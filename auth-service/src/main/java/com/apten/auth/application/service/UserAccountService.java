package com.apten.auth.application.service;

import com.apten.auth.application.model.request.UserDeleteReq;
import com.apten.auth.application.model.request.UserPasswordPatchReq;
import com.apten.auth.application.model.response.UserDeleteRes;
import com.apten.auth.application.model.response.UserPasswordPatchRes;
import com.apten.auth.domain.entity.User;
import com.apten.auth.domain.repository.UserRepository;
import com.apten.auth.exception.AuthErrorCode;
import com.apten.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
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
    public UserDeleteRes deleteMyAccount(UserDeleteReq request) {
        // TODO: 회원 소프트 삭제 로직 구현
        return UserDeleteRes.builder()
                .message("회원 탈퇴 완료")
                .build();
    }
}
