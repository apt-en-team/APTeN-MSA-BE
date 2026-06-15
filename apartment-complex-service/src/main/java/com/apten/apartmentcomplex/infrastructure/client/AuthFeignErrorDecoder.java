package com.apten.apartmentcomplex.infrastructure.client;

import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Auth 서비스 내부 API 오류 응답 → BusinessException 변환 디코더
@Slf4j
public class AuthFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // HTTP 오류 응답 본문 파싱 후 BusinessException으로 변환
    @Override
    public Exception decode(String methodKey, Response response) {
        String body = "";
        try {
            if (response.body() != null) {
                body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("Auth 내부 API 응답 본문 파싱 실패. methodKey={}", methodKey, e);
            return new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
        }
        log.error("Auth 내부 API 오류. methodKey={}, status={}, body={}", methodKey, response.status(), body);
        return mapAuthException(body);
    }

    // 응답 code/message 기반 ApartmentComplex 도메인 예외 매핑
    private BusinessException mapAuthException(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            String code = root.hasNonNull("code") ? root.get("code").asText() : "";
            String message = root.hasNonNull("message") ? root.get("message").asText() : "";

            if ("AUTH_409_01".equals(code) || "DUPLICATE_EMAIL".equals(code)) {
                return new BusinessException(ApartmentComplexErrorCode.DUPLICATE_EMAIL);
            }
            if ("COMMON_400".equals(code) || "INVALID_PARAMETER".equals(code)) {
                return new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            if ("AUTH_400_04".equals(code) || "PASSWORD_POLICY_INVALID".equals(code)) {
                return new BusinessException(ApartmentComplexErrorCode.INVALID_ADMIN_PASSWORD);
            }
            if ("AUTH_404_01".equals(code) || "USER_NOT_FOUND".equals(code)) {
                return new BusinessException(ApartmentComplexErrorCode.USER_NOT_FOUND);
            }
            if ("ADMIN_PROFILE_NOT_FOUND".equals(code)) {
                return new BusinessException(ApartmentComplexErrorCode.ADMIN_PROFILE_NOT_FOUND);
            }

            if (message.contains("이미 사용중인 이메일")) {
                return new BusinessException(ApartmentComplexErrorCode.DUPLICATE_EMAIL);
            }
            if (message.contains("잘못된 요청 파라미터")) {
                return new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            if (message.contains("비밀번호는 8자 이상")) {
                return new BusinessException(ApartmentComplexErrorCode.INVALID_ADMIN_PASSWORD);
            }
            if (message.contains("사용자를 찾을 수 없습니다")) {
                return new BusinessException(ApartmentComplexErrorCode.USER_NOT_FOUND);
            }
            if (message.contains("관리자 프로필")) {
                return new BusinessException(ApartmentComplexErrorCode.ADMIN_PROFILE_NOT_FOUND);
            }
        } catch (Exception ignored) {
            // 응답 본문 파싱 실패 시 공통 내부 호출 오류로 처리
        }
        return new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
    }
}
