package com.apten.auth.application.service;

import com.apten.auth.application.mapper.AuthUserMapper;
import com.apten.auth.application.model.request.*;
import com.apten.auth.application.model.response.*;
import com.apten.auth.domain.entity.AdminProfile;
import com.apten.auth.domain.entity.LoginHistory;
import com.apten.auth.domain.entity.ResidentProfile;
import com.apten.auth.domain.entity.User;
import com.apten.auth.domain.enums.LoginResult;
import com.apten.auth.domain.enums.SignupType;
import com.apten.auth.domain.enums.UserRole;
import com.apten.auth.domain.enums.UserStatus;
import com.apten.auth.domain.repository.AdminProfileRepository;
import com.apten.auth.domain.repository.ComplexCacheRepository;
import com.apten.auth.domain.repository.LoginHistoryRepository;
import com.apten.auth.domain.repository.ResidentProfileRepository;
import com.apten.auth.domain.repository.UserRepository;
import com.apten.auth.exception.AuthErrorCode;
import com.apten.auth.infrastructure.kafka.AuthOutboxService;
import com.apten.auth.infrastructure.mail.MailService;
import com.apten.auth.infrastructure.mapper.AuthQueryMapper;
import com.apten.auth.infrastructure.sms.CoolsmsClient;
import com.apten.auth.security.JwtTokenProvider;
import com.apten.auth.security.UserPrincipal;
import com.apten.common.exception.BusinessException;
import com.apten.common.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.apten.auth.domain.entity.ResidentProfile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

// 인증 응용 서비스 — 로그인, 회원가입, 토큰, 비밀번호, SMS 인증 처리
@Service
@RequiredArgsConstructor
public class AuthService {

    // 회원 기본 저장소
    private final UserRepository userRepository;

    // MANAGER / ADMIN 단지 소속 정보 저장소
    private final AdminProfileRepository adminProfileRepository;

    // USER 단지 소속 정보 저장소
    private final ResidentProfileRepository residentProfileRepository;

    // 회원가입 시 선택한 complexId를 검증하는 단지 캐시 저장소
    private final ComplexCacheRepository complexCacheRepository;

    // 인증 조회용 MyBatis 매퍼
    private final ObjectProvider<AuthQueryMapper> authQueryMapperProvider;

    // principal과 응답 변환기
    private final AuthUserMapper authUserMapper;

    // JWT 발급기
    private final JwtTokenProvider jwtTokenProvider;

    // 사용자 원본 변경 이벤트를 Kafka 직접 발행 대신 Outbox에 저장하는 서비스
    private final AuthOutboxService authOutboxService;

    // SMS 인증코드 및 토큰 임시 저장소
    private final RedisTemplate<String, String> redisTemplate;

    // Coolsms 문자 발송 클라이언트
    private final CoolsmsClient coolsmsClient;

    // 비밀번호 단방향 암호화
    private final BCryptPasswordEncoder passwordEncoder;

    // 비밀번호 재설정 링크를 이메일로 발송하는 서비스
    private final MailService mailService;

    // 로그인 이력 저장소
    private final LoginHistoryRepository loginHistoryRepository;

    // 이메일 로그인 서비스
    @Transactional
    public AuthLoginPostRes login(AuthLoginPostReq request) {
        // 이메일로 회원 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

        // 계정 잠금 상태 확인 — 5회 실패 시 잠금 처리된 계정
        if (user.getStatus() == UserStatus.LOCKED) {
            throw new BusinessException(AuthErrorCode.ACCOUNT_LOCKED);
        }

        // 계정 활성화 상태 확인 — PENDING, REJECTED, DELETED 계정 차단
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(AuthErrorCode.ACCOUNT_NOT_ACTIVE);
        }

        // 비밀번호 검증 실패 시 실패 횟수 증가 (5회 시 자동 잠금)
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            user.incrementLoginFailCount();
            // 로그인 실패 이력 저장
            loginHistoryRepository.save(LoginHistory.builder()
                    .userId(user.getId())
                    .email(request.getEmail())
                    .result(LoginResult.FAIL)
                    .build());
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        // 로그인 성공 — 실패 횟수 초기화 + 마지막 로그인 시각 갱신
        user.resetLoginFailCount();

        // 로그인 성공 이력 저장
        loginHistoryRepository.save(LoginHistory.builder()
                .userId(user.getId())
                .email(request.getEmail())
                .result(LoginResult.SUCCESS)
                .build());

        // auth.UserRole -> common.UserRole 변환
        com.apten.common.security.UserRole commonRole = user.getRole().toCommonUserRole();

        // role에 따라 complexId 조회 분기
        // MASTER는 null, MANAGER/ADMIN은 admin_profile, USER는 user 테이블
        Long complexId = resolveComplexIdByRole(user);

        UserContext userContext = UserContext.builder()
                .userId(user.getId())
                .userRole(commonRole)
                .complexId(complexId)
                .build();

        // JWT 발급
        String accessToken = jwtTokenProvider.issueAccessToken(userContext);
        String refreshToken = jwtTokenProvider.issueRefreshToken(user.getId());

        // Redis에 RefreshToken 저장 — key: refresh:{userId}, TTL: 14일
        redisTemplate.opsForValue().set(
                "refresh:" + user.getId(),
                refreshToken,
                Duration.ofDays(14)
        );

        // USER 역할인 경우에만 resident_profile에서 동/호 조회
        String building = null;
        String unit = null;
        if (user.getRole() == UserRole.USER) {
            building = residentProfileRepository.findByUserId(user.getId())
                    .map(ResidentProfile::getBuilding)
                    .orElse(null);
            unit = residentProfileRepository.findByUserId(user.getId())
                    .map(ResidentProfile::getUnit)
                    .orElse(null);
        }

        return AuthLoginPostRes.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .userUid(String.valueOf(user.getId()))
                .name(user.getName())
                .role(user.getRole().name()) // "USER"
                .status(user.getStatus().name()) // "ACTIVE"
                .building(building)  // 입주민 동
                .unit(unit) // 입주민 호
                .build();
    }

    // 로그아웃 서비스
    @Transactional
    public AuthLogoutPostRes logout(String authorizationHeader) {
        // Authorization 헤더에서 토큰 추출
        String accessToken = jwtTokenProvider.resolveToken(authorizationHeader);

        // JWT 파싱해서 만료 시각 계산
        Long userId = jwtTokenProvider.getUserId(accessToken);

        // AT 남은 유효시간 계산 — 블랙리스트 TTL로 사용
        // AT가 만료되면 Gateway에서 이미 차단되므로 남은 시간만큼만 저장
        long remainMs = jwtTokenProvider.getExpiration(accessToken).getTime() - System.currentTimeMillis();
        if (remainMs > 0) {
            redisTemplate.opsForValue().set(
                    "blacklist:" + accessToken,
                    "logout",
                    Duration.ofMillis(remainMs)
            );
        }

        // Redis에서 RefreshToken 삭제
        redisTemplate.delete("refresh:" + userId);

        return AuthLogoutPostRes.builder()
                .message("로그아웃 완료")
                .build();
    }

    // 이메일 회원가입 서비스
    @Transactional
    public AuthRegisterPostRes register(AuthRegisterPostReq request) {
        // SMS 인증 완료 여부 확인 — verifySmsCode() 호출 시 저장한 플래그 조회
        // "true"가 아니면 인증 미완료 또는 TTL 만료로 판단
        String verified = redisTemplate.opsForValue().get("sms:verified:" + request.getPhone());
        if (!"true".equals(verified)) {
            throw new BusinessException(AuthErrorCode.SMS_CODE_EXPIRED);
        }

        // 인증 완료 플래그 삭제 — 재사용 방지
        redisTemplate.delete("sms:verified:" + request.getPhone());

        // 이메일 중복 확인
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호 BCrypt 암호화
        String passwordHash = passwordEncoder.encode(request.getPassword());

        // 회원 원본 저장 — aggregateId로 사용할 Long PK 확보
        User user = User.builder()
//                .complexId(resolveComplexId(request.getApartmentComplexUid()))
                .email(request.getEmail())
                .passwordHash(passwordHash)
                .name(request.getName())
                .phone(request.getPhone())
//                .birthDate(request.getBirthDate())
//                .building(request.getDong())
//                .unit(request.getHo())
                .role(UserRole.USER)
                .status(UserStatus.PENDING)
                .signupType(SignupType.EMAIL)
                .isPhoneVerified(true)  // SMS 인증 완료
                .isEmailVerified(false)
                .loginFailCount(0)
                .isDeleted(false)
                .build();
        User savedUser = userRepository.save(user);
        ResidentProfile resident = ResidentProfile.builder()
                .userId(savedUser.getId())
                // resident_profile.complex_id에는 검증된 complexId를 저장한다.
                .complexId(resolveComplexId(request.getComplexId()))
                .birthDate(request.getBirthDate())
                .building(request.getDong())
                .unit(request.getHo())
                .status(UserStatus.PENDING)
                .build();
        ResidentProfile savedResident = residentProfileRepository.save(resident);

        Long complexId = savedResident.getComplexId();

        // Kafka 직접 발행 대신 생성 이벤트를 같은 트랜잭션 안에서 Outbox에 적재
        authOutboxService.saveCreatedEvent(savedUser, complexId);

        return AuthRegisterPostRes.builder()
                .complexId(savedResident.getComplexId())
                .userId(savedUser.getId())
                .userUid(String.valueOf(savedUser.getId()))
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role(savedUser.getRole().getValue())
                .status(savedUser.getStatus().getValue())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 소셜 회원가입 서비스
    @Transactional
    public AuthSocialSignupPostRes socialSignup(AuthSocialSignupPostReq request) {
        // 소셜 가입도 user 원본을 먼저 저장해 Outbox aggregateId로 사용할 PK를 확보
        User user = User.builder()
//                .complexId(resolveComplexId(request.getApartmentComplexUid()))
                .email(request.getEmail())
                .passwordHash(null)
                .name(request.getName())
                .phone(request.getPhone())
//                .birthDate(request.getBirthDate())
//                .building(request.getDong())
//                .unit(request.getHo())
                .role(UserRole.USER)
                .status(UserStatus.PENDING)
                .signupType(SignupType.valueOf(request.getProvider().name()))
                .isPhoneVerified(false)
                .isEmailVerified(false)
                .loginFailCount(0)
                .isDeleted(false)
                .build();
        User savedUser = userRepository.save(user);
        ResidentProfile resident = ResidentProfile.builder()
                .userId(savedUser.getId())
                // apartmentComplexUid 호환 입력이 와도 내부 저장은 complexId 기준으로 처리한다.
                .complexId(resolveComplexId(request.getComplexId()))
                .birthDate(request.getBirthDate())
                .building(request.getDong())
                .unit(request.getHo())
                .status(UserStatus.PENDING)
                .build();
        ResidentProfile savedResident = residentProfileRepository.save(resident);

        Long complexId = savedResident.getComplexId();

        // Kafka 직접 발행 대신 생성 이벤트를 같은 트랜잭션 안에서 Outbox에 적재
        authOutboxService.saveCreatedEvent(savedUser, complexId);

        return AuthSocialSignupPostRes.builder()
                .complexId(savedResident.getComplexId())
                .userId(savedUser.getId())
                .userUid(String.valueOf(savedUser.getId()))
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role(savedUser.getRole().getValue())
                .status(savedUser.getStatus().getValue())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 토큰 재발급 서비스
    @Transactional
    public AuthTokenRefreshPostRes refreshToken(AuthTokenRefreshPostReq request) {
        String refreshToken = request.getRefreshToken();

        // RT 유효성 검증 — 만료 또는 위조 시 예외
        jwtTokenProvider.validateToken(refreshToken);

        // RT에서 userId 추출 — RT에는 role claim 없음
        Long userId = jwtTokenProvider.getUserId(refreshToken);

        // Redis에서 저장된 RT 조회
        String storedRT = redisTemplate.opsForValue().get("refresh:" + userId);

        // Redis에 없으면 로그아웃된 상태
        if (storedRT == null) {
            throw new BusinessException(AuthErrorCode.REFRESH_TOKEN_INVALID);
        }

        // 요청 RT와 Redis RT 불일치 — 토큰 탈취 의심
        if (!storedRT.equals(refreshToken)) {
            // 기존 RT 무효화
            redisTemplate.delete("refresh:" + userId);
            throw new BusinessException(AuthErrorCode.REFRESH_TOKEN_INVALID);
        }

        // DB에서 최신 User 조회 — 재발급 시점의 최신 role, status 반영
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

        // 계정 상태 확인
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(AuthErrorCode.ACCOUNT_NOT_ACTIVE);
        }

        // 최신 정보로 새 AT 발급
        com.apten.common.security.UserRole commonRole = user.getRole().toCommonUserRole();

        // role에 따라 complexId 조회 분기
        // MASTER는 null, MANAGER/ADMIN은 admin_profile, USER는 user 테이블
        Long complexId = resolveComplexIdByRole(user);

        UserContext userContext = UserContext.builder()
                .userId(userId)
                .userRole(commonRole)
                .complexId(complexId)
                .build();
        String newAccessToken = jwtTokenProvider.issueAccessToken(userContext);

        // Refresh Token Rotation — 새 RT 발급 후 Redis 갱신
        // 매번 새 RT를 발급해 기존 RT 재사용 공격 방지
        String newRefreshToken = jwtTokenProvider.issueRefreshToken(userId);
        redisTemplate.opsForValue().set(
                "refresh:" + userId,
                newRefreshToken,
                Duration.ofDays(14)
        );

        return AuthTokenRefreshPostRes.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    // 비밀번호 재설정 메일 발송 서비스
    @Transactional
    public AuthPasswordForgotPostRes sendPasswordResetMail(AuthPasswordForgotPostReq request) {
        // 계정 존재 여부 노출 방지 — 없어도 성공 응답
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {

            // UUID 토큰 생성 (충분히 랜덤 — BCrypt 불필요)
            String rawToken = java.util.UUID.randomUUID().toString();

            // Redis 저장 — key: reset:{token}, value: userId, TTL: 30분
            redisTemplate.opsForValue().set(
                    "reset:" + rawToken,
                    String.valueOf(user.getId()),
                    Duration.ofMinutes(30)
            );

            // 재설정 링크 발송
            // 나중에는 배포 서버로 고쳐야함
            String resetLink = "http://localhost:5173/reset-password?token=" + rawToken;
            mailService.sendPasswordResetMail(user.getEmail(), resetLink);
        });

        return AuthPasswordForgotPostRes.builder()
                .message("비밀번호 재설정 메일 발송 완료")
                .build();
    }

    // 비밀번호 재설정 서비스
    @Transactional
    public AuthPasswordResetPostRes resetPassword(AuthPasswordResetPostReq request) {
        // Redis에서 토큰으로 userId 조회
        String userIdStr = redisTemplate.opsForValue().get("reset:" + request.getToken());

        if (userIdStr == null) {
            // null이면 TTL 만료 또는 없는 토큰
            throw new BusinessException(AuthErrorCode.RESET_TOKEN_INVALID);
        }

        Long userId = Long.valueOf(userIdStr);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

        // 새 비밀번호 저장
        user.changePassword(passwordEncoder.encode(request.getNewPassword()));

        // 토큰 즉시 삭제 — 재사용 방지
        redisTemplate.delete("reset:" + request.getToken());

        return AuthPasswordResetPostRes.builder()
                .message("비밀번호 재설정 완료")
                .build();
    }

    // SMS 인증번호 발송 서비스
    public AuthSmsSendPostRes sendSmsCode(AuthSmsSendPostReq request) {
        // 6자리 랜덤 인증코드 생성
        String code = String.format("%06d",
                ThreadLocalRandom.current().nextInt(0, 1_000_000));

        // Redis 저장 — key: sms:{phone}, TTL: 5분 후 자동 삭제
        redisTemplate.opsForValue().set(
                "sms:" + request.getPhone(),
                code,
                Duration.ofMinutes(5)
        );

        // Coolsms로 실제 문자 발송
        coolsmsClient.send(request.getPhone(), code);

        return AuthSmsSendPostRes.builder()
                .message("SMS 인증번호 발송 완료")
                .build();
    }

    // SMS 인증번호 검증 서비스 — 회원가입 외 단독 인증에 사용
    public AuthSmsVerifyPostRes verifySmsCode(AuthSmsVerifyPostReq request) {
        String storedCode = redisTemplate.opsForValue()
                .get("sms:" + request.getPhone());

        // Redis에 없으면 TTL 만료 — 인증코드 유효시간 초과
        if (storedCode == null) {
            throw new BusinessException(AuthErrorCode.SMS_CODE_EXPIRED);
        }

        // 입력값과 저장값 불일치
        if (!storedCode.equals(request.getCode())) {
            throw new BusinessException(AuthErrorCode.SMS_CODE_INVALID);
        }

        // 인증 성공 후 SMS 코드 즉시 삭제 — 재사용 방지
        redisTemplate.delete("sms:" + request.getPhone());

        // 인증 완료 플래그 저장 — register() 호출 시 인증 여부 확인용
        // TTL 5분: 인증 완료 후 5분 내에 회원가입을 완료해야 함
        redisTemplate.opsForValue().set(
                "sms:verified:" + request.getPhone(),
                "true",
                Duration.ofMinutes(5)
        );

        return AuthSmsVerifyPostRes.builder()
                .verified(true)
                .build();
    }

    // 이메일 중복 확인 서비스
    public AuthCheckEmailRes checkEmailDuplicate(AuthCheckEmailReq request) {
        boolean isDuplicate = userRepository.findByEmail(request.getEmail()).isPresent();
        return AuthCheckEmailRes.builder()
                .isDuplicate(isDuplicate)
                .build();
    }

    // OAuth2 성공 핸들러가 호출하는 JWT 응답 생성 서비스
    @Transactional
    public AuthLoginPostRes issueTokenResponse(UserPrincipal userPrincipal) {
        UserContext userContext = authUserMapper.toUserContext(userPrincipal);
        String accessToken = jwtTokenProvider.issueAccessToken(userContext);
        String refreshToken = jwtTokenProvider.issueRefreshToken(userPrincipal.getUserId());

        // Redis에 RefreshToken 저장 — key: refresh:{userId}, TTL: 14일
        redisTemplate.opsForValue().set(
                "refresh:" + userPrincipal.getUserId(),
                refreshToken,
                Duration.ofDays(14)
        );

        return authUserMapper.toLoginResponse(accessToken, refreshToken, userPrincipal);
    }

    // role에 따라 complexId 조회 분기
    // MASTER → null, MANAGER/ADMIN → admin_profile 조회, USER → resident_profile 조회
    private Long resolveComplexIdByRole(User user) {
        if (user.getRole() == UserRole.MASTER) {
            // MASTER는 전체 단지 접근 — complexId 없음
            return null;
        }
        if (user.getRole() == UserRole.MANAGER || user.getRole() == UserRole.ADMIN) {
            // MANAGER / ADMIN은 admin_profile에서 소속 단지 조회
            return adminProfileRepository.findByUserId(user.getId())
                    .map(AdminProfile::getComplexId)
                    .orElse(null);
        }
        // USER(입주민)는 resident_profile에서 소속 단지 조회
        return residentProfileRepository.findByUserId(user.getId())
                .map(ResidentProfile::getComplexId)
                .orElse(null);
    }

    // 회원가입에서 받은 complexId는 auth complex_cache 존재 여부까지 검증한다.
    private Long resolveComplexId(Long complexId) {
        if (complexId == null) {
            throw new BusinessException(AuthErrorCode.INVALID_PARAMETER);
        }

        return complexCacheRepository.findById(complexId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_PARAMETER))
                .getId();
    }
}
