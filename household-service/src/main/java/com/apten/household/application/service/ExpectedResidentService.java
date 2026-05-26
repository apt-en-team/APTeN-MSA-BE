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
import com.apten.household.domain.enums.ExpectedResidentStatus;
import com.apten.household.domain.enums.HouseholdMemberRole;
import com.apten.household.domain.repository.ExpectedResidentRepository;
import com.apten.household.domain.repository.HouseholdRepository;
import com.apten.household.exception.HouseholdErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 관리자 입주민 명부를 등록하고 조회하는 서비스이다.
@Service
@Transactional
@RequiredArgsConstructor
public class ExpectedResidentService {

    private final ExpectedResidentRepository expectedResidentRepository;
    private final HouseholdRepository householdRepository;

    // 관리자 요청을 검증하고 명부 등록 응답을 조립한다.
    public ExpectedResidentRes registerExpectedResident(HouseholdRequestContext context, ExpectedResidentCreateReq request) {
        validateCreateRequest(context, request);

        Household household = getHouseholdForComplex(context.getComplexId(), request.getHouseholdId());
        validateDuplicateExpectedResident(null, household.getId(), request.getName(), request.getPhone(), request.getBirthDate());

        ExpectedResident expectedResident = createExpectedResident(context, request, household);
        return ExpectedResidentRes.from(expectedResident);
    }

    // 관리자 명부 목록을 조회한다.
    @Transactional(readOnly = true)
    public ExpectedResidentListRes getExpectedResidentList(HouseholdRequestContext context, ExpectedResidentListReq request) {
        int pageNumber = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ExpectedResident> page = expectedResidentRepository.findByFilters(
                context.getComplexId(),
                request.getStatus(),
                pageable
        );

        return ExpectedResidentListRes.builder()
                .content(page.getContent().stream()
                        .map(ExpectedResidentRes::from)
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
                relationship,
                householdRole
        );

        return ExpectedResidentRes.from(expectedResidentRepository.save(expectedResident));
    }

    // 관리자 명부를 자동매칭 대상에서 제외한다.
    public ExpectedResidentRes disableExpectedResident(HouseholdRequestContext context, Long expectedResidentId) {
        if (context == null || context.getComplexId() == null || expectedResidentId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        ExpectedResident expectedResident = getExpectedResidentForComplex(context.getComplexId(), expectedResidentId);
        validateEditableExpectedResident(expectedResident);
        expectedResident.disable();

        return ExpectedResidentRes.from(expectedResidentRepository.save(expectedResident));
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
                .relationship(request.getRelationship())
                .householdRole(resolveHouseholdRole(request.getHouseholdRole()))
                .status(ExpectedResidentStatus.AVAILABLE)
                .createdByUserId(context.getUserId())
                .build();

        return expectedResidentRepository.save(expectedResident);
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
                || request.getBirthDate() == null) {
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
