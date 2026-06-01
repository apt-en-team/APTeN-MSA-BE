package com.apten.facilityreservation.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.common.security.UserRole;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import org.springframework.stereotype.Component;

// 시설예약 요청 컨텍스트 해석
@Component
public class FacilityRequestContextResolver {

    // 관리자 단지 컨텍스트 해석
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

    // 입주민 단지 컨텍스트 해석
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

    // 사용자 역할 변환
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

    // 사용자 ID 검증
    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }
}
