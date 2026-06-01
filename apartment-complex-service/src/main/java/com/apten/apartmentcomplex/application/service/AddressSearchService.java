package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.application.model.response.AddressSearchPageRes;
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

// 행안부 주소 검색
@Service
@Slf4j
@RequiredArgsConstructor
public class AddressSearchService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 6;
    private static final int MAX_SIZE = 20;

    private final JusoAddressClient jusoAddressClient;

    // 주소 검색
    public AddressSearchPageRes searchAddress(String keyword, Integer page, Integer size) {
        // 검색 조건 검증
        String trimmedKeyword = keyword == null ? null : keyword.trim();
        if (trimmedKeyword == null || trimmedKeyword.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        int normalizedPage = page == null ? DEFAULT_PAGE : page;
        int normalizedSize = size == null ? DEFAULT_SIZE : size;
        if (normalizedPage < 0 || normalizedSize <= 0 || normalizedSize > MAX_SIZE) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        JusoAddressApiResponse response;
        try {
            // 행안부 도로명주소 API 호출
            response = jusoAddressClient.searchAddress(trimmedKeyword, normalizedPage + 1, normalizedSize);
        } catch (BusinessException exception) {
            log.error("주소 검색 외부 API 호출 실패 — keyword={}", trimmedKeyword, exception);
            throw exception;
        } catch (Exception exception) {
            // 외부 API 예외 변환
            log.error("주소 검색 외부 API 호출 실패 — keyword={}", trimmedKeyword, exception);
            throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
        }

        if (response == null
                || response.getResults() == null
                || response.getResults().getCommon() == null) {
            // 외부 API 응답 구조 검증
            log.warn(
                    "주소 검색 외부 API 응답 구조가 비정상입니다. responseNull={}, resultsNull={}, commonNull={}",
                    response == null,
                    response != null && response.getResults() == null,
                    response != null && response.getResults() != null && response.getResults().getCommon() == null
            );
            throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
        }

        if (!"0".equals(response.getResults().getCommon().getErrorCode())) {
            // 외부 API 오류 코드 검증
            log.warn(
                    "주소 검색 외부 API가 오류를 반환했습니다. errorCode={}, errorMessage={}",
                    response.getResults().getCommon().getErrorCode(),
                    response.getResults().getCommon().getErrorMessage()
            );
            throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
        }

        List<JusoAddressApiResponse.Juso> jusoList = response.getResults().getJuso();
        long totalElements = parseTotalCount(response.getResults().getCommon().getTotalCount());
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / normalizedSize);
        boolean hasNext = normalizedPage + 1 < totalPages;

        if (jusoList == null || jusoList.isEmpty()) {
            // 빈 검색 결과 반환
            if (jusoList == null) {
                log.warn("주소 검색 외부 API 응답에서 juso 목록이 null입니다.");
            }
            return AddressSearchPageRes.builder()
                    .content(Collections.emptyList())
                    .page(normalizedPage)
                    .size(normalizedSize)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .hasNext(hasNext)
                    .build();
        }

        // 주소 검색 응답 변환
        List<AddressSearchRes> content = jusoList.stream()
                .map(this::toResponse)
                .toList();

        return AddressSearchPageRes.builder()
                .content(content)
                .page(normalizedPage)
                .size(normalizedSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(hasNext)
                .build();
    }

    // 주소 검색 항목 변환
    private AddressSearchRes toResponse(JusoAddressApiResponse.Juso juso) {
        String address = hasText(juso.getRoadAddr()) ? juso.getRoadAddr() : defaultString(juso.getJibunAddr());
        String apartmentName = hasText(juso.getBdNm()) ? juso.getBdNm() : address;

        return AddressSearchRes.builder()
                .apartmentName(apartmentName)
                .address(address)
                .zipCode(defaultString(juso.getZipNo()))
                .build();
    }

    // 문자열 존재 여부 확인
    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    // null 문자열 기본값 변환
    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    // 주소 검색 결과 수 변환
    private long parseTotalCount(String totalCount) {
        if (totalCount == null || totalCount.isBlank()) {
            return 0L;
        }

        try {
            return Long.parseLong(totalCount);
        } catch (NumberFormatException exception) {
            log.warn("주소 검색 외부 API totalCount가 숫자가 아닙니다. totalCount={}", totalCount, exception);
            throw new BusinessException(ApartmentComplexErrorCode.EXTERNAL_ADDRESS_API_ERROR);
        }
    }
}
