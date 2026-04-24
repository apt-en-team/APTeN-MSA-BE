package com.apten.auth.application.service;

import com.apten.auth.application.mapper.AuthUserMapper;
import com.apten.auth.application.model.request.AuthCheckEmailReq;
import com.apten.auth.application.model.request.AuthLoginPostReq;
import com.apten.auth.application.model.request.AuthPasswordForgotPostReq;
import com.apten.auth.application.model.request.AuthPasswordResetPostReq;
import com.apten.auth.application.model.request.AuthRegisterPostReq;
import com.apten.auth.application.model.request.AuthSmsSendPostReq;
import com.apten.auth.application.model.request.AuthSmsVerifyPostReq;
import com.apten.auth.application.model.request.AuthSocialSignupPostReq;
import com.apten.auth.application.model.request.AuthTokenRefreshPostReq;
import com.apten.auth.application.model.response.AuthCheckEmailRes;
import com.apten.auth.application.model.response.AuthLoginPostRes;
import com.apten.auth.application.model.response.AuthLogoutPostRes;
import com.apten.auth.application.model.response.AuthPasswordForgotPostRes;
import com.apten.auth.application.model.response.AuthPasswordResetPostRes;
import com.apten.auth.application.model.response.AuthRegisterPostRes;
import com.apten.auth.application.model.response.AuthSmsSendPostRes;
import com.apten.auth.application.model.response.AuthSmsVerifyPostRes;
import com.apten.auth.application.model.response.AuthSocialSignupPostRes;
import com.apten.auth.application.model.response.AuthTokenRefreshPostRes;
import com.apten.auth.domain.entity.User;
import com.apten.auth.domain.enums.SignupType;
import com.apten.auth.domain.enums.UserRole;
import com.apten.auth.domain.enums.UserStatus;
import com.apten.auth.domain.repository.UserRepository;
import com.apten.auth.exception.AuthErrorCode;
import com.apten.auth.infrastructure.kafka.AuthOutboxService;
import com.apten.auth.infrastructure.mapper.AuthQueryMapper;
import com.apten.auth.infrastructure.sms.CoolsmsClient;
import com.apten.auth.security.JwtTokenProvider;
import com.apten.auth.security.UserPrincipal;
import com.apten.common.exception.BusinessException;
import com.apten.common.security.UserContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 인증 응용 서비스 — 로그인, 회원가입, 토큰, 비밀번호, SMS 인증 처리
@Service
@RequiredArgsConstructor
public class AuthService {

    // 회원 기본 저장소
    private final UserRepository userRepository;

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

    // 이메일 로그인 서비스
    public AuthLoginPostRes login(AuthLoginPostReq request) {
        // TODO: 이메일 로그인 로직 구현
        return AuthLoginPostRes.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .userId(1L)
                .name(request.getEmail())
                .role(UserRole.USER.getValue())
                .status(UserStatus.ACTIVE.getValue())
                .build();
    }

    // 로그아웃 서비스
    public AuthLogoutPostRes logout() {
        // TODO: Refresh Token 무효화 처리
        // TODO: Access Token 블랙리스트 등록 처리
        return AuthLogoutPostRes.builder()
                .message("로그아웃 완료")
                .build();
    }

    // 이메일 회원가입 서비스
    @Transactional
    public AuthRegisterPostRes register(AuthRegisterPostReq request) {
        // 회원 원본을 먼저 저장해 aggregateId로 사용할 Long PK를 확보
        User user = User.builder()
                .complexId(resolveComplexId(request.getApartmentComplexUid()))
                .email(request.getEmail())
                .passwordHash(null)
                .name(request.getName())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .building(request.getDong())
                .unit(request.getHo())
                .role(UserRole.USER)
                .status(UserStatus.PENDING)
                .signupType(SignupType.EMAIL)
                .isPhoneVerified(false)
                .isEmailVerified(false)
                .loginFailCount(0)
                .isDeleted(false)
                .build();
        User savedUser = userRepository.save(user);

        // Kafka 전송은 relay가 담당하므로 같은 트랜잭션 안에서는 Outbox row만 남김
        authOutboxService.saveCreatedEvent(savedUser);

        // TODO: 회원가입 요청 이벤트는 세대 매칭 명세 확정 후 별도 Outbox 이벤트로 분리
        return AuthRegisterPostRes.builder()
                .apartmentComplexUid(request.getApartmentComplexUid())
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
                .complexId(resolveComplexId(request.getApartmentComplexUid()))
                .email(request.getEmail())
                .passwordHash(null)
                .name(request.getName())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .building(request.getDong())
                .unit(request.getHo())
                .role(UserRole.USER)
                .status(UserStatus.PENDING)
                .signupType(SignupType.valueOf(request.getProvider().name()))
                .isPhoneVerified(false)
                .isEmailVerified(false)
                .loginFailCount(0)
                .isDeleted(false)
                .build();
        User savedUser = userRepository.save(user);

        // Kafka 직접 발행 대신 생성 이벤트를 같은 트랜잭션 안에서 Outbox에 적재
        authOutboxService.saveCreatedEvent(savedUser);

        // TODO: 회원가입 요청 이벤트는 세대 매칭 명세 확정 후 별도 Outbox 이벤트로 분리
        return AuthSocialSignupPostRes.builder()
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
    public AuthTokenRefreshPostRes refreshToken(AuthTokenRefreshPostReq request) {
        // TODO: Refresh Token 검증 및 재발급 로직 구현
        return AuthTokenRefreshPostRes.builder()
                .accessToken("refreshed-access-token")
                .refreshToken(request.getRefreshToken())
                .build();
    }

    // 비밀번호 재설정 메일 발송 서비스
    public AuthPasswordForgotPostRes sendPasswordResetMail(AuthPasswordForgotPostReq request) {
        // TODO: 비밀번호 재설정 메일 발송 로직 구현
        return AuthPasswordForgotPostRes.builder()
                .message("비밀번호 재설정 메일 발송 완료")
                .build();
    }

    // 비밀번호 재설정 서비스
    public AuthPasswordResetPostRes resetPassword(AuthPasswordResetPostReq request) {
        // TODO: 비밀번호 재설정 로직 구현
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

    // SMS 인증번호 검증 서비스
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

        // 인증 성공 후 즉시 삭제 — 재사용 방지
        redisTemplate.delete("sms:" + request.getPhone());

        return AuthSmsVerifyPostRes.builder()
                .verified(true)
                .build();
    }

    // 이메일 중복 확인 서비스
    public AuthCheckEmailRes checkEmailDuplicate(AuthCheckEmailReq request) {
        // TODO: 이메일 중복 확인 로직 구현
        return AuthCheckEmailRes.builder()
                .isDuplicate(false)
                .build();
    }

    // OAuth2 성공 핸들러가 호출하는 JWT 응답 생성 서비스
    public AuthLoginPostRes issueTokenResponse(UserPrincipal userPrincipal) {
        UserContext userContext = authUserMapper.toUserContext(userPrincipal);
        String accessToken = jwtTokenProvider.issueAccessToken(userContext);
        String refreshToken = jwtTokenProvider.issueRefreshToken(userPrincipal.getUserId());
        return authUserMapper.toLoginResponse(accessToken, refreshToken, userPrincipal);
    }

    // TODO: 내 계정 정보 조회는 API 명세 확정 후 추가
    // TODO: 내 계정 정보 수정은 API 명세 확정 후 추가
    // TODO: 회원 상태 변경 이벤트 수신 후 ACTIVE 또는 REJECTED 반영

    // 단지 UID를 Long으로 변환 — 매핑 확정 전까지 숫자 UID만 허용
    private Long resolveComplexId(String apartmentComplexUid) {
        try {
            return Long.valueOf(apartmentComplexUid);
        } catch (NumberFormatException exception) {
            return 0L;
        }
    }
}