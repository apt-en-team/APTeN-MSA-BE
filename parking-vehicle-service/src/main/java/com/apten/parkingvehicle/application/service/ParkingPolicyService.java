package com.apten.parkingvehicle.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.parkingvehicle.application.model.request.VehiclePolicyPutReq;
import com.apten.parkingvehicle.application.model.request.VehicleRegistrationPolicyPutReq;
import com.apten.parkingvehicle.application.model.request.VisitorPolicyPutReq;
import com.apten.parkingvehicle.application.model.response.VehiclePolicyListRes;
import com.apten.parkingvehicle.application.model.response.VehiclePolicyPutRes;
import com.apten.parkingvehicle.application.model.response.VehicleRegistrationPolicyGetRes;
import com.apten.parkingvehicle.application.model.response.VehicleRegistrationPolicyPutRes;
import com.apten.parkingvehicle.application.model.response.VisitorPolicyGetRes;
import com.apten.parkingvehicle.application.model.response.VisitorPolicyPutRes;
import com.apten.parkingvehicle.domain.entity.VehiclePolicy;
import com.apten.parkingvehicle.domain.entity.VehicleRegistrationPolicy;
import com.apten.parkingvehicle.domain.entity.VisitorPolicy;
import com.apten.parkingvehicle.domain.repository.VehiclePolicyRepository;
import com.apten.parkingvehicle.domain.repository.VehicleRegistrationPolicyRepository;
import com.apten.parkingvehicle.domain.repository.VisitorPolicyRepository;
import com.apten.parkingvehicle.exception.ParkingVehicleErrorCode;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 차량 정책과 방문차량 정책 원본 관리 API를 담당하는 응용 서비스이다.
@Service
@RequiredArgsConstructor
public class ParkingPolicyService {

    // 차량 대수별 요금 정책 저장소
    private final VehiclePolicyRepository vehiclePolicyRepository;

    // 방문차량 정책 저장소
    private final VisitorPolicyRepository visitorPolicyRepository;

    // 입주민 차량 등록 한도 정책 저장소
    private final VehicleRegistrationPolicyRepository vehicleRegistrationPolicyRepository;

    // 정책 전체 교체 시 DELETE 즉시 반영용 EntityManager
    private final EntityManager entityManager;

    // 단지 차량 대수별 요금 정책 전체 교체 — 검증 통과 후 기존 row 삭제 + 새 목록 저장
    @Transactional
    public VehiclePolicyPutRes updateVehiclePolicy(
            VehiclePolicyPutReq req, String userRole, Long complexId, Long selectedComplexId
    ) {
        // 관리자 컨텍스트 해석 (MASTER는 selectedComplexId, 일반 관리자는 X-COMPLEX-ID)
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 요청 null / 빈 목록 검증 (빈 PUT으로 정책 전체 삭제는 차단)
        if (req == null || req.getPolicies() == null || req.getPolicies().isEmpty()) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }

        // 각 item 검증 (null / carCount null|<1 / monthlyFee null|음수 / carCount 중복)
        Set<Integer> seenCarCounts = new HashSet<>();
        for (VehiclePolicyPutReq.VehiclePolicyItem item : req.getPolicies()) {
            if (item == null
                    || item.getCarCount() == null
                    || item.getCarCount() < 1
                    || item.getMonthlyFee() == null
                    || item.getMonthlyFee().signum() < 0) {
                throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
            }
            // carCount 중복 차단
            if (!seenCarCounts.add(item.getCarCount())) {
                throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
            }
        }

        // 기존 row 전체 삭제
        vehiclePolicyRepository.deleteByComplexId(targetComplexId);

        // DELETE를 DB에 즉시 반영 — 후속 INSERT의 (complex_id, car_count) 유니크 제약 충돌 방지
        entityManager.flush();

        // 새 row 목록 builder로 매핑 (isLimitRule, isActive는 엔티티 @Builder.Default 적용)
        List<VehiclePolicy> newEntities = req.getPolicies().stream()
                .map(item -> VehiclePolicy.builder()
                        .complexId(targetComplexId)
                        .carCount(item.getCarCount())
                        .monthlyFee(item.getMonthlyFee())
                        .build())
                .toList();

        // saveAll로 일괄 저장
        List<VehiclePolicy> saved = vehiclePolicyRepository.saveAll(newEntities);

        // 응답 빌드 — carCount 오름차순 정렬로 사용자 친화 노출
        List<VehiclePolicyPutRes.VehiclePolicyItem> items = saved.stream()
                .sorted(Comparator.comparingInt(VehiclePolicy::getCarCount))
                .map(e -> VehiclePolicyPutRes.VehiclePolicyItem.builder()
                        .carCount(e.getCarCount())
                        .monthlyFee(e.getMonthlyFee())
                        .build())
                .toList();

        return VehiclePolicyPutRes.builder()
                .complexId(targetComplexId)
                .policies(items)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 단지 차량 대수별 요금 정책 목록 조회 — carCount 오름차순 정렬, 빈 단지는 빈 목록 응답
    @Transactional(readOnly = true)
    public VehiclePolicyListRes getVehiclePolicies(String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석 (MASTER는 selectedComplexId, 일반 관리자는 X-COMPLEX-ID)
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 단지 차량 정책 목록 조회 (활성·비활성 전부 — 관리자 화면이 정책 상태까지 노출)
        List<VehiclePolicy> entities = vehiclePolicyRepository.findByComplexId(targetComplexId);

        // 응답 항목 빌드 — carCount 오름차순 정렬로 PUT 응답과 일관
        List<VehiclePolicyListRes.Item> items = entities.stream()
                .sorted(Comparator.comparingInt(VehiclePolicy::getCarCount))
                .map(e -> VehiclePolicyListRes.Item.builder()
                        .carCount(e.getCarCount())
                        .monthlyFee(e.getMonthlyFee())
                        .isLimitRule(e.getIsLimitRule())
                        .isActive(e.getIsActive())
                        .build())
                .toList();

        // updatedAt — N행 중 가장 최근 updatedAt (정책이 마지막으로 바뀐 시각). 빈 목록이면 null
        LocalDateTime updatedAt = entities.stream()
                .map(VehiclePolicy::getUpdatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return VehiclePolicyListRes.builder()
                .complexId(targetComplexId)
                .policies(items)
                .updatedAt(updatedAt)
                .build();
    }

    // 단지 방문차량 정책 upsert — 기존 row면 부분 갱신, 없으면 핵심 필드 검증 후 신규 생성
    @Transactional
    public VisitorPolicyPutRes updateVisitorPolicy(
            VisitorPolicyPutReq req, String userRole, Long complexId, Long selectedComplexId
    ) {
        // 관리자 컨텍스트 해석 (MASTER는 selectedComplexId, 일반 관리자는 X-COMPLEX-ID)
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 요청 null 검증
        if (req == null) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }

        // 음수 검증 — hourFee/monthlyLimitHours (null은 부분 갱신 통과)
        if ((req.getHourFee() != null && req.getHourFee().signum() < 0)
                || (req.getMonthlyLimitHours() != null && req.getMonthlyLimitHours() < 0)) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }

        // 단지 정책 row 조회
        Optional<VisitorPolicy> existing = visitorPolicyRepository.findByComplexId(targetComplexId);

        VisitorPolicy entity;
        if (existing.isPresent()) {
            // 수정 — apply가 null skip으로 부분 갱신
            entity = existing.get();
            entity.apply(req);
        } else {
            // 신규 생성 — 핵심 필드(hourFee, monthlyLimitHours) 필수
            if (req.getHourFee() == null || req.getMonthlyLimitHours() == null) {
                throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
            }
            // isActive는 null이면 디폴트 true로 생성
            entity = VisitorPolicy.builder()
                    .complexId(targetComplexId)
                    .hourFee(req.getHourFee())
                    .monthlyLimitHours(req.getMonthlyLimitHours())
                    .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                    .build();
        }

        // 저장 (신규면 persist, 기존이면 dirty checking 명시화)
        VisitorPolicy saved = visitorPolicyRepository.save(entity);

        // 응답 빌드
        return VisitorPolicyPutRes.builder()
                .complexId(saved.getComplexId())
                .hourFee(saved.getHourFee())
                .monthlyLimitHours(saved.getMonthlyLimitHours())
                .isActive(saved.getIsActive())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    // 단지 방문차량 정책 조회 — row 없으면 complexId만 채운 빈 응답
    @Transactional(readOnly = true)
    public VisitorPolicyGetRes getVisitorPolicy(String userRole, Long complexId, Long selectedComplexId) {
        // 관리자 컨텍스트 해석 (MASTER는 selectedComplexId, 일반 관리자는 X-COMPLEX-ID)
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 단지 정책 row 조회
        Optional<VisitorPolicy> existing = visitorPolicyRepository.findByComplexId(targetComplexId);

        // row 없으면 complexId만 채워 빈 응답 (미설정 단지)
        if (existing.isEmpty()) {
            return VisitorPolicyGetRes.builder()
                    .complexId(targetComplexId)
                    .build();
        }

        // row 있으면 엔티티 필드 그대로 응답에 매핑
        VisitorPolicy entity = existing.get();
        return VisitorPolicyGetRes.builder()
                .complexId(entity.getComplexId())
                .hourFee(entity.getHourFee())
                .monthlyLimitHours(entity.getMonthlyLimitHours())
                .isActive(entity.getIsActive())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // 단지 차량 등록 한도 정책 upsert — 기존 row면 부분 갱신, 없으면 maxCarCount 검증 후 신규 생성
    @Transactional
    public VehicleRegistrationPolicyPutRes updateVehicleRegistrationPolicy(
            VehicleRegistrationPolicyPutReq req, String userRole, Long complexId, Long selectedComplexId
    ) {
        // 관리자 컨텍스트 해석 (MASTER는 selectedComplexId, 일반 관리자는 X-COMPLEX-ID)
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 요청 null 검증
        if (req == null) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }

        // 음수 검증 — maxCarCount (null은 부분 갱신 통과)
        if (req.getMaxCarCount() != null && req.getMaxCarCount() < 0) {
            throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
        }

        // 단지 정책 row 조회
        Optional<VehicleRegistrationPolicy> existing =
                vehicleRegistrationPolicyRepository.findByComplexId(targetComplexId);

        VehicleRegistrationPolicy entity;
        if (existing.isPresent()) {
            // 수정 — apply가 null skip으로 부분 갱신
            entity = existing.get();
            entity.apply(req);
        } else {
            // 신규 생성 — 핵심 필드(maxCarCount) 필수
            if (req.getMaxCarCount() == null) {
                throw new BusinessException(ParkingVehicleErrorCode.INVALID_PARAMETER);
            }
            // isActive는 null이면 디폴트 true로 생성
            entity = VehicleRegistrationPolicy.builder()
                    .complexId(targetComplexId)
                    .maxCarCount(req.getMaxCarCount())
                    .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                    .build();
        }

        // 저장 (신규면 persist, 기존이면 dirty checking 명시화)
        VehicleRegistrationPolicy saved = vehicleRegistrationPolicyRepository.save(entity);

        // 응답 빌드
        return VehicleRegistrationPolicyPutRes.builder()
                .complexId(saved.getComplexId())
                .maxCarCount(saved.getMaxCarCount())
                .isActive(saved.getIsActive())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    // 단지 차량 등록 한도 정책 조회 — row 없으면 complexId만 채운 빈 응답
    @Transactional(readOnly = true)
    public VehicleRegistrationPolicyGetRes getVehicleRegistrationPolicy(
            String userRole, Long complexId, Long selectedComplexId
    ) {
        // 관리자 컨텍스트 해석 (MASTER는 selectedComplexId, 일반 관리자는 X-COMPLEX-ID)
        Long targetComplexId = resolveAdminContextComplexId(userRole, complexId, selectedComplexId);

        // 단지 정책 row 조회
        Optional<VehicleRegistrationPolicy> existing =
                vehicleRegistrationPolicyRepository.findByComplexId(targetComplexId);

        // row 없으면 complexId만 채워 빈 응답 (미설정 단지)
        if (existing.isEmpty()) {
            return VehicleRegistrationPolicyGetRes.builder()
                    .complexId(targetComplexId)
                    .build();
        }

        // row 있으면 엔티티 필드 그대로 응답에 매핑
        VehicleRegistrationPolicy entity = existing.get();
        return VehicleRegistrationPolicyGetRes.builder()
                .complexId(entity.getComplexId())
                .maxCarCount(entity.getMaxCarCount())
                .isActive(entity.getIsActive())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // 관리자 주차 API의 단지 컨텍스트를 역할별 헤더 기준으로 해석한다.
    // MASTER는 선택 단지, 일반 관리자는 토큰 단지 기준으로 처리한다.
    private Long resolveAdminContextComplexId(String userRole, Long complexId, Long selectedComplexId) {
        if (userRole == null || userRole.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        if ("MASTER".equals(userRole)) {
            if (selectedComplexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return selectedComplexId;
        }

        if ("MANAGER".equals(userRole) || "ADMIN".equals(userRole)) {
            if (complexId == null) {
                throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
            }
            return complexId;
        }

        throw new BusinessException(CommonErrorCode.FORBIDDEN);
    }
}
