package com.apten.household.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.common.enumcode.EnumMapperType;
import com.apten.common.kafka.payload.HouseholdMatchRequestEventPayload;
import com.apten.common.kafka.payload.HouseholdMatchResultEventPayload;
import com.apten.household.application.model.request.HouseholdMatchBulkApproveReq;
import com.apten.household.application.model.request.HouseholdMatchListReq;
import com.apten.household.application.model.request.HouseholdMatchPostReq;
import com.apten.household.application.model.request.HouseholdMatchRejectReq;
import com.apten.household.application.model.response.HouseholdMatchApproveRes;
import com.apten.household.application.model.response.HouseholdMatchListRes;
import com.apten.household.application.model.response.HouseholdMatchPostRes;
import com.apten.household.application.model.response.HouseholdMatchRejectRes;
import com.apten.household.domain.entity.ExpectedResident;
import com.apten.household.domain.entity.Household;
import com.apten.household.domain.entity.HouseholdMatchRequest;
import com.apten.household.domain.entity.HouseholdMember;
import com.apten.household.domain.enums.ExpectedResidentStatus;
import com.apten.household.domain.enums.HouseholdMatchProcessType;
import com.apten.household.domain.enums.HouseholdMatchRejectReason;
import com.apten.household.domain.enums.HouseholdMatchStatus;
import com.apten.household.domain.enums.HouseholdMemberRole;
import com.apten.household.domain.enums.HouseholdStatus;
import com.apten.household.domain.repository.ExpectedResidentRepository;
import com.apten.household.domain.repository.HouseholdMatchRequestRepository;
import com.apten.household.domain.repository.HouseholdMemberRepository;
import com.apten.household.domain.repository.HouseholdRepository;
import com.apten.household.exception.HouseholdErrorCode;
import com.apten.household.infrastructure.kafka.HouseholdOutboxService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 세대 매칭 요청과 승인 처리를 담당하는 서비스이다.
@Service
@Transactional
@RequiredArgsConstructor
public class HouseholdMatchService {

    private final HouseholdOutboxService householdOutboxService;
    private final HouseholdMemberRepository householdMemberRepository;
    private final HouseholdMatchRequestRepository matchRequestRepository;
    private final HouseholdRepository householdRepository;
    private final ExpectedResidentRepository expectedResidentRepository;

    // 세대 매칭 요청을 생성하고 관리자 명부 기준 자동승인을 시도한다.
    public HouseholdMatchPostRes createMatchRequest(HouseholdMatchPostReq request) {
        validateMatchRequest(request);
        Optional<HouseholdMember> activeMemberOptional = householdMemberRepository.findActiveByUserIdAndComplexId(
                request.getUserId(),
                request.getComplexId()
        );
        if (activeMemberOptional.isPresent()) {
            return approveAlreadyActiveMember(request, activeMemberOptional.get());
        }
        validateDuplicateMatchRequest(request);

        Optional<ExpectedResident> expectedResidentOptional = findMatchingExpectedResident(request);
        HouseholdMatchRequest matchRequest = expectedResidentOptional
                .map(expectedResident -> approveByExpectedResident(request, expectedResident))
                .orElseGet(() -> createPendingMatchRequest(request));

        return HouseholdMatchPostRes.builder()
                .matchRequestId(matchRequest.getId())
                .matchedHouseholdId(matchRequest.getMatchedHouseholdId())
                .processType(matchRequest.getProcessType().getCode())
                .matchStatus(matchRequest.getMatchStatus().getCode())
                .createdAt(matchRequest.getCreatedAt())
                .build();
    }

    // 회원가입 이벤트 payload를 내부 매칭 요청 DTO로 변환해 처리한다.
    public HouseholdMatchPostRes createMatchRequest(HouseholdMatchRequestEventPayload payload) {
        HouseholdMatchPostReq request = HouseholdMatchPostReq.builder()
                .userId(payload.getUserId())
                .complexId(payload.getComplexId())
                .inputName(payload.getName())
                .inputPhone(payload.getPhone())
                .inputBirthDate(payload.getBirthDate())
                .inputBuilding(payload.getBuilding())
                .inputUnit(payload.getUnit())
                .build();

        return createMatchRequest(request);
    }

    // 수동 승인 대상 매칭 요청 목록을 조회한다.
    @Transactional(readOnly = true)
    public HouseholdMatchListRes getMatchRequestList(Long complexId, HouseholdMatchListReq request) {
        HouseholdMatchListReq resolvedRequest = request == null ? HouseholdMatchListReq.builder().build() : request;
        int pageNumber = resolvedRequest.getPage() != null ? resolvedRequest.getPage() : 0;
        int pageSize = resolvedRequest.getSize() != null ? resolvedRequest.getSize() : 20;
        HouseholdMatchStatus matchStatus = resolvedRequest.getMatchStatus();
        HouseholdMatchProcessType processType = resolvedRequest.getProcessType() != null
                ? resolvedRequest.getProcessType()
                : HouseholdMatchProcessType.MANUAL;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<HouseholdMatchRequest> page = findMatchRequests(
                complexId,
                matchStatus,
                processType,
                pageable
        );

        List<HouseholdMatchListRes.Item> content = page.getContent().stream()
                .map(matchRequest -> HouseholdMatchListRes.Item.builder()
                        .matchRequestId(matchRequest.getId())
                        .userId(matchRequest.getUserId())
                        .complexId(matchRequest.getComplexId())
                        .inputName(matchRequest.getInputName())
                        .inputPhone(matchRequest.getInputPhone())
                        .inputBirthDate(matchRequest.getInputBirthDate())
                        .inputBuilding(matchRequest.getInputBuilding())
                        .inputUnit(matchRequest.getInputUnit())
                        .matchedHouseholdId(matchRequest.getMatchedHouseholdId())
                        .expectedResidentRegistered(findRegisteredExpectedResident(matchRequest).isPresent())
                        .processType(matchRequest.getProcessType().getValue())
                        .matchStatus(matchRequest.getMatchStatus().getValue())
                        .processedAt(matchRequest.getProcessedAt())
                        .createdAt(matchRequest.getCreatedAt())
                        .build())
                .toList();

        return HouseholdMatchListRes.builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    // 관리자가 수동으로 세대 매칭 요청을 승인한다.
    public HouseholdMatchApproveRes approveMatchRequest(Long complexId, Long matchRequestId) {
        HouseholdMatchRequest matchRequest = getMatchRequestForComplex(complexId, matchRequestId);
        validatePendingMatchRequest(matchRequest);

        Household household = householdRepository.findByComplexIdAndBuildingAndUnit(
                        complexId,
                        matchRequest.getInputBuilding(),
                        matchRequest.getInputUnit())
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND));

        if (householdMemberRepository.existsByUserIdAndComplexId(matchRequest.getUserId(), complexId)) {
            throw new BusinessException(HouseholdErrorCode.ALREADY_HOUSEHOLD_MEMBER);
        }
        if (householdMemberRepository.existsByHouseholdIdAndUserId(household.getId(), matchRequest.getUserId())) {
            throw new BusinessException(HouseholdErrorCode.ALREADY_HOUSEHOLD_MEMBER);
        }

        ExpectedResident expectedResident = findMatchingExpectedResident(matchRequest, household)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.MATCH_EXPECTED_RESIDENT_REQUIRED));
        approvePendingMatchRequest(matchRequest, expectedResident, household);

        return HouseholdMatchApproveRes.builder()
                .matchRequestId(matchRequestId)
                .matchStatus(HouseholdMatchStatus.APPROVED.getCode())
                .processedAt(matchRequest.getProcessedAt())
                .build();
    }

    // 관리자가 정해진 거절 사유 코드로 세대 매칭 요청을 거절한다.
    public List<HouseholdMatchApproveRes> approveMatchRequests(Long complexId, HouseholdMatchBulkApproveReq request) {
        if (request == null || request.getMatchRequestIds() == null || request.getMatchRequestIds().isEmpty()) {
            throw new BusinessException(HouseholdErrorCode.MATCH_REQUEST_NOT_SELECTED);
        }
        return request.getMatchRequestIds().stream()
                .map(matchRequestId -> approveMatchRequest(complexId, matchRequestId))
                .toList();
    }

    public HouseholdMatchRejectRes rejectMatchRequest(Long complexId, Long matchRequestId, HouseholdMatchRejectReq request) {
        HouseholdMatchRequest matchRequest = getMatchRequestForComplex(complexId, matchRequestId);
        validatePendingMatchRequest(matchRequest);

        HouseholdMatchRejectReason reason = resolveRejectReason(request);
        matchRequest.reject(reason);
        matchRequestRepository.save(matchRequest);
        householdOutboxService.saveMatchRejectedEvent(buildMatchResultPayload(matchRequest, matchRequest.getMatchedHouseholdId()));

        return HouseholdMatchRejectRes.builder()
                .matchRequestId(matchRequestId)
                .matchStatus(HouseholdMatchStatus.REJECTED.getCode())
                .processedAt(matchRequest.getProcessedAt())
                .reason(reason.getCode())
                .build();
    }

    // 관리자 명부와 가입자 입력값이 일치하면 자동승인으로 처리한다.
    private HouseholdMatchRequest approveByExpectedResident(HouseholdMatchPostReq request, ExpectedResident expectedResident) {
        Household household = householdRepository.findById(expectedResident.getHouseholdId())
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND));

        HouseholdMatchRequest matchRequest = matchRequestRepository.save(HouseholdMatchRequest.builder()
                .userId(request.getUserId())
                .complexId(request.getComplexId())
                .inputName(request.getInputName())
                .inputPhone(request.getInputPhone())
                .inputBirthDate(request.getInputBirthDate())
                .inputBuilding(request.getInputBuilding())
                .inputUnit(request.getInputUnit())
                .matchedHouseholdId(household.getId())
                .matchedExpectedResidentId(expectedResident.getId())
                .processType(HouseholdMatchProcessType.AUTO)
                .matchStatus(HouseholdMatchStatus.APPROVED)
                .processedAt(LocalDateTime.now())
                .build());

        createMemberAndEvent(household, request.getUserId(), expectedResident.getHouseholdRole());
        expectedResident.markMatched(request.getUserId());
        expectedResidentRepository.save(expectedResident);
        householdOutboxService.saveMatchApprovedEvent(buildMatchResultPayload(matchRequest, household.getId()));

        return matchRequest;
    }

    // 관리자 명부 등록 시 일치하는 대기 요청을 자동 승인한다.
    private void approvePendingMatchRequest(
            HouseholdMatchRequest matchRequest,
            ExpectedResident expectedResident,
            Household household
    ) {
        if (matchRequest.getMatchStatus() != HouseholdMatchStatus.PENDING) {
            return;
        }
        if (expectedResident.getStatus() != ExpectedResidentStatus.AVAILABLE) {
            return;
        }
        if (householdMemberRepository.existsByUserIdAndComplexId(matchRequest.getUserId(), matchRequest.getComplexId())) {
            return;
        }
        createMemberAndEvent(household, matchRequest.getUserId(), expectedResident.getHouseholdRole());
        expectedResident.markMatched(matchRequest.getUserId());
        expectedResidentRepository.save(expectedResident);
        matchRequest.apply(household.getId(), expectedResident.getId(), HouseholdMatchProcessType.MANUAL, HouseholdMatchStatus.APPROVED);
        matchRequest.updateProcessedAt(LocalDateTime.now());
        matchRequestRepository.save(matchRequest);
        householdOutboxService.saveMatchApprovedEvent(buildMatchResultPayload(matchRequest, household.getId()));
    }

    // 관리자 명부 등록 시 이미 세대원으로 연결해둔 사용자는 회원가입 매칭 이벤트 수신 시 승인 완료로 동기화한다.
    private HouseholdMatchPostRes approveAlreadyActiveMember(HouseholdMatchPostReq request, HouseholdMember householdMember) {
        Optional<HouseholdMatchRequest> existingApproved = matchRequestRepository
                .findTopByUserIdAndComplexIdAndMatchStatusOrderByCreatedAtDesc(
                        request.getUserId(),
                        request.getComplexId(),
                        HouseholdMatchStatus.APPROVED
                );

        // 이미 APPROVED 매칭이 존재하면 재발행 없이 반환 — HOUSEHOLD_MATCH_REQUESTED 재수신 시 중복 알림 방지
        if (existingApproved.isPresent()) {
            HouseholdMatchRequest matchRequest = existingApproved.get();
            return HouseholdMatchPostRes.builder()
                    .matchRequestId(matchRequest.getId())
                    .matchedHouseholdId(householdMember.getHouseholdId())
                    .processType(matchRequest.getProcessType().getCode())
                    .matchStatus(matchRequest.getMatchStatus().getCode())
                    .createdAt(matchRequest.getCreatedAt())
                    .build();
        }

        HouseholdMatchRequest matchRequest = matchRequestRepository.save(HouseholdMatchRequest.builder()
                .userId(request.getUserId())
                .complexId(request.getComplexId())
                .inputName(request.getInputName())
                .inputPhone(request.getInputPhone())
                .inputBirthDate(request.getInputBirthDate())
                .inputBuilding(request.getInputBuilding())
                .inputUnit(request.getInputUnit())
                .matchedHouseholdId(householdMember.getHouseholdId())
                .processType(HouseholdMatchProcessType.AUTO)
                .matchStatus(HouseholdMatchStatus.APPROVED)
                .processedAt(LocalDateTime.now())
                .build());

        householdOutboxService.saveMatchApprovedEvent(buildMatchResultPayload(matchRequest, householdMember.getHouseholdId()));

        return HouseholdMatchPostRes.builder()
                .matchRequestId(matchRequest.getId())
                .matchedHouseholdId(householdMember.getHouseholdId())
                .processType(matchRequest.getProcessType().getCode())
                .matchStatus(matchRequest.getMatchStatus().getCode())
                .createdAt(matchRequest.getCreatedAt())
                .build();
    }

    private Page<HouseholdMatchRequest> findMatchRequests(
            Long complexId,
            HouseholdMatchStatus matchStatus,
            HouseholdMatchProcessType processType,
            Pageable pageable
    ) {
        if (matchStatus != null && processType != null) {
            return matchRequestRepository.findByComplexIdAndMatchStatusAndProcessType(
                    complexId,
                    matchStatus,
                    processType,
                    pageable
            );
        }
        if (matchStatus != null) {
            return matchRequestRepository.findByComplexIdAndMatchStatus(complexId, matchStatus, pageable);
        }
        if (processType != null) {
            return matchRequestRepository.findByComplexIdAndProcessType(complexId, processType, pageable);
        }
        return matchRequestRepository.findByComplexId(complexId, pageable);
    }

    // 자동승인 기준에 맞지 않으면 관리자 수동 승인 대기로 저장한다.
    private HouseholdMatchRequest createPendingMatchRequest(HouseholdMatchPostReq request) {
        return matchRequestRepository.save(HouseholdMatchRequest.builder()
                .userId(request.getUserId())
                .complexId(request.getComplexId())
                .inputName(request.getInputName())
                .inputPhone(request.getInputPhone())
                .inputBirthDate(request.getInputBirthDate())
                .inputBuilding(request.getInputBuilding())
                .inputUnit(request.getInputUnit())
                .processType(HouseholdMatchProcessType.MANUAL)
                .matchStatus(HouseholdMatchStatus.PENDING)
                .build());
    }

    // 관리자 명부에서 가입자 입력값과 일치하는 사용 가능한 row를 찾는다.
    private Optional<ExpectedResident> findMatchingExpectedResident(HouseholdMatchPostReq request) {
        String requestName = normalizeName(request.getInputName());
        String requestPhone = normalizePhone(request.getInputPhone());

        return expectedResidentRepository.findByComplexIdAndBuildingAndUnitAndStatus(
                        request.getComplexId(),
                        request.getInputBuilding(),
                        request.getInputUnit(),
                        ExpectedResidentStatus.AVAILABLE)
                .stream()
                .filter(expectedResident -> normalizeName(expectedResident.getName()).equals(requestName))
                .filter(expectedResident -> normalizePhone(expectedResident.getPhone()).equals(requestPhone))
                .filter(expectedResident -> expectedResident.getBirthDate().equals(request.getInputBirthDate()))
                .findFirst();
    }

    // 세대원 저장과 관련 이벤트 적재를 같은 트랜잭션 안에서 처리한다.
    private Optional<ExpectedResident> findMatchingExpectedResident(HouseholdMatchRequest matchRequest, Household household) {
        String requestName = normalizeName(matchRequest.getInputName());
        String requestPhone = normalizePhone(matchRequest.getInputPhone());

        return expectedResidentRepository.findByComplexIdAndBuildingAndUnitAndStatus(
                        matchRequest.getComplexId(),
                        household.getBuilding(),
                        household.getUnit(),
                        ExpectedResidentStatus.AVAILABLE)
                .stream()
                .filter(expectedResident -> normalizeName(expectedResident.getName()).equals(requestName))
                .filter(expectedResident -> normalizePhone(expectedResident.getPhone()).equals(requestPhone))
                .filter(expectedResident -> expectedResident.getBirthDate().equals(matchRequest.getInputBirthDate()))
                .findFirst();
    }

    private Optional<ExpectedResident> findRegisteredExpectedResident(HouseholdMatchRequest matchRequest) {
        String requestName = normalizeName(matchRequest.getInputName());
        String requestPhone = normalizePhone(matchRequest.getInputPhone());

        return expectedResidentRepository.findByComplexIdAndBuildingAndUnitAndStatusNot(
                        matchRequest.getComplexId(),
                        matchRequest.getInputBuilding(),
                        matchRequest.getInputUnit(),
                        ExpectedResidentStatus.DISABLED)
                .stream()
                .filter(expectedResident -> normalizeName(expectedResident.getName()).equals(requestName))
                .filter(expectedResident -> normalizePhone(expectedResident.getPhone()).equals(requestPhone))
                .filter(expectedResident -> expectedResident.getBirthDate().equals(matchRequest.getInputBirthDate()))
                .findFirst();
    }

    private void createMemberAndEvent(Household household, Long userId, HouseholdMemberRole role) {
        HouseholdMemberRole resolvedRole = role == null ? HouseholdMemberRole.MEMBER : role;
        HouseholdMember householdMember = householdMemberRepository.save(HouseholdMember.builder()
                .householdId(household.getId())
                .userId(userId)
                .role(resolvedRole)
                .isActive(true)
                .build());

        householdOutboxService.saveHouseholdMemberCreatedEvent(householdMember);

        if (resolvedRole == HouseholdMemberRole.HEAD) {
            household.changeHeadUserId(userId);
        }
        if (household.getStatus() == HouseholdStatus.VACANT) {
            household.changeStatus(HouseholdStatus.OCCUPIED);
        }
        householdOutboxService.saveHouseholdUpdatedEvent(householdRepository.save(household));
    }

    private HouseholdMatchRequest getMatchRequestForComplex(Long complexId, Long matchRequestId) {
        HouseholdMatchRequest matchRequest = matchRequestRepository.findById(matchRequestId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_MATCH_FAILED));
        if (!matchRequest.getComplexId().equals(complexId)) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_MATCH_FAILED);
        }
        return matchRequest;
    }

    private void validateMatchRequest(HouseholdMatchPostReq request) {
        if (request == null
                || request.getUserId() == null
                || request.getComplexId() == null
                || isBlank(request.getInputName())
                || isBlank(request.getInputPhone())
                || request.getInputBirthDate() == null
                || isBlank(request.getInputBuilding())
                || isBlank(request.getInputUnit())) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_MATCH_FAILED);
        }
    }

    private void validateDuplicateMatchRequest(HouseholdMatchPostReq request) {
        if (householdMemberRepository.existsByUserIdAndComplexId(request.getUserId(), request.getComplexId())) {
            throw new BusinessException(HouseholdErrorCode.ALREADY_HOUSEHOLD_MEMBER);
        }
        if (matchRequestRepository.existsByUserIdAndComplexIdAndMatchStatusIn(
                request.getUserId(),
                request.getComplexId(),
                List.of(HouseholdMatchStatus.PENDING, HouseholdMatchStatus.APPROVED))) {
            throw new BusinessException(HouseholdErrorCode.DUPLICATE_MATCH_REQUEST);
        }
    }

    private void validatePendingMatchRequest(HouseholdMatchRequest matchRequest) {
        if (matchRequest.getMatchStatus() != HouseholdMatchStatus.PENDING) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_MATCH_FAILED);
        }
    }

    private HouseholdMatchRejectReason resolveRejectReason(HouseholdMatchRejectReq request) {
        if (request == null || isBlank(request.getReason())) {
            return HouseholdMatchRejectReason.ADMIN_REJECTED;
        }
        String reason = request.getReason().trim();
        return Arrays.stream(HouseholdMatchRejectReason.values())
                .filter(value -> matchesEnumCode(value, reason))
                .findFirst()
                .orElseThrow(() -> new BusinessException(CommonErrorCode.INVALID_PARAMETER));
    }

    private boolean matchesEnumCode(Enum<?> value, String input) {
        if (value.name().equalsIgnoreCase(input)) {
            return true;
        }
        EnumMapperType mapper = (EnumMapperType) value;
        return mapper.getCode().equalsIgnoreCase(input) || mapper.getValue().equals(input);
    }

    private HouseholdMatchResultEventPayload buildMatchResultPayload(HouseholdMatchRequest matchRequest, Long householdId) {
        return HouseholdMatchResultEventPayload.builder()
                .matchRequestId(matchRequest.getId())
                .userId(matchRequest.getUserId())
                .complexId(matchRequest.getComplexId())
                .matchStatus(matchRequest.getMatchStatus().name())
                .householdId(householdId)
                .build();
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
}
