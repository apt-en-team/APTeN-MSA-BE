package com.apten.facilityreservation.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.common.security.UserRole;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import org.springframework.stereotype.Component;

// gateway 헤더를 시설예약 서비스의 사용자/단지 컨텍스트로 해석하는 resolver이다.
@Component
public class FacilityRequestContextResolver {

    // 관리자 API의 단지 컨텍스트를 역할별 헤더 기준으로 해석한다.
    public FacilityRequestContext resolveAdminContext(
            Long userId,
            String userRoleHeader,
            Long complexIdHeader,
            Long selectedComplexIdHeader
    ) {
        UserRole userRole = parseUserRole(userRoleHeader);
        validateUserId(userId);

        if (userRole == UserRole.MASTER) {
            if (selectedComplexIdHeader == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return FacilityRequestContext.builder()
                    .userId(userId)
                    .userRole(userRole)
                    .complexId(selectedComplexIdHeader)
                    .build();
        }

        if (userRole == UserRole.MANAGER || userRole == UserRole.ADMIN) {
            if (complexIdHeader == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return FacilityRequestContext.builder()
                    .userId(userId)
                    .userRole(userRole)
                    .complexId(complexIdHeader)
                    .build();
        }

        throw new BusinessException(CommonErrorCode.FORBIDDEN);
    }

    // 입주민 API의 단지 컨텍스트를 토큰 단지 헤더 기준으로 해석한다.
    public FacilityRequestContext resolveResidentContext(Long userId, String userRoleHeader, Long complexIdHeader) {
        UserRole userRole = parseUserRole(userRoleHeader);
        validateUserId(userId);

        if (userRole != UserRole.USER) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }

        if (complexIdHeader == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        return FacilityRequestContext.builder()
                .userId(userId)
                .userRole(userRole)
                .complexId(complexIdHeader)
                .build();
    }

    // 헤더의 사용자 역할을 공통 enum으로 변환한다.
    private UserRole parseUserRole(String userRoleHeader) {
        if (userRoleHeader == null || userRoleHeader.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        try {
            return UserRole.valueOf(userRoleHeader);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 헤더의 사용자 ID를 기본 검증한다.
    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }
}
