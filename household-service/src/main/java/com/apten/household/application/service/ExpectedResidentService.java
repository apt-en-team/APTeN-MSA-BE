package com.apten.household.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.household.application.model.dto.HouseholdRequestContext;
import com.apten.household.application.model.request.ExpectedResidentCreateReq;
import com.apten.household.application.model.request.ExpectedResidentListReq;
import com.apten.household.application.model.request.ExpectedResidentPatchReq;
import com.apten.household.application.model.response.ExpectedResidentListRes;
import com.apten.household.application.model.response.ExpectedResidentRes;
import com.apten.household.domain.entity.ExpectedResident;
import com.apten.household.domain.entity.Household;
import com.apten.household.domain.entity.HouseholdHistory;
import com.apten.household.domain.entity.HouseholdMatchRequest;
import com.apten.household.domain.enums.ExpectedResidentStatus;
import com.apten.household.domain.enums.HouseholdMatchStatus;
import com.apten.household.domain.enums.HouseholdMemberRole;
import com.apten.household.domain.enums.HouseholdStatus;
import com.apten.household.domain.repository.ExpectedResidentRepository;
import com.apten.household.domain.repository.HouseholdHistoryRepository;
import com.apten.household.domain.repository.HouseholdMatchRequestRepository;
import com.apten.household.domain.repository.HouseholdMemberRepository;
import com.apten.household.domain.repository.HouseholdRepository;
import com.apten.household.infrastructure.kafka.HouseholdOutboxService;
import com.apten.household.exception.HouseholdErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 관리자 입주민 명부를 등록하고 조회하는 서비스이다.
@Service
@Transactional
@RequiredArgsConstructor
public class ExpectedResidentService {

    private final ExpectedResidentRepository expectedResidentRepository;
    private final HouseholdRepository householdRepository;
    private final HouseholdMatchRequestRepository householdMatchRequestRepository;
    private final HouseholdMemberRepository householdMemberRepository;
    private final HouseholdHistoryRepository householdHistoryRepository;
    private final HouseholdOutboxService householdOutboxService;

    // 관리자 요청을 검증하고 명부 등록 응답을 조립한다.
    public ExpectedResidentRes registerExpectedResident(HouseholdRequestContext context, ExpectedResidentCreateReq request) {
        validateCreateRequest(context, request);

        Household household = getHouseholdForComplex(context.getComplexId(), request.getHouseholdId());
        validateDuplicateExpectedResident(null, household.getId(), request.getName(), request.getPhone(), request.getBirthDate());

        ExpectedResident expectedResident = createExpectedResident(context, request, household);
        occupyHouseholdIfMoveInDateDue(household, expectedResident.getMoveInDate(), LocalDate.now());
        saveHouseholdHistory(
                household,
                "등록입주민 추가: " + expectedResident.getName()
        );
        return toExpectedResidentRes(expectedResident);
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void occupyHouseholdsByMoveInDate() {
        occupyHouseholdsByMoveInDate(LocalDate.now());
    }

    @Transactional
    public void occupyHouseholdsByMoveInDate(LocalDate today) {
        expectedResidentRepository.findDueHouseholdIds(ExpectedResidentStatus.AVAILABLE, today).stream()
                .distinct()
                .map(householdRepository::findById)
                .flatMap(java.util.Optional::stream)
                .forEach(this::occupyHouseholdIfVacant);
    }

    // 관리자 명부 목록을 조회한다.
    @Transactional(readOnly = true)
    public ExpectedResidentListRes getExpectedResidentList(HouseholdRequestContext context, ExpectedResidentListReq request) {
        int pageNumber = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ExpectedResident> page = findExpectedResidents(context.getComplexId(), request, pageable);

        return ExpectedResidentListRes.builder()
                .content(page.getContent().stream()
                        .map(this::toExpectedResidentRes)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    // 관리자 명부 정보를 수정한다.
    public ExpectedResidentRes updateExpectedResident(
            HouseholdRequestContext context,
            Long expectedResidentId,
            ExpectedResidentPatchReq request
    ) {
        validatePatchRequest(context, expectedResidentId, request);

        ExpectedResident expectedResident = getExpectedResidentForComplex(context.getComplexId(), expectedResidentId);
        validateEditableExpectedResident(expectedResident);

        Long householdId = request.getHouseholdId() != null ? request.getHouseholdId() : expectedResident.getHouseholdId();
        Household household = getHouseholdForComplex(context.getComplexId(), householdId);

        String name = resolveText(request.getName(), expectedResident.getName());
        String phone = resolveText(request.getPhone(), expectedResident.getPhone());
        var birthDate = request.getBirthDate() != null ? request.getBirthDate() : expectedResident.getBirthDate();
        LocalDate moveInDate = request.getMoveInDate() != null ? request.getMoveInDate() : expectedResident.getMoveInDate();
        String relationship = request.getRelationship() != null ? request.getRelationship() : expectedResident.getRelationship();
        HouseholdMemberRole householdRole = request.getHouseholdRole() != null
                ? request.getHouseholdRole()
                : expectedResident.getHouseholdRole();

        validateDuplicateExpectedResident(expectedResident.getId(), household.getId(), name, phone, birthDate);
        expectedResident.update(
                household.getId(),
                household.getBuilding(),
                household.getUnit(),
                name,
                phone,
                birthDate,
                moveInDate,
                relationship,
                householdRole
        );

        ExpectedResident savedExpectedResident = expectedResidentRepository.save(expectedResident);
        occupyHouseholdIfMoveInDateDue(household, savedExpectedResident.getMoveInDate(), LocalDate.now());
        saveHouseholdHistory(
                household,
                "등록입주민 수정: " + savedExpectedResident.getName()
        );
        return toExpectedResidentRes(savedExpectedResident);
    }

    // 관리자 명부를 자동매칭 대상에서 제외한다.
    public ExpectedResidentRes disableExpectedResident(HouseholdRequestContext context, Long expectedResidentId) {
        if (context == null || context.getComplexId() == null || expectedResidentId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        ExpectedResident expectedResident = getExpectedResidentForComplex(context.getComplexId(), expectedResidentId);
        if (expectedResident.getStatus() == ExpectedResidentStatus.DISABLED) {
            return toExpectedResidentRes(expectedResident);
        }
        expectedResident.disable();
        saveHouseholdHistory(
                getHouseholdForComplex(context.getComplexId(), expectedResident.getHouseholdId()),
                "등록입주민 삭제: " + expectedResident.getName()
        );

        return toExpectedResidentRes(expectedResidentRepository.save(expectedResident));
    }

    private void saveHouseholdHistory(Household household, String reason) {
        householdHistoryRepository.save(HouseholdHistory.builder()
                .householdId(household.getId())
                .fromStatus(household.getStatus())
                .toStatus(household.getStatus())
                .reason(reason)
                .changedAt(LocalDateTime.now())
                .build());
    }

    // 실제 관리자 명부 엔티티를 생성하고 저장한다.
    private ExpectedResident createExpectedResident(
            HouseholdRequestContext context,
            ExpectedResidentCreateReq request,
            Household household
    ) {
        ExpectedResident expectedResident = ExpectedResident.builder()
                .complexId(context.getComplexId())
                .householdId(household.getId())
                .building(household.getBuilding())
                .unit(household.getUnit())
                .name(request.getName())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .moveInDate(request.getMoveInDate())
                .relationship(request.getRelationship())
                .householdRole(resolveHouseholdRole(request.getHouseholdRole()))
                .status(ExpectedResidentStatus.AVAILABLE)
                .createdByUserId(context.getUserId())
                .build();

        return expectedResidentRepository.save(expectedResident);
    }

    private Page<ExpectedResident> findExpectedResidents(Long complexId, ExpectedResidentListReq request, Pageable pageable) {
        if (request.getHouseholdId() != null && request.getStatus() != null) {
            return expectedResidentRepository.findByComplexIdAndHouseholdIdAndStatus(
                    complexId,
                    request.getHouseholdId(),
                    request.getStatus(),
                    pageable
            );
        }
        if (request.getHouseholdId() != null) {
            return expectedResidentRepository.findByComplexIdAndHouseholdIdAndStatusNot(
                    complexId,
                    request.getHouseholdId(),
                    ExpectedResidentStatus.DISABLED,
                    pageable
            );
        }
        if (request.getStatus() != null) {
            return expectedResidentRepository.findByComplexIdAndStatus(complexId, request.getStatus(), pageable);
        }
        return expectedResidentRepository.findByComplexIdAndStatusNot(
                complexId,
                ExpectedResidentStatus.DISABLED,
                pageable
        );
    }

    private ExpectedResidentRes toExpectedResidentRes(ExpectedResident expectedResident) {
        HouseholdMatchRequest latestMatchRequest = householdMatchRequestRepository
                .findTopByComplexIdAndInputNameAndInputPhoneAndInputBirthDateAndInputBuildingAndInputUnitOrderByCreatedAtDesc(
                        expectedResident.getComplexId(),
                        expectedResident.getName(),
                        expectedResident.getPhone(),
                        expectedResident.getBirthDate(),
                        expectedResident.getBuilding(),
                        expectedResident.getUnit()
                )
                .orElse(null);

        WebServiceStatus webServiceStatus = resolveWebServiceStatus(expectedResident, latestMatchRequest);

        return ExpectedResidentRes.builder()
                .expectedResidentId(expectedResident.getId())
                .complexId(expectedResident.getComplexId())
                .householdId(expectedResident.getHouseholdId())
                .building(expectedResident.getBuilding())
                .unit(expectedResident.getUnit())
                .name(expectedResident.getName())
                .phone(expectedResident.getPhone())
                .birthDate(expectedResident.getBirthDate())
                .moveInDate(expectedResident.getMoveInDate())
                .relationship(expectedResident.getRelationship())
                .householdRole(expectedResident.getHouseholdRole())
                .status(expectedResident.getStatus())
                .webServiceStatus(webServiceStatus.code)
                .webServiceStatusName(webServiceStatus.label)
                .matchProcessType(latestMatchRequest == null ? null : latestMatchRequest.getProcessType().getCode())
                .matchProcessTypeName(latestMatchRequest == null ? null : latestMatchRequest.getProcessType().getValue())
                .matchedUserId(expectedResident.getMatchedUserId())
                .matchedAt(expectedResident.getMatchedAt())
                .createdAt(expectedResident.getCreatedAt())
                .build();
    }

    private WebServiceStatus resolveWebServiceStatus(
            ExpectedResident expectedResident,
            HouseholdMatchRequest latestMatchRequest
    ) {
        if (isActiveHouseholdMember(expectedResident)) {
            return WebServiceStatus.COMPLETED;
        }
        if (latestMatchRequest == null) {
            return WebServiceStatus.NOT_SIGNED_UP;
        }
        if (latestMatchRequest.getMatchStatus() == HouseholdMatchStatus.APPROVED
                && isActiveHouseholdMember(expectedResident.getHouseholdId(), latestMatchRequest.getUserId())) {
            return WebServiceStatus.COMPLETED;
        }
        if (latestMatchRequest.getMatchStatus() == HouseholdMatchStatus.PENDING) {
            return WebServiceStatus.PENDING;
        }
        if (latestMatchRequest.getMatchStatus() == HouseholdMatchStatus.REJECTED) {
            return WebServiceStatus.REJECTED;
        }
        return WebServiceStatus.NOT_SIGNED_UP;
    }

    private boolean isActiveHouseholdMember(ExpectedResident expectedResident) {
        return expectedResident.getMatchedUserId() != null
                && isActiveHouseholdMember(expectedResident.getHouseholdId(), expectedResident.getMatchedUserId());
    }

    private boolean isActiveHouseholdMember(Long householdId, Long userId) {
        return householdId != null
                && userId != null
                && householdMemberRepository.findByHouseholdIdAndUserIdAndIsActiveTrue(householdId, userId).isPresent();
    }

    private enum WebServiceStatus {
        NOT_SIGNED_UP("NOT_SIGNED_UP", "미가입"),
        COMPLETED("COMPLETED", "가입"),
        PENDING("PENDING", "대기"),
        REJECTED("REJECTED", "반려");

        private final String code;
        private final String label;

        WebServiceStatus(String code, String label) {
            this.code = code;
            this.label = label;
        }
    }

    private void occupyHouseholdIfMoveInDateDue(Household household, LocalDate moveInDate, LocalDate today) {
        if (moveInDate == null || moveInDate.isAfter(today)) {
            return;
        }
        occupyHouseholdIfVacant(household);
    }

    private void occupyHouseholdIfVacant(Household household) {
        if (household.getStatus() != HouseholdStatus.VACANT) {
            return;
        }
        household.changeStatus(HouseholdStatus.OCCUPIED);
        Household savedHousehold = householdRepository.save(household);
        householdOutboxService.saveHouseholdUpdatedEvent(savedHousehold);
    }

    private Household getHouseholdForComplex(Long complexId, Long householdId) {
        Household household = householdRepository.findById(householdId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND));
        validateHouseholdComplex(complexId, household);
        return household;
    }

    private ExpectedResident getExpectedResidentForComplex(Long complexId, Long expectedResidentId) {
        ExpectedResident expectedResident = expectedResidentRepository.findById(expectedResidentId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.EXPECTED_RESIDENT_NOT_FOUND));
        if (!expectedResident.getComplexId().equals(complexId)) {
            throw new BusinessException(HouseholdErrorCode.EXPECTED_RESIDENT_NOT_FOUND);
        }
        return expectedResident;
    }

    private void validateDuplicateExpectedResident(
            Long currentExpectedResidentId,
            Long householdId,
            String name,
            String phone,
            java.time.LocalDate birthDate
    ) {
        String normalizedName = normalizeName(name);
        String normalizedPhone = normalizePhone(phone);
        boolean duplicated = expectedResidentRepository.findByHouseholdIdAndStatus(householdId, ExpectedResidentStatus.AVAILABLE)
                .stream()
                .filter(expectedResident -> currentExpectedResidentId == null
                        || !expectedResident.getId().equals(currentExpectedResidentId))
                .anyMatch(expectedResident ->
                        normalizeName(expectedResident.getName()).equals(normalizedName)
                                && normalizePhone(expectedResident.getPhone()).equals(normalizedPhone)
                                && expectedResident.getBirthDate().equals(birthDate));

        if (duplicated) {
            throw new BusinessException(HouseholdErrorCode.DUPLICATE_EXPECTED_RESIDENT);
        }
    }

    private void validateCreateRequest(HouseholdRequestContext context, ExpectedResidentCreateReq request) {
        if (context == null
                || context.getUserId() == null
                || context.getComplexId() == null
                || request == null
                || request.getHouseholdId() == null
                || isBlank(request.getName())
                || isBlank(request.getPhone())
                || request.getBirthDate() == null
                || request.getMoveInDate() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private void validatePatchRequest(HouseholdRequestContext context, Long expectedResidentId, ExpectedResidentPatchReq request) {
        if (context == null
                || context.getComplexId() == null
                || expectedResidentId == null
                || request == null
                || isBlankWhenPresent(request.getName())
                || isBlankWhenPresent(request.getPhone())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private void validateHouseholdComplex(Long complexId, Household household) {
        if (!household.getComplexId().equals(complexId)) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND);
        }
    }

    private void validateEditableExpectedResident(ExpectedResident expectedResident) {
        if (expectedResident.getStatus() != ExpectedResidentStatus.AVAILABLE) {
            throw new BusinessException(HouseholdErrorCode.EXPECTED_RESIDENT_STATUS_INVALID);
        }
    }

    private HouseholdMemberRole resolveHouseholdRole(HouseholdMemberRole householdRole) {
        return householdRole == null ? HouseholdMemberRole.MEMBER : householdRole;
    }

    private String resolveText(String requestValue, String currentValue) {
        return requestValue != null ? requestValue : currentValue;
    }

    private String normalizeName(String value) {
        return value == null ? "" : value.replaceAll("\\s+", "");
    }

    private String normalizePhone(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isBlankWhenPresent(String value) {
        return value != null && value.isBlank();
    }
}
