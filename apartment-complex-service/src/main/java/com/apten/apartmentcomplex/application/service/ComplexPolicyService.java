package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.application.model.request.ComplexPolicyPutReq;
import com.apten.apartmentcomplex.application.model.request.FacilityPolicyPutReq;
import com.apten.apartmentcomplex.application.model.request.VehiclePolicyPutReq;
import com.apten.apartmentcomplex.application.model.request.VisitorPolicyPutReq;
import com.apten.apartmentcomplex.application.model.response.ComplexPolicyPutRes;
import com.apten.apartmentcomplex.application.model.response.FacilityPolicyPutRes;
import com.apten.apartmentcomplex.application.model.response.VehiclePolicyPutRes;
import com.apten.apartmentcomplex.application.model.response.VisitorPolicyPutRes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import com.apten.apartmentcomplex.domain.entity.ComplexPolicy;
import com.apten.apartmentcomplex.domain.repository.ApartmentComplexRepository;
import com.apten.apartmentcomplex.domain.repository.ComplexPolicyRepository;
import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.apartmentcomplex.infrastructure.kafka.ApartmentComplexOutboxService;
import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// 단지 정책 응용 서비스
// 기본 정책과 차량, 시설, 방문차량 정책 시그니처를 이 서비스에 모아둔다
@Service
@RequiredArgsConstructor
public class ComplexPolicyService {

    private final ApartmentComplexRepository apartmentComplexRepository;
    private final ComplexPolicyRepository complexPolicyRepository;
    private final ApartmentComplexOutboxService apartmentComplexOutboxService;

    // 단지 code로 단지를 조회한다
    private ApartmentComplex getComplexByCode(String code) {
        return apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));
    }

    // null 또는 음수 값을 검증한다
    private void validatePositiveOrZero(Number value) {
        if (value == null || value.doubleValue() < 0) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // 기본 정책 설정 서비스 API-206
    public ComplexPolicyPutRes updateBasicPolicy(String code, ComplexPolicyPutReq req) {
        // 단지 존재 여부 확인
        ApartmentComplex apartmentComplex = getComplexByCode(code);
        //요청값 검증 (baseFee, paymentDueDay, lateFeeRate)
        validatePositiveOrZero(req.getBaseFee());
        validatePositiveOrZero(req.getPaymentDueDay());
        validatePositiveOrZero(req.getLateFeeRate());
        // 납부기한일은 1~31일만 허용한다.
        if (req.getPaymentDueDay() < 1 || req.getPaymentDueDay() > 31) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 기존 정책이 있으면 수정하고, 없으면 새로 생성한다.
        ComplexPolicy complexPolicy = complexPolicyRepository.findByComplexId(apartmentComplex.getId())
                .orElseGet(() -> ComplexPolicy.builder()
                        .complexId(apartmentComplex.getId())
                        .baseFee(req.getBaseFee())
                        .paymentDueDay(req.getPaymentDueDay())
                        .lateFeeRate(req.getLateFeeRate())
                        .lateFeeUnit("MONTHLY")
                        .isActive(true)
                        .startDate(LocalDate.now())
                        .endDate(null)
                        .build());

        // 기존 정책 또는 신규 정책에 현재 요청값을 반영한다.
        complexPolicy.apply(
                req.getBaseFee(),
                req.getPaymentDueDay(),
                req.getLateFeeRate(),
                "MONTHLY",
                true,
                LocalDate.now(),
                null
        );

        // 신규 정책은 INSERT, 기존 정책은 UPDATE 처리된다.
        ComplexPolicy savedPolicy = complexPolicyRepository.save(complexPolicy);

        // 정책 변경 이벤트를 Outbox에 적재한다.
        apartmentComplexOutboxService.saveComplexPolicyUpdatedEvent(savedPolicy);

        // 저장 결과를 응답한다.
        return ComplexPolicyPutRes.builder()
                .complexId(apartmentComplex.getCode())
                .baseFee(savedPolicy.getBaseFee())
                .paymentDueDay(savedPolicy.getPaymentDueDay())
                .lateFeeRate(savedPolicy.getLateFeeRate())
                .updatedAt(savedPolicy.getUpdatedAt())
                .build();
    }

    // 차량 정책 설정 서비스 API-207
    public VehiclePolicyPutRes updateVehiclePolicy(String code, VehiclePolicyPutReq req) {
        // TODO: 차량 정책 설정 로직 구현
        return VehiclePolicyPutRes.builder()
                .apartmentComplexUid(code)
                .maxVehicleCountPerHousehold(req.getMaxVehicleCountPerHousehold())
                .freeVehicleCount(req.getFreeVehicleCount())
                .extraVehicleFee(req.getExtraVehicleFee())
                .visitorFreeMinutes(req.getVisitorFreeMinutes())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 시설 정책 설정 서비스 API-208
    public FacilityPolicyPutRes updateFacilityPolicy(String code, FacilityPolicyPutReq req) {
        // TODO: 시설 정책 설정 로직 구현
        return FacilityPolicyPutRes.builder()
                .apartmentComplexUid(code)
                .reservationSlotMin(req.getReservationSlotMin())
                .facilityCancelDeadlineHours(req.getFacilityCancelDeadlineHours())
                .gxWaitingEnabled(req.getGxWaitingEnabled())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량 정책 설정 서비스 API-209
    public VisitorPolicyPutRes updateVisitorPolicy(String code, VisitorPolicyPutReq req) {
        // TODO: 방문차량 정책 설정 로직 구현
        return VisitorPolicyPutRes.builder()
                .apartmentComplexUid(code)
                .freeMinutes(req.getFreeMinutes())
                .extraFeePerUnit(req.getExtraFeePerUnit())
                .extraFeeUnitMinutes(req.getExtraFeeUnitMinutes())
                .dailyMaxFee(req.getDailyMaxFee())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
