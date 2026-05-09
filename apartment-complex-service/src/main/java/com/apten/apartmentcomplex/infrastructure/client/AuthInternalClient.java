package com.apten.apartmentcomplex.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminCreateReq;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminCreateRes;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminDeleteRes;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminUpdateReq;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminUpdateRes;
import com.apten.apartmentcomplex.infrastructure.config.AuthServiceProperties;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import java.net.http.HttpClient;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

// Auth Service 내부 관리자 생성 API를 호출하는 클라이언트이다.
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthInternalClient {

    private final RestClient.Builder restClientBuilder;
    private final AuthServiceProperties authServiceProperties;
    private final ObjectMapper objectMapper;

    private RestClient restClient() {
        // 내부 인증 연동은 PATCH 요청을 사용하므로 JDK HttpClient 기반 request factory를 명시한다.
        return restClientBuilder
                .requestFactory(new JdkClientHttpRequestFactory(HttpClient.newHttpClient()))
                .build();
    }

    public InternalAdminCreateRes createAdmin(InternalAdminCreateReq req) {
        String url = authServiceProperties.getUrl() + "/internal/auth/admins";
        return executeAuthRequest(
                "내부 관리자 생성",
                url,
                () -> restClient()
                        .post()
                        .uri(url)
                        .body(req)
                        .retrieve()
                        .body(String.class),
                InternalAdminCreateRes.class
        );
    }

    public InternalAdminDeleteRes softDeleteAdmin(Long userId) {
        String url = authServiceProperties.getUrl() + "/internal/auth/admins/" + userId + "/delete";
        return executeAuthRequest(
                "내부 관리자 삭제",
                url,
                () -> restClient()
                        .patch()
                        .uri(url)
                        .retrieve()
                        .body(String.class),
                InternalAdminDeleteRes.class
        );
    }

    public InternalAdminUpdateRes updateAdmin(Long userId, InternalAdminUpdateReq req) {
        String url = authServiceProperties.getUrl() + "/internal/auth/admins/" + userId;
        return executeAuthRequest(
                "내부 관리자 수정",
                url,
                () -> restClient()
                        .patch()
                        .uri(url)
                        .body(req)
                        .retrieve()
                        .body(String.class),
                InternalAdminUpdateRes.class
        );
    }

    private <T> T unwrapResponse(String body, Class<T> responseType) throws Exception {
        JsonNode root = objectMapper.readTree(body);
        JsonNode successNode = root.get("success");

        if (successNode != null) {
            if (!successNode.asBoolean()) {
                throw mapAuthException(body);
            }

            JsonNode dataNode = root.get("data");
            if (dataNode == null || dataNode.isNull()) {
                dataNode = root.get("resultData");
            }

            if (dataNode == null || dataNode.isNull() || dataNode.isMissingNode()) {
                throw new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
            }

            return objectMapper.treeToValue(dataNode, responseType);
        }

        return objectMapper.treeToValue(root, responseType);
    }

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
            // 응답 본문 파싱에 실패하면 공통 내부 호출 오류로 처리한다.
        }

        return new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
    }

    private <T> T executeAuthRequest(
            String operationName,
            String url,
            Supplier<String> responseSupplier,
            Class<T> responseType
    ) {
        try {
            String body = responseSupplier.get();
            return unwrapResponse(body, responseType);
        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientResponseException exception) {
            log.error(
                    "{} 실패. statusCode={}, responseBody={}, url={}",
                    operationName,
                    exception.getStatusCode(),
                    exception.getResponseBodyAsString(),
                    url,
                    exception
            );
            throw mapAuthException(exception.getResponseBodyAsString());
        } catch (RestClientException exception) {
            log.error("{} 실패. url={}", operationName, url, exception);
            throw new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
        } catch (Exception exception) {
            log.error("{} 실패. url={}", operationName, url, exception);
            throw new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
        }
    }
}
