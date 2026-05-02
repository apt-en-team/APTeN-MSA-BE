package com.apten.apartmentcomplex.infrastructure.client;

import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.apartmentcomplex.infrastructure.client.model.JusoAddressApiResponse;
import com.apten.apartmentcomplex.infrastructure.config.JusoProperties;
import com.apten.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpStatusCode;

// 행안부 주소 검색 API를 호출하는 외부 클라이언트이다.
@Component
@RequiredArgsConstructor
public class JusoAddressClient {

    private final RestClient.Builder restClientBuilder;
    private final JusoProperties jusoProperties;

    public JusoAddressApiResponse searchAddress(String keyword) {
        try {
            return restClientBuilder.build()
                    .get()
                    .uri(UriComponentsBuilder.fromUriString(jusoProperties.getSearchUrl())
                            .queryParam("confmKey", jusoProperties.getApiKey())
                            .queryParam("currentPage", 1)
                            .queryParam("countPerPage", 10)
                            .queryParam("keyword", keyword)
                            .queryParam("resultType", "json")
                            .build(true)
                            .toUri())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
                    })
                    .body(JusoAddressApiResponse.class);
        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
        }
    }
}
