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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

// Auth Service 내부 관리자 생성 API를 호출하는 클라이언트이다.
@Component
@RequiredArgsConstructor
public class AuthInternalClient {

    private final RestClient.Builder restClientBuilder;
    private final AuthServiceProperties authServiceProperties;
    private final ObjectMapper objectMapper;

    public InternalAdminCreateRes createAdmin(InternalAdminCreateReq req) {
        try {
            String body = restClientBuilder.build()
                    .post()
                    .uri(authServiceProperties.getUrl() + "/internal/auth/admins")
                    .body(req)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
                    })
                    .body(String.class);

            return unwrapResponse(body, InternalAdminCreateRes.class);
        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientResponseException exception) {
            throw mapAuthException(exception.getResponseBodyAsString());
        } catch (RestClientException exception) {
            // 내부 호출 실패 시 예외를 단지 서비스 예외로 변환한다.
            throw new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
        } catch (Exception exception) {
            // 내부 호출 실패 시 예외를 단지 서비스 예외로 변환한다.
            throw new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
        }
    }

    public InternalAdminDeleteRes softDeleteAdmin(Long userId) {
        try {
            String body = restClientBuilder.build()
                    .patch()
                    .uri(authServiceProperties.getUrl() + "/internal/auth/admins/" + userId + "/delete")
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
                    })
                    .body(String.class);

            return unwrapResponse(body, InternalAdminDeleteRes.class);
        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientResponseException exception) {
            throw mapAuthException(exception.getResponseBodyAsString());
        } catch (RestClientException exception) {
            // 내부 호출 실패 시 예외를 단지 서비스 예외로 변환한다.
            throw new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
        } catch (Exception exception) {
            // 내부 호출 실패 시 예외를 단지 서비스 예외로 변환한다.
            throw new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
        }
    }

    public InternalAdminUpdateRes updateAdmin(Long userId, InternalAdminUpdateReq req) {
        try {
            String body = restClientBuilder.build()
                    .patch()
                    .uri(authServiceProperties.getUrl() + "/internal/auth/admins/" + userId)
                    .body(req)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
                    })
                    .body(String.class);

            return unwrapResponse(body, InternalAdminUpdateRes.class);
        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientResponseException exception) {
            throw mapAuthException(exception.getResponseBodyAsString());
        } catch (RestClientException exception) {
            // 내부 호출 실패 시 예외를 단지 서비스 예외로 변환한다.
            throw new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
        } catch (Exception exception) {
            // 내부 호출 실패 시 예외를 단지 서비스 예외로 변환한다.
            throw new BusinessException(ApartmentComplexErrorCode.AUTH_INTERNAL_API_ERROR);
        }
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

            if ("AUTH_404_01".equals(code) || "USER_NOT_FOUND".equals(code)) {
                return new BusinessException(ApartmentComplexErrorCode.USER_NOT_FOUND);
            }

            if ("ADMIN_PROFILE_NOT_FOUND".equals(code)) {
                return new BusinessException(ApartmentComplexErrorCode.ADMIN_PROFILE_NOT_FOUND);
            }

            if (message.contains("이미 사용중인 이메일")) {
                return new BusinessException(ApartmentComplexErrorCode.DUPLICATE_EMAIL);
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
}
