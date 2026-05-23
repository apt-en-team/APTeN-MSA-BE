package com.apten.household.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.household.application.model.dto.HouseholdRequestContext;
import com.apten.household.application.model.request.ExpectedResidentCreateReq;
import com.apten.household.application.model.request.ExpectedResidentListReq;
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

        Household household = householdRepository.findById(request.getHouseholdId())
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND));
        validateHouseholdComplex(context.getComplexId(), household);

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

    private void validateHouseholdComplex(Long complexId, Household household) {
        if (!household.getComplexId().equals(complexId)) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND);
        }
    }

    private HouseholdMemberRole resolveHouseholdRole(HouseholdMemberRole householdRole) {
        return householdRole == null ? HouseholdMemberRole.MEMBER : householdRole;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
