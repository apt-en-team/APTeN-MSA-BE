package com.apten.auth.application.service;

import com.apten.auth.application.model.request.AdminCreateReq;
import com.apten.auth.application.model.response.AdminCreateRes;
import com.apten.auth.domain.entity.AdminProfile;
import com.apten.auth.domain.entity.User;
import com.apten.auth.domain.enums.SignupType;
import com.apten.auth.domain.enums.UserRole;
import com.apten.auth.domain.enums.UserStatus;
import com.apten.auth.domain.repository.AdminProfileRepository;
import com.apten.auth.domain.repository.UserRepository;
import com.apten.auth.exception.AuthErrorCode;
import com.apten.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// MANAGER / ADMIN 계정 생성 서비스
// MASTER → MANAGER 생성, MANAGER → ADMIN 생성 흐름을 처리한다
@Service
@RequiredArgsConstructor
public class AdminService {

    // 회원 기본 저장소
    private final UserRepository userRepository;

    // MANAGER / ADMIN 단지 소속 정보 저장소
    private final AdminProfileRepository adminProfileRepository;

    // 비밀번호 단방향 암호화
    private final BCryptPasswordEncoder passwordEncoder;

    // MASTER가 MANAGER 계정 생성
    // createdBy → MASTER의 userId
    @Transactional
    public AdminCreateRes createManager(Long masterUserId, AdminCreateReq request) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호 BCrypt 암호화
        String passwordHash = passwordEncoder.encode(request.getPassword());

        // user 테이블에 MANAGER 계정 저장
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordHash)
                .name(request.getName())
                .role(UserRole.MANAGER)
                // MASTER가 생성한 계정 — 별도 승인 없이 바로 ACTIVE
                .status(UserStatus.ACTIVE)
                .signupType(SignupType.CREATED_BY_ADMIN)
                .isPhoneVerified(false)
                .isEmailVerified(false)
                .loginFailCount(0)
                .isDeleted(false)
                .build();
        User savedUser = userRepository.save(user);

        // admin_profile 테이블에 단지 소속 정보 저장
        AdminProfile adminProfile = AdminProfile.builder()
                .userId(savedUser.getId())
                .complexId(request.getComplexId())
                .adminRole(UserRole.MANAGER)
                // 이 계정을 생성한 사람은 MASTER
                .createdBy(masterUserId)
                .build();
        adminProfileRepository.save(adminProfile);

        return AdminCreateRes.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role(savedUser.getRole().getValue())
                .complexId(request.getComplexId())
                .build();
    }

    // MANAGER가 ADMIN 계정 생성
    // createdBy → MANAGER의 userId
    // complexId → MANAGER 본인 단지로 강제 (다른 단지 ADMIN 생성 불가)
    @Transactional
    public AdminCreateRes createAdmin(Long managerUserId, AdminCreateReq request) {
        // MANAGER의 단지 ID 조회 — 요청의 complexId 무시하고 MANAGER 본인 단지로 강제
        AdminProfile managerProfile = adminProfileRepository.findByUserId(managerUserId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

        // 이메일 중복 확인
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호 BCrypt 암호화
        String passwordHash = passwordEncoder.encode(request.getPassword());

        // user 테이블에 ADMIN 계정 저장
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordHash)
                .name(request.getName())
                .role(UserRole.ADMIN)
                // MANAGER가 생성한 계정 — 별도 승인 없이 바로 ACTIVE
                .status(UserStatus.ACTIVE)
                .signupType(SignupType.CREATED_BY_ADMIN)
                .isPhoneVerified(false)
                .isEmailVerified(false)
                .loginFailCount(0)
                .isDeleted(false)
                .build();
        User savedUser = userRepository.save(user);

        // admin_profile 테이블에 단지 소속 정보 저장
        // complexId는 MANAGER 본인 단지로 강제 설정
        AdminProfile adminProfile = AdminProfile.builder()
                .userId(savedUser.getId())
                .complexId(managerProfile.getComplexId())
                .adminRole(UserRole.ADMIN)
                // 이 계정을 생성한 사람은 MANAGER
                .createdBy(managerUserId)
                .build();
        adminProfileRepository.save(adminProfile);

        return AdminCreateRes.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role(savedUser.getRole().getValue())
                .complexId(managerProfile.getComplexId())
                .build();
    }
}