package com.apten.parkingvehicle.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.parkingvehicle.application.model.request.VehiclePolicyPutReq;
import com.apten.parkingvehicle.application.model.request.VisitorPolicyPutReq;
import com.apten.parkingvehicle.application.model.response.VehiclePolicyPutRes;
import com.apten.parkingvehicle.application.model.response.VisitorPolicyPutRes;
import com.apten.parkingvehicle.domain.entity.VehiclePolicy;
import com.apten.parkingvehicle.domain.entity.VisitorPolicy;
import com.apten.parkingvehicle.domain.repository.VehiclePolicyRepository;
import com.apten.parkingvehicle.domain.repository.VisitorPolicyRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 차량 정책과 방문차량 정책 원본 저장을 담당하는 응용 서비스이다.
// parking-vehicle-service가 정책 원본을 직접 관리하는 A안 기준으로 동작한다.
@Service
@Transactional
@RequiredArgsConstructor
public class ParkingPolicyService {

    // 차량 정책 원본 저장소이다.
    private final VehiclePolicyRepository vehiclePolicyRepository;

    // 방문차량 정책 원본 저장소이다.
    private final VisitorPolicyRepository visitorPolicyRepository;

    // 차량 정책은 단지 기준 목록 전체를 교체하는 방식으로 저장한다.
    public VehiclePolicyPutRes updateVehiclePolicy(Long complexId, VehiclePolicyPutReq req) {
        // 단지 식별자와 요청 본문이 모두 있어야 전체 교체 저장을 진행할 수 있다.
        if (complexId == null || req == null || req.getPolicies() == null || req.getPolicies().isEmpty()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 같은 차량 대수가 두 번 들어오지 않도록 먼저 중복을 검증한다.
        Set<Integer> carCounts = new HashSet<>();
        for (VehiclePolicyPutReq.VehiclePolicyItem item : req.getPolicies()) {
            // 차량 대수는 1 이상만 허용한다.
            if (item.getCarCount() == null || item.getCarCount() <= 0) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }

            // 월 요금은 null 또는 음수가 들어오면 안 된다.
            validatePositiveOrZero(item.getMonthlyFee());

            // 같은 차량 대수가 중복되면 정책 의미가 모호해지므로 차단한다.
            if (!carCounts.add(item.getCarCount())) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
        }

        // 차량 정책은 목록 전체 교체 방식이므로 기존 정책을 먼저 모두 지운다.
        vehiclePolicyRepository.deleteByComplexId(complexId);

        // 요청 목록을 새 vehicle_policy 원본 엔티티 목록으로 변환한다.
        List<VehiclePolicy> policies = req.getPolicies().stream()
                .map(item -> VehiclePolicy.builder()
                        .complexId(complexId)
                        .carCount(item.getCarCount())
                        .monthlyFee(item.getMonthlyFee())
                        .isLimitRule(true)
                        .isActive(true)
                        .build())
                .toList();

        // 새 차량 정책 목록을 한 번에 저장한다.
        List<VehiclePolicy> savedPolicies = vehiclePolicyRepository.saveAll(policies);

        // 저장 결과를 응답 DTO 목록으로 변환한다.
        List<VehiclePolicyPutRes.VehiclePolicyItem> responsePolicies = savedPolicies.stream()
                .map(policy -> VehiclePolicyPutRes.VehiclePolicyItem.builder()
                        .carCount(policy.getCarCount())
                        .monthlyFee(policy.getMonthlyFee())
                        .build())
                .toList();

        // 전체 교체 완료 결과를 응답으로 돌려준다.
        return VehiclePolicyPutRes.builder()
                .complexId(complexId)
                .policies(responsePolicies)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 방문차량 정책은 단지당 1건만 유지하므로 upsert 방식으로 저장한다.
    public VisitorPolicyPutRes updateVisitorPolicy(Long complexId, VisitorPolicyPutReq req) {
        // 단지 식별자와 요청 본문이 모두 있어야 upsert 저장을 진행할 수 있다.
        if (complexId == null || req == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 무료 시간은 null 또는 음수가 들어오면 안 된다.
        validatePositiveOrZero(req.getFreeMinutes());

        // 시간당 요금은 null 또는 음수가 들어오면 안 된다.
        validatePositiveOrZero(req.getHourFee());

        // 월 기준 시간은 null 또는 음수가 들어오면 안 된다.
        validatePositiveOrZero(req.getMonthlyLimitHours());

        // 기존 정책이 있으면 갱신하고 없으면 새 정책을 만든다.
        VisitorPolicy visitorPolicy = visitorPolicyRepository.findByComplexId(complexId)
                .orElseGet(() -> VisitorPolicy.builder()
                        .complexId(complexId)
                        .build());

        // 요청값을 현재 정책 엔티티에 반영한다.
        visitorPolicy.apply(req);

        // 신규 생성 또는 기존 갱신 결과를 저장한다.
        VisitorPolicy savedPolicy = visitorPolicyRepository.save(visitorPolicy);

        // 저장 결과를 응답 DTO로 변환한다.
        return VisitorPolicyPutRes.builder()
                .complexId(complexId)
                .freeMinutes(savedPolicy.getFreeMinutes())
                .hourFee(savedPolicy.getHourFee())
                .monthlyLimitHours(savedPolicy.getMonthlyLimitHours())
                .isActive(savedPolicy.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // null 또는 음수 값이 정책에 들어오지 않게 공통 검증한다.
    private void validatePositiveOrZero(Number value) {
        if (value == null || value.doubleValue() < 0) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }
}
