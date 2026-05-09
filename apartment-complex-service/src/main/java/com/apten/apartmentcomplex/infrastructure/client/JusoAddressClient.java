package com.apten.apartmentcomplex.infrastructure.client;

import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.apartmentcomplex.infrastructure.client.model.JusoAddressApiResponse;
import com.apten.apartmentcomplex.infrastructure.config.JusoProperties;
import com.apten.common.exception.BusinessException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

// 행안부 주소 검색 API를 호출하는 외부 클라이언트이다.
@Component
@Slf4j
@RequiredArgsConstructor
public class JusoAddressClient {

    private final RestClient.Builder restClientBuilder;
    private final JusoProperties jusoProperties;

    public JusoAddressApiResponse searchAddress(String keyword, int currentPage, int countPerPage) {
        URI requestUri = null;

        try {
            if (jusoProperties.getSearchUrl() == null || jusoProperties.getSearchUrl().isBlank()) {
                log.error(
                        "행안부 주소 검색 API URL 설정이 비어 있습니다. searchUrl={}, apiKeyMasked={}",
                        jusoProperties.getSearchUrl(),
                        maskApiKey(jusoProperties.getApiKey())
                );
                throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
            }

            if (jusoProperties.getApiKey() == null || jusoProperties.getApiKey().isBlank()) {
                log.error(
                        "행안부 주소 검색 API 키 설정이 비어 있습니다. searchUrl={}, apiKeyMasked={}",
                        jusoProperties.getSearchUrl(),
                        maskApiKey(jusoProperties.getApiKey())
                );
                throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
            }

            requestUri = UriComponentsBuilder.fromUriString(jusoProperties.getSearchUrl())
                    .queryParam("confmKey", jusoProperties.getApiKey())
                    .queryParam("currentPage", currentPage)
                    .queryParam("countPerPage", countPerPage)
                    .queryParam("keyword", keyword)
                    .queryParam("resultType", "json")
                    .encode(StandardCharsets.UTF_8)
                    .build()
                    .toUri();
            URI finalRequestUri = requestUri;

            log.info(
                    "행안부 주소 검색 API 호출 준비. searchUrl={}, requestUri={}, keyword={}, currentPage={}, countPerPage={}, apiKeyMasked={}",
                    jusoProperties.getSearchUrl(),
                    finalRequestUri,
                    keyword,
                    currentPage,
                    countPerPage,
                    maskApiKey(jusoProperties.getApiKey())
            );

            return restClientBuilder.build()
                    .get()
                    .uri(finalRequestUri)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);

                        log.error(
                                "행안부 주소 검색 API HTTP 오류. statusCode={}, responseBody={}, searchUrl={}, requestUri={}, keyword={}, apiKeyMasked={}",
                                response.getStatusCode(),
                                responseBody,
                                jusoProperties.getSearchUrl(),
                                finalRequestUri,
                                keyword,
                                maskApiKey(jusoProperties.getApiKey())
                        );
                        throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
                    })
                    .body(JusoAddressApiResponse.class);
        } catch (BusinessException exception) {
            log.error(
                    "행안부 주소 검색 API 호출 중 BusinessException이 발생했습니다. searchUrl={}, requestUri={}, keyword={}, currentPage={}, countPerPage={}, apiKeyMasked={}",
                    jusoProperties.getSearchUrl(),
                    requestUri,
                    keyword,
                    currentPage,
                    countPerPage,
                    maskApiKey(jusoProperties.getApiKey()),
                    exception
            );
            throw exception;
        } catch (RestClientResponseException exception) {
            log.error(
                    "행안부 주소 검색 API HTTP 오류. statusCode={}, responseBody={}, searchUrl={}, requestUri={}, keyword={}, currentPage={}, countPerPage={}, apiKeyMasked={}",
                    exception.getStatusCode(),
                    exception.getResponseBodyAsString(),
                    jusoProperties.getSearchUrl(),
                    requestUri,
                    keyword,
                    currentPage,
                    countPerPage,
                    maskApiKey(jusoProperties.getApiKey()),
                    exception
            );
            throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
        } catch (RestClientException exception) {
            log.error(
                    "행안부 주소 검색 API 호출 중 클라이언트 오류가 발생했습니다. searchUrl={}, requestUri={}, keyword={}, currentPage={}, countPerPage={}, apiKeyMasked={}",
                    jusoProperties.getSearchUrl(),
                    requestUri,
                    keyword,
                    currentPage,
                    countPerPage,
                    maskApiKey(jusoProperties.getApiKey()),
                    exception
            );
            throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
        } catch (Exception exception) {
            log.error(
                    "행안부 주소 검색 API 호출 중 예기치 못한 오류가 발생했습니다. searchUrl={}, requestUri={}, keyword={}, currentPage={}, countPerPage={}, apiKeyMasked={}",
                    jusoProperties.getSearchUrl(),
                    requestUri,
                    keyword,
                    currentPage,
                    countPerPage,
                    maskApiKey(jusoProperties.getApiKey()),
                    exception
            );
            throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
        }
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return "(blank)";
        }

        if (apiKey.length() <= 8) {
            return apiKey;
        }

        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }
}
