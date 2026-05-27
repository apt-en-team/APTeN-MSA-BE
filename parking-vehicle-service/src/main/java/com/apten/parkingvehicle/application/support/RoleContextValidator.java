package com.apten.parkingvehicle.application.support;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// gateway가 전달한 헤더로 입주민/관리자 컨텍스트를 검증하는 공용 유틸
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoleContextValidator {

    // 입주민 SSE 컨텍스트 검증, USER 권한과 단지 식별자 필수
    public static void validateResidentContext(String userRole, Long complexId) {
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        if (!"USER".equals(userRole)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 입주민 API 헤더 컨텍스트 검증, USER 권한과 식별자 필수
    public static void validateResidentContext(Long userId, String userRole, Long complexId) {
        validateResidentContext(userRole, complexId);
        if (userId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 관리자 주차 API의 단지 컨텍스트를 역할별 헤더 기준으로 해석한다.
    // MASTER는 선택 단지, 일반 관리자는 토큰 단지 기준으로 처리한다.
    public static Long resolveAdminContextComplexId(String userRole, Long complexId, Long selectedComplexId) {
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if ("MASTER".equals(userRole)) {
            if (selectedComplexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return selectedComplexId;
        }

        if ("MANAGER".equals(userRole) || "ADMIN".equals(userRole)) {
            if (complexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return complexId;
        }

        throw new BusinessException(CommonErrorCode.FORBIDDEN);
    }
}
