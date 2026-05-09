package com.apten.household.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.common.security.UserRole;
import com.apten.household.application.model.dto.HouseholdRequestContext;
import org.springframework.stereotype.Component;

// household-service 외부 API가 Gateway 헤더 기준으로 현재 사용자와 단지를 해석하는 도우미이다.
@Component
public class HouseholdRequestContextResolver {

    // 관리자 API의 단지 컨텍스트를 해석한다.
    public HouseholdRequestContext resolveAdminContext(
            Long userId,
            String userRole,
            Long complexId,
            Long selectedComplexId
    ) {
        UserRole resolvedRole = resolveRole(userRole);

        if (resolvedRole == UserRole.MASTER) {
            if (selectedComplexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return HouseholdRequestContext.builder()
                    .userId(userId)
                    .userRole(resolvedRole)
                    .complexId(selectedComplexId)
                    .build();
        }

        if (resolvedRole != UserRole.ADMIN && resolvedRole != UserRole.MANAGER) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }

        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        return HouseholdRequestContext.builder()
                .userId(userId)
                .userRole(resolvedRole)
                .complexId(complexId)
                .build();
    }

    // 입주민 API의 단지 컨텍스트를 해석한다.
    public HouseholdRequestContext resolveResidentContext(Long userId, String userRole, Long complexId) {
        UserRole resolvedRole = resolveRole(userRole);

        if (resolvedRole != UserRole.USER) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }

        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        return HouseholdRequestContext.builder()
                .userId(userId)
                .userRole(resolvedRole)
                .complexId(complexId)
                .build();
    }

    // role 문자열을 공통 enum으로 변환한다.
    private UserRole resolveRole(String userRole) {
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        try {
            return UserRole.valueOf(userRole);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
    }
}
