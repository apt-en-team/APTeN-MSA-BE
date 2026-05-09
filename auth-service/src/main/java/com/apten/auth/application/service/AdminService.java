package com.apten.auth.application.service;

import com.apten.auth.application.model.request.AdminCreateReq;
import com.apten.auth.application.model.request.AdminPatchReq;
import com.apten.auth.application.model.request.InternalAdminCreateReq;
import com.apten.auth.application.model.request.InternalAdminPatchReq;
import com.apten.auth.application.model.response.AdminCreateRes;
import com.apten.auth.application.model.response.AdminDeleteRes;
import com.apten.auth.application.model.response.AdminPatchRes;
import com.apten.auth.application.model.response.InternalAdminCreateRes;
import com.apten.auth.application.model.response.InternalAdminDeleteRes;
import com.apten.auth.application.model.response.InternalAdminPatchRes;
import com.apten.auth.domain.entity.AdminProfile;
import com.apten.auth.domain.entity.User;
import com.apten.auth.domain.enums.AdminProfileStatus;
import com.apten.auth.domain.enums.SignupType;
import com.apten.auth.domain.enums.UserRole;
import com.apten.auth.domain.enums.UserStatus;
import com.apten.auth.domain.repository.AdminProfileRepository;
import com.apten.auth.domain.repository.UserRepository;
import com.apten.auth.exception.AuthErrorCode;
import com.apten.auth.infrastructure.kafka.AuthOutboxService;
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

    private static final String PASSWORD_POLICY_REGEX =
            "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";

    // 회원 기본 저장소
    private final UserRepository userRepository;

    // MANAGER / ADMIN 단지 소속 정보 저장소
    private final AdminProfileRepository adminProfileRepository;

    // 비밀번호 단방향 암호화
    private final BCryptPasswordEncoder passwordEncoder;

    //추가 관리자 계정 변경 사항을 user cache로 전파한다.
    private final AuthOutboxService authOutboxService;

    //추가 MASTER가 MANAGER 계정을 생성한다.
    @Transactional
    public AdminCreateRes createManagerByMaster(Long masterUserId, String callerRole, AdminCreateReq request) {
        //수정 MASTER만 MANAGER 생성 가능하도록 권한을 검증한다.
        User master = validateActor(masterUserId, callerRole, UserRole.MASTER);
        User savedUser = createAdminAccount(request.getEmail(), request.getPassword(), request.getName(), request.getPhone(),
                request.getComplexId(), UserRole.MANAGER, master.getId());

        return toAdminCreateResponse(savedUser, request.getComplexId());
    }

    //추가 MASTER가 선택 단지에 ADMIN 계정을 생성한다.
    @Transactional
    public AdminCreateRes createAdminByMaster(Long masterUserId, String callerRole, AdminCreateReq request) {
        //수정 MASTER만 ADMIN 생성 가능하도록 권한을 검증한다.
        User master = validateActor(masterUserId, callerRole, UserRole.MASTER);
        User savedUser = createAdminAccount(request.getEmail(), request.getPassword(), request.getName(), request.getPhone(),
                request.getComplexId(), UserRole.ADMIN, master.getId());

        return toAdminCreateResponse(savedUser, request.getComplexId());
    }

    //수정 MANAGER는 본인 단지에만 ADMIN을 생성할 수 있다.
    @Transactional
    public AdminCreateRes createAdminByManager(Long managerUserId, String callerRole, AdminCreateReq request) {
        User manager = validateActor(managerUserId, callerRole, UserRole.MANAGER);
        AdminProfile managerProfile = getAdminProfile(managerUserId);

        User savedUser = createAdminAccount(request.getEmail(), request.getPassword(), request.getName(), request.getPhone(),
                managerProfile.getComplexId(), UserRole.ADMIN, manager.getId());

        return toAdminCreateResponse(savedUser, managerProfile.getComplexId());
    }

    //추가 MASTER가 MANAGER 또는 ADMIN 계정을 수정한다.
    @Transactional
    public AdminPatchRes updateAdminByMaster(Long masterUserId, String callerRole, Long userId, AdminPatchReq request) {
        validateActor(masterUserId, callerRole, UserRole.MASTER);
        return updateAdmin(userId, request, true, null);
    }

    //추가 MANAGER가 같은 단지의 ADMIN 계정만 수정한다.
    @Transactional
    public AdminPatchRes updateAdminByManager(Long managerUserId, String callerRole, Long userId, AdminPatchReq request) {
        validateActor(managerUserId, callerRole, UserRole.MANAGER);
        AdminProfile managerProfile = getAdminProfile(managerUserId);
        return updateAdmin(userId, request, false, managerProfile.getComplexId());
    }

    //추가 MASTER가 MANAGER 또는 ADMIN 계정을 비활성한다.
    @Transactional
    public AdminDeleteRes deleteAdminByMaster(Long masterUserId, String callerRole, Long userId) {
        validateActor(masterUserId, callerRole, UserRole.MASTER);
        return deleteAdmin(userId, true, null);
    }

    //추가 MANAGER가 같은 단지의 ADMIN 계정만 비활성한다.
    @Transactional
    public AdminDeleteRes deleteAdminByManager(Long managerUserId, String callerRole, Long userId) {
        validateActor(managerUserId, callerRole, UserRole.MANAGER);
        AdminProfile managerProfile = getAdminProfile(managerUserId);
        return deleteAdmin(userId, false, managerProfile.getComplexId());
    }

    //추가 단지 서비스 내부 연동용 관리자 생성 API를 처리한다.
    @Transactional
    public InternalAdminCreateRes createAdminInternal(InternalAdminCreateReq request) {
        validateInternalCreateRequest(request);
        UserRole targetRole = resolveRoleFromAdminRoleCode(request.getAdminRole());
        User savedUser = createAdminAccount(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getPhone(),
                request.getComplexId(),
                targetRole,
                //수정 내부 서비스 호출은 실제 생성자 컨텍스트가 없으므로 createdBy를 null로 둔다.
                null
        );

        return InternalAdminCreateRes.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .phone(savedUser.getPhone())
                .role(savedUser.getRole().name())
                .adminRole(toAdminRoleCode(targetRole))
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    //추가 단지 서비스 내부 연동용 관리자 수정 API를 처리한다.
    @Transactional
    public InternalAdminPatchRes updateAdminInternal(Long userId, InternalAdminPatchReq request) {
        User user = getManageableUser(userId);
        AdminProfile adminProfile = getAdminProfile(userId);

        //추가 내부 수정 요청도 요청값 검증을 유지한다.
        if (request.getAdminRole() != null && !request.getAdminRole().isBlank()) {
            UserRole targetRole = resolveRoleFromAdminRoleCode(request.getAdminRole());
            user.updateRole(targetRole);
            adminProfile.updateAdminRole(targetRole);
        }

        //수정 관리자 수정 시 user.phone도 함께 갱신한다.
        user.updateProfile(request.getName(), request.getPhone());

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            applyInternalStatus(user, adminProfile, request.getStatus());
        }

        authOutboxService.saveUpdatedEvent(user, adminProfile.getComplexId());

        return InternalAdminPatchRes.builder()
                .userId(user.getId())
                .adminRole(toAdminRoleCode(adminProfile.getAdminRole()))
                .status(adminProfile.getStatus().getCode())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    //추가 단지 서비스 내부 연동용 관리자 비활성 API를 처리한다.
    @Transactional
    public InternalAdminDeleteRes deleteAdminInternal(Long userId) {
        User user = getManageableUser(userId);
        AdminProfile adminProfile = getAdminProfile(userId);

        //수정 user hard delete는 금지하고 soft delete만 수행한다.
        user.softDelete();
        adminProfile.updateStatus(AdminProfileStatus.INACTIVE);
        authOutboxService.saveDeletedEvent(user, adminProfile.getComplexId());

        return InternalAdminDeleteRes.builder()
                .userId(user.getId())
                .status(user.getStatus().name())
                .isDeleted(user.getIsDeleted())
                .deletedAt(user.getDeletedAt())
                .build();
    }

    //추가 관리자 계정을 생성하고 outbox까지 적재하는 공통 메서드
    private User createAdminAccount(
            String email,
            String password,
            String name,
            String phone,
            Long complexId,
            UserRole targetRole,
            Long createdBy
    ) {
        validateCreateRequest(email, password, name, complexId, targetRole);

        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(AuthErrorCode.DUPLICATE_EMAIL);
        }

        String passwordHash = passwordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .passwordHash(passwordHash)
                .name(name)
                .phone(phone)
                .role(targetRole)
                .status(UserStatus.ACTIVE)
                .signupType(SignupType.CREATED_BY_ADMIN)
                .isPhoneVerified(false)
                .isEmailVerified(false)
                .loginFailCount(0)
                .isDeleted(false)
                .build();
        User savedUser = userRepository.save(user);

        AdminProfile adminProfile = AdminProfile.builder()
                .userId(savedUser.getId())
                .complexId(complexId)
                .adminRole(targetRole)
                .status(AdminProfileStatus.ACTIVE)
                .createdBy(createdBy)
                .build();
        adminProfileRepository.save(adminProfile);

        //수정 관리자 계정 생성 후 user cache 동기화 이벤트를 적재한다.
        authOutboxService.saveCreatedEvent(savedUser, complexId);
        return savedUser;
    }

    //추가 권한별 관리자 수정 공통 로직
    private AdminPatchRes updateAdmin(Long userId, AdminPatchReq request, boolean masterMode, Long managerComplexId) {
        User user = getManageableUser(userId);
        AdminProfile adminProfile = getAdminProfile(userId);
        validatePatchRequest(request);

        //수정 관리자 수정 권한을 service에서 다시 검증한다.
        if (!masterMode) {
            validateManagerTarget(user, adminProfile, managerComplexId);
        }

        if (masterMode && request.getTargetRole() != null && !request.getTargetRole().isBlank()) {
            UserRole targetRole = resolveRoleName(request.getTargetRole());
            if (targetRole != UserRole.MANAGER && targetRole != UserRole.ADMIN) {
                throw new BusinessException(AuthErrorCode.INVALID_ADMIN_ROLE);
            }
            user.updateRole(targetRole);
            adminProfile.updateAdminRole(targetRole);
        }

        if (!masterMode && request.getTargetRole() != null && !request.getTargetRole().isBlank()) {
            UserRole targetRole = resolveRoleName(request.getTargetRole());
            if (targetRole != UserRole.ADMIN) {
                throw new BusinessException(AuthErrorCode.FORBIDDEN);
            }
            user.updateRole(UserRole.ADMIN);
            adminProfile.updateAdminRole(UserRole.ADMIN);
        }

        //수정 관리자 수정 시 user.phone도 함께 갱신한다.
        user.updateProfile(request.getName(), request.getPhone());
        applyPublicStatus(user, adminProfile, request.getStatus());

        authOutboxService.saveUpdatedEvent(user, adminProfile.getComplexId());

        return AdminPatchRes.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .complexId(adminProfile.getComplexId())
                .status(user.getStatus().name())
                .build();
    }

    //추가 권한별 관리자 비활성 공통 로직
    private AdminDeleteRes deleteAdmin(Long userId, boolean masterMode, Long managerComplexId) {
        User user = getManageableUser(userId);
        AdminProfile adminProfile = getAdminProfile(userId);

        if (!masterMode) {
            validateManagerTarget(user, adminProfile, managerComplexId);
        }

        //추가 소속 해제 의미를 반영해 admin_profile은 INACTIVE로 유지한다.
        user.softDelete();
        adminProfile.updateStatus(AdminProfileStatus.INACTIVE);

        authOutboxService.saveDeletedEvent(user, adminProfile.getComplexId());

        return AdminDeleteRes.builder()
                .userId(user.getId())
                .status(user.getStatus().name())
                .deletedAt(user.getDeletedAt())
                .build();
    }

    //추가 호출자 userId와 role 헤더를 함께 검증한다.
    private User validateActor(Long userId, String callerRole, UserRole expectedRole) {
        if (!expectedRole.name().equals(callerRole)) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN);
        }

        User actor = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

        if (Boolean.TRUE.equals(actor.getIsDeleted()) || actor.getStatus() == UserStatus.DELETED || actor.getRole() != expectedRole) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN);
        }
        return actor;
    }

    //추가 MANAGER는 같은 단지의 ADMIN만 관리할 수 있다.
    private void validateManagerTarget(User targetUser, AdminProfile targetProfile, Long managerComplexId) {
        if (targetUser.getRole() != UserRole.ADMIN) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN);
        }
        if (!targetProfile.getComplexId().equals(managerComplexId)) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN);
        }
    }

    //추가 관리자 대상 user는 MANAGER 또는 ADMIN만 허용한다.
    private User getManageableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

        if (Boolean.TRUE.equals(user.getIsDeleted()) || user.getStatus() == UserStatus.DELETED) {
            throw new BusinessException(AuthErrorCode.USER_NOT_FOUND);
        }

        if (user.getRole() != UserRole.MANAGER && user.getRole() != UserRole.ADMIN) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN);
        }
        return user;
    }

    //추가 관리자 프로필은 반드시 존재해야 한다.
    private AdminProfile getAdminProfile(Long userId) {
        return adminProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.ADMIN_PROFILE_NOT_FOUND));
    }

    //추가 공개 관리자 생성 요청을 검증한다.
    private void validateCreateRequest(String email, String password, String name, Long complexId, UserRole targetRole) {
        if (email == null || email.isBlank() || password == null || password.isBlank() || name == null || name.isBlank() || complexId == null) {
            throw new BusinessException(AuthErrorCode.INVALID_PARAMETER);
        }
        if (targetRole != UserRole.MANAGER && targetRole != UserRole.ADMIN) {
            throw new BusinessException(AuthErrorCode.INVALID_ADMIN_ROLE);
        }
    }

    //추가 내부 관리자 생성 요청도 필수값을 검증한다.
    private void validateInternalCreateRequest(InternalAdminCreateReq request) {
        if (request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()
                || request.getName() == null || request.getName().isBlank()
                || request.getPhone() == null || request.getPhone().isBlank()
                || request.getComplexId() == null) {
            throw new BusinessException(AuthErrorCode.INVALID_PARAMETER);
        }

        // 내부 관리자 생성도 공개 회원가입과 같은 비밀번호 정책을 따른다.
        if (!request.getPassword().matches(PASSWORD_POLICY_REGEX)) {
            throw new BusinessException(AuthErrorCode.PASSWORD_POLICY_INVALID);
        }

        resolveRoleFromAdminRoleCode(request.getAdminRole());
    }

    //추가 관리자 수정 요청의 핵심 값만 검증한다.
    private void validatePatchRequest(AdminPatchReq request) {
        if (request == null) {
            throw new BusinessException(AuthErrorCode.INVALID_PARAMETER);
        }
        if (request.getTargetRole() == null && request.getStatus() == null && request.getName() == null && request.getPhone() == null) {
            throw new BusinessException(AuthErrorCode.INVALID_PARAMETER);
        }
    }

    //추가 공개 API 상태 문자열을 user/admin_profile 상태로 함께 반영한다.
    private void applyPublicStatus(User user, AdminProfile adminProfile, String status) {
        if (status == null || status.isBlank()) {
            return;
        }
        if ("ACTIVE".equals(status)) {
            user.updateUserStatus(UserStatus.ACTIVE);
            adminProfile.updateStatus(AdminProfileStatus.ACTIVE);
            return;
        }
        if ("INACTIVE".equals(status)) {
            user.updateUserStatus(UserStatus.ACTIVE);
            adminProfile.updateStatus(AdminProfileStatus.INACTIVE);
            return;
        }
        throw new BusinessException(AuthErrorCode.INVALID_PARAMETER);
    }

    //추가 내부 API 상태 code를 user/admin_profile 상태로 함께 반영한다.
    private void applyInternalStatus(User user, AdminProfile adminProfile, String statusCode) {
        if ("01".equals(statusCode)) {
            user.updateUserStatus(UserStatus.ACTIVE);
            adminProfile.updateStatus(AdminProfileStatus.ACTIVE);
            return;
        }
        if ("02".equals(statusCode)) {
            user.updateUserStatus(UserStatus.ACTIVE);
            adminProfile.updateStatus(AdminProfileStatus.INACTIVE);
            return;
        }
        if ("03".equals(statusCode)) {
            user.softDelete();
            adminProfile.updateStatus(AdminProfileStatus.INACTIVE);
            return;
        }
        throw new BusinessException(AuthErrorCode.INVALID_PARAMETER);
    }

    //추가 단지 서비스 adminRole code를 Auth UserRole로 변환한다.
    private UserRole resolveRoleFromAdminRoleCode(String adminRoleCode) {
        if ("01".equals(adminRoleCode)) {
            return UserRole.MANAGER;
        }
        if ("02".equals(adminRoleCode)) {
            return UserRole.ADMIN;
        }
        throw new BusinessException(AuthErrorCode.INVALID_ADMIN_ROLE);
    }

    //추가 공개 수정 요청의 targetRole 문자열을 UserRole로 변환한다.
    private UserRole resolveRoleName(String roleName) {
        //수정 null 또는 blank 권한값은 바로 잘못된 요청으로 처리한다.
        if (roleName == null || roleName.isBlank()) {
            throw new BusinessException(AuthErrorCode.INVALID_ADMIN_ROLE);
        }
        try {
            return UserRole.valueOf(roleName);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(AuthErrorCode.INVALID_ADMIN_ROLE);
        }
    }

    //추가 Auth UserRole을 단지 서비스 adminRole code로 변환한다.
    private String toAdminRoleCode(UserRole role) {
        //수정 MANAGER 또는 ADMIN 외 값은 허용하지 않는다.
        if (role == UserRole.MANAGER) {
            return "01";
        }
        if (role == UserRole.ADMIN) {
            return "02";
        }
        throw new BusinessException(AuthErrorCode.INVALID_ADMIN_ROLE);
    }

    //추가 관리자 생성 응답을 공통 DTO로 변환한다.
    private AdminCreateRes toAdminCreateResponse(User savedUser, Long complexId) {
        return AdminCreateRes.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .phone(savedUser.getPhone())
                .role(savedUser.getRole().name())
                .complexId(complexId)
                .build();
    }
}
