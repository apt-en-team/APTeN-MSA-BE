package com.apten.auth.application.service;

import com.apten.auth.application.model.request.UserDeleteReq;
import com.apten.auth.application.model.request.UserPasswordPatchReq;
import com.apten.auth.application.model.request.UserPatchReq;
import com.apten.auth.application.model.response.UserDeleteRes;
import com.apten.auth.application.model.response.UserMeRes;
import com.apten.auth.application.model.response.UserPasswordPatchRes;
import com.apten.auth.application.model.response.UserPatchRes;
import com.apten.auth.domain.entity.User;
import com.apten.auth.domain.repository.UserRepository;
import com.apten.auth.exception.AuthErrorCode;
import com.apten.auth.infrastructure.kafka.AuthOutboxService;
import com.apten.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 로그인 사용자 계정 관리 서비스
// 비밀번호 변경과 회원 탈퇴 시그니처를 이 서비스에 모아둔다
@Service
@RequiredArgsConstructor
public class UserAccountService {
    // 회원 저장소
    private final UserRepository userRepository;

    // 비밀번호 암호화
    private final BCryptPasswordEncoder passwordEncoder;

    // Redis — RT 제거용
    private final RedisTemplate<String, String> redisTemplate;

    // Outbox 이벤트 발행 — 탈퇴 후 user_cache 동기화
    private final AuthOutboxService authOutboxService;

    // 내 비밀번호 변경
    // X-User-Id 헤더로 받은 userId로 본인을 식별하고 현재 비밀번호 검증 후 새 비밀번호로 교체한다
    @Transactional
    public UserPasswordPatchRes changePassword(Long userId, UserPasswordPatchReq request) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

        // 소셜 전용 계정은 비밀번호 없음
        if (user.getPasswordHash() == null) {
            throw new BusinessException(AuthErrorCode.SOCIAL_ACCOUNT_NO_PASSWORD);
        }

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        // 새 비밀번호 저장
        user.changePassword(passwordEncoder.encode(request.getNewPassword()));

        return UserPasswordPatchRes.builder()
                .message("비밀번호 변경 완료")
                .build();
    }

    // 회원 탈퇴 서비스
    // 소셜 전용 계정은 비밀번호 검증 없이 탈퇴 가능
    @Transactional
    public UserDeleteRes deleteMyAccount(Long userId, UserDeleteReq request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

        // 비밀번호가 있는 계정만 비밀번호 검증
        if (user.getPasswordHash() != null) {
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
            }
        }

        // 소프트 삭제 — DB 행 유지, status = DELETED
        user.softDelete();

        // Redis RT 제거 — 즉시 로그아웃 효과
        redisTemplate.delete("refresh:" + userId);

        // USER_UPDATED 이벤트 발행 — 다른 서비스의 user_cache 동기화
        authOutboxService.saveUpdatedEvent(user);

        return UserDeleteRes.builder()
                .message("회원 탈퇴 완료")
                .build();
    }

    // 내 계정 정보 조회
    // X-User-Id 헤더로 받은 userId로 본인 정보를 반환한다
    @Transactional(readOnly = true)
    public UserMeRes getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

        return UserMeRes.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .building(user.getBuilding())
                .unit(user.getUnit())
                .role(user.getRole().getValue())
                .status(user.getStatus().getValue())
                .signupType(user.getSignupType().name())
                .complexId(user.getComplexId())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // 내 계정 정보 수정
    @Transactional
    public UserPatchRes updateMyInfo(Long userId, UserPatchReq request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

        // 변경 항목만 업데이트 — null이면 기존값 유지
        user.updateProfile(request.getName(), request.getPhone());

        // Outbox 이벤트 발행 — 다른 서비스 user_cache 동기화
        authOutboxService.saveUpdatedEvent(user);

        return UserPatchRes.builder()
                .name(user.getName())
                .phone(user.getPhone())
                .message("계정 정보 수정 완료")
                .build();
    }
}
