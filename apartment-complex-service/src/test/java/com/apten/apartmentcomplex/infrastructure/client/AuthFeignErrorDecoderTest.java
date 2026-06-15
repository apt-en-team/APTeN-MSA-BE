package com.apten.apartmentcomplex.infrastructure.client;

import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class AuthFeignErrorDecoderTest {

    private AuthFeignErrorDecoder decoder;

    @BeforeEach
    void setUp() {
        decoder = new AuthFeignErrorDecoder();
    }

    private Response buildResponse(int status, String body) {
        return Response.builder()
                .status(status)
                .reason("test")
                .request(Request.create(
                        Request.HttpMethod.POST,
                        "http://localhost/test",
                        Collections.emptyMap(),
                        null,
                        StandardCharsets.UTF_8,
                        null
                ))
                .headers(Collections.emptyMap())
                .body(body, StandardCharsets.UTF_8)
                .build();
    }

    @Test
    @DisplayName("AUTH_409_01 코드 → DUPLICATE_EMAIL")
    void duplicateEmail_byCode() {
        Response response = buildResponse(409, "{\"success\":false,\"code\":\"AUTH_409_01\",\"message\":\"이미 사용중인 이메일\"}");
        Exception ex = decoder.decode("createAdmin", response);
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(ApartmentComplexErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("DUPLICATE_EMAIL 코드 → DUPLICATE_EMAIL")
    void duplicateEmail_byDuplicateEmailCode() {
        Response response = buildResponse(409, "{\"success\":false,\"code\":\"DUPLICATE_EMAIL\",\"message\":\"\"}");
        Exception ex = decoder.decode("createAdmin", response);
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(ApartmentComplexErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("AUTH_400_04 코드 → INVALID_ADMIN_PASSWORD")
    void invalidPassword_byCode() {
        Response response = buildResponse(400, "{\"success\":false,\"code\":\"AUTH_400_04\",\"message\":\"\"}");
        Exception ex = decoder.decode("createAdmin", response);
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(ApartmentComplexErrorCode.INVALID_ADMIN_PASSWORD);
    }

    @Test
    @DisplayName("AUTH_404_01 코드 → USER_NOT_FOUND")
    void userNotFound_byCode() {
        Response response = buildResponse(404, "{\"success\":false,\"code\":\"AUTH_404_01\",\"message\":\"\"}");
        Exception ex = decoder.decode("softDeleteAdmin", response);
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(ApartmentComplexErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("ADMIN_PROFILE_NOT_FOUND 코드 → ADMIN_PROFILE_NOT_FOUND")
    void adminProfileNotFound_byCode() {
        Response response = buildResponse(404, "{\"success\":false,\"code\":\"ADMIN_PROFILE_NOT_FOUND\",\"message\":\"\"}");
        Exception ex = decoder.decode("updateAdmin", response);
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(ApartmentComplexErrorCode.ADMIN_PROFILE_NOT_FOUND);
    }

    @Test
    @DisplayName("COMMON_400 코드 → INVALID_PARAMETER")
    void invalidParameter_byCode() {
        Response response = buildResponse(400, "{\"success\":false,\"code\":\"COMMON_400\",\"message\":\"\"}");
        Exception ex = decoder.decode("createAdmin", response);
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(CommonErrorCode.INVALID_PARAMETER);
    }

    @Test
    @DisplayName("알 수 없는 오류 → AUTH_INTERNAL_API_ERROR")
    void unknownError_fallback() {
        Response response = buildResponse(500, "{\"success\":false,\"code\":\"UNKNOWN\",\"message\":\"서버 오류\"}");
        Exception ex = decoder.decode("createAdmin", response);
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
    }

    @Test
    @DisplayName("빈 응답 본문 → AUTH_INTERNAL_API_ERROR")
    void emptyBody_fallback() {
        Response response = buildResponse(500, "");
        Exception ex = decoder.decode("createAdmin", response);
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
    }

    @Test
    @DisplayName("메시지 기반 이메일 중복 감지")
    void duplicateEmail_byMessage() {
        Response response = buildResponse(409, "{\"success\":false,\"code\":\"UNKNOWN\",\"message\":\"이미 사용중인 이메일입니다\"}");
        Exception ex = decoder.decode("createAdmin", response);
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(((BusinessException) ex).getErrorCode()).isEqualTo(ApartmentComplexErrorCode.DUPLICATE_EMAIL);
    }
}
