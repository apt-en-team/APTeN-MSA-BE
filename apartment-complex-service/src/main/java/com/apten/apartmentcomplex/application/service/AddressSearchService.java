package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.application.model.response.AddressSearchRes;
import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.apartmentcomplex.infrastructure.client.JusoAddressClient;
import com.apten.apartmentcomplex.infrastructure.client.model.JusoAddressApiResponse;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// 행안부 주소 검색 응답을 서비스 응답으로 변환하는 서비스이다.
@Service
@Slf4j
@RequiredArgsConstructor
public class AddressSearchService {

    private final JusoAddressClient jusoAddressClient;

    // 주소 검색 API-211
    public List<AddressSearchRes> searchAddress(String keyword) {
        // 검색어가 비어 있으면 요청 오류로 처리한다.
        String trimmedKeyword = keyword == null ? null : keyword.trim();
        if (trimmedKeyword == null || trimmedKeyword.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        JusoAddressApiResponse response;
        try {
            // 행안부 도로명주소 검색 API를 호출한다.
            response = jusoAddressClient.searchAddress(trimmedKeyword);
        } catch (BusinessException exception) {
            log.error("주소 검색 외부 API 호출 실패 — keyword={}", trimmedKeyword, exception);
            throw exception;
        } catch (Exception exception) {
            // 외부 API 호출 실패는 주소 검색 외부 API 오류로 변환한다.
            log.error("주소 검색 외부 API 호출 실패 — keyword={}", trimmedKeyword, exception);
            throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
        }

        if (response == null
                || response.getResults() == null
                || response.getResults().getCommon() == null) {
            // 외부 API 응답이 비정상이면 서비스 예외로 변환한다.
            log.warn(
                    "주소 검색 외부 API 응답 구조가 비정상입니다. responseNull={}, resultsNull={}, commonNull={}",
                    response == null,
                    response != null && response.getResults() == null,
                    response != null && response.getResults() != null && response.getResults().getCommon() == null
            );
            throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
        }

        if (!"0".equals(response.getResults().getCommon().getErrorCode())) {
            // 외부 API 응답이 비정상이면 서비스 예외로 변환한다.
            log.warn(
                    "주소 검색 외부 API가 오류를 반환했습니다. errorCode={}, errorMessage={}",
                    response.getResults().getCommon().getErrorCode(),
                    response.getResults().getCommon().getErrorMessage()
            );
            throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
        }

        List<JusoAddressApiResponse.Juso> jusoList = response.getResults().getJuso();
        if (jusoList == null || jusoList.isEmpty()) {
            // 검색 결과가 없으면 빈 목록을 반환한다.
            if (jusoList == null) {
                log.warn("주소 검색 외부 API 응답에서 juso 목록이 null입니다.");
            }
            return Collections.emptyList();
        }

        // 외부 API 응답을 프론트에서 사용하는 주소 검색 응답으로 변환한다.
        return jusoList.stream()
                .map(this::toResponse)
                .toList();
    }

    private AddressSearchRes toResponse(JusoAddressApiResponse.Juso juso) {
        String address = hasText(juso.getRoadAddr()) ? juso.getRoadAddr() : defaultString(juso.getJibunAddr());
        String apartmentName = hasText(juso.getBdNm()) ? juso.getBdNm() : address;

        return AddressSearchRes.builder()
                .apartmentName(apartmentName)
                .address(address)
                .addressDetail("")
                .zipCode(defaultString(juso.getZipNo()))
                .build();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
