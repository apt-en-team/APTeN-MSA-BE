package com.apten.household.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.household.application.model.request.HouseholdBulkCreateReq;
import com.apten.household.application.model.request.HouseholdCreateReq;
import com.apten.household.application.model.request.HouseholdHeadPatchReq;
import com.apten.household.application.model.request.HouseholdListReq;
import com.apten.household.application.model.request.HouseholdMemberPatchReq;
import com.apten.household.application.model.request.HouseholdMemberPostReq;
import com.apten.household.application.model.request.HouseholdPatchReq;
import com.apten.household.application.model.request.HouseholdStatusPatchReq;
import com.apten.household.application.model.response.HouseholdBulkCreateRes;
import com.apten.household.application.model.response.HouseholdCreateRes;
import com.apten.household.application.model.response.HouseholdDetailRes;
import com.apten.household.application.model.response.HouseholdHeadPatchRes;
import com.apten.household.application.model.response.HouseholdHistoryRes;
import com.apten.household.application.model.response.HouseholdListRes;
import com.apten.household.application.model.response.HouseholdPatchRes;
import com.apten.household.application.model.response.HouseholdMemberDeleteRes;
import com.apten.household.application.model.response.HouseholdMemberListRes;
import com.apten.household.application.model.response.HouseholdMemberPatchRes;
import com.apten.household.application.model.response.HouseholdMemberPostRes;
import com.apten.household.application.model.response.HouseholdMemberRepublishRes;
import com.apten.household.application.model.response.HouseholdStatusPatchRes;
import com.apten.household.application.model.response.MyHouseholdRes;
import com.apten.household.domain.entity.ComplexCache;
import com.apten.household.domain.entity.ExpectedResident;
import com.apten.household.domain.entity.Household;
import com.apten.household.domain.entity.HouseholdHistory;
import com.apten.household.domain.entity.HouseholdMember;
import com.apten.household.domain.entity.HouseholdType;
import com.apten.household.domain.entity.UserCache;
import com.apten.household.domain.enums.ComplexCacheStatus;
import com.apten.household.domain.enums.ExpectedResidentStatus;
import com.apten.household.domain.enums.HouseholdMemberRole;
import com.apten.household.domain.enums.HouseholdStatus;
import com.apten.household.domain.repository.ComplexCacheRepository;
import com.apten.household.domain.repository.ExpectedResidentRepository;
import com.apten.household.domain.repository.HouseholdHistoryRepository;
import com.apten.household.domain.repository.HouseholdMemberCountProjection;
import com.apten.household.domain.repository.HouseholdMemberRepository;
import com.apten.household.domain.repository.HouseholdRepository;
import com.apten.household.domain.repository.HouseholdTypeRepository;
import com.apten.household.domain.repository.UserCacheRepository;
import com.apten.household.exception.HouseholdErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 세대와 세대원 도메인 API 시그니처를 모아두는 서비스이다.
@Service
@Transactional
@RequiredArgsConstructor
public class HouseholdService {

    // 세대 저장소이다.
    private final HouseholdRepository householdRepository;

    // 단지 캐시 저장소이다.
    private final ComplexCacheRepository complexCacheRepository;

    // 세대원 저장소이다.
    private final HouseholdMemberRepository householdMemberRepository;

    private final HouseholdHistoryRepository householdHistoryRepository;

    private final ExpectedResidentRepository expectedResidentRepository;

    // 사용자 캐시 저장소이다.
    private final UserCacheRepository userCacheRepository;

    // Outbox 적재 전용 서비스이다.
    private final com.apten.household.infrastructure.kafka.HouseholdOutboxService householdOutboxService;

    // 동/호 라인 기준 평형을 해석하는 서비스이다.
    private final HouseholdTypeService householdTypeService;

    private final HouseholdTypeRepository householdTypeRepository;

    // 세대 마스터 등록 서비스이다.
    public HouseholdCreateRes createHousehold(Long complexId, HouseholdCreateReq request) {
        // Gateway Header에서 해석한 complexId만 신뢰하고, 요청 본문의 complexId는 사용하지 않는다.
        validateActiveComplex(complexId);
        if (householdRepository.existsByComplexIdAndBuildingAndUnit(complexId, request.getBuilding(), request.getUnit())) {
            throw new BusinessException(HouseholdErrorCode.DUPLICATE_HOUSEHOLD);
        }

        Household household = householdRepository.save(Household.builder()
                .complexId(complexId)
                .building(request.getBuilding())
                .unit(request.getUnit())
                .typeId(resolveCreateTypeId(complexId, request))
                .status(HouseholdStatus.VACANT)
                .headUserId(null)
                .build());

        // 세대 생성과 같은 트랜잭션 안에서 생성 이벤트를 outbox에 적재한다.
        householdOutboxService.saveHouseholdCreatedEvent(household);
        saveHouseholdHistory(household, "세대 등록");

        return HouseholdCreateRes.builder()
                .householdId(household.getId())
                .complexId(household.getComplexId())
                .building(household.getBuilding())
                .unit(household.getUnit())
                .status(household.getStatus())
                .createdAt(household.getCreatedAt())
                .build();
    }

    public HouseholdBulkCreateRes createHouseholdsBulk(Long complexId, HouseholdBulkCreateReq request) {
        validateBulkCreateRequest(request);
        validateActiveComplex(complexId);

        String building = request.getBuilding().trim();
        int floorStart = request.getFloorStart() == null ? 1 : request.getFloorStart();
        int floorEnd = request.getFloorEnd();
        int lineStart = request.getLineStart();
        int lineEnd = request.getLineEnd();
        Long typeId = request.getTypeId();

        List<Household> households = new ArrayList<>();
        List<String> createdUnits = new ArrayList<>();
        List<String> skippedUnits = new ArrayList<>();

        for (int floor = floorStart; floor <= floorEnd; floor++) {
            for (int line = lineStart; line <= lineEnd; line++) {
                String unit = String.valueOf(floor * 100 + line);
                if (householdRepository.existsByComplexIdAndBuildingAndUnit(complexId, building, unit)) {
                    skippedUnits.add(unit);
                    continue;
                }

                households.add(Household.builder()
                        .complexId(complexId)
                        .building(building)
                        .unit(unit)
                        .typeId(typeId)
                        .status(HouseholdStatus.VACANT)
                        .headUserId(null)
                        .build());
                createdUnits.add(unit);
            }
        }

        List<Household> savedHouseholds = householdRepository.saveAll(households);
        savedHouseholds.forEach(householdOutboxService::saveHouseholdCreatedEvent);
        savedHouseholds.forEach(household -> saveHouseholdHistory(household, "세대 일괄 등록"));

        return HouseholdBulkCreateRes.builder()
                .complexId(complexId)
                .building(building)
                .floorStart(floorStart)
                .floorEnd(floorEnd)
                .lineStart(lineStart)
                .lineEnd(lineEnd)
                .typeId(typeId)
                .createdCount(createdUnits.size())
                .skippedCount(skippedUnits.size())
                .createdUnits(createdUnits)
                .skippedUnits(skippedUnits)
                .build();
    }

    // 세대 목록 조회 서비스이다.
    public HouseholdListRes getHouseholdList(Long complexId, HouseholdListReq request) {
        int pageNumber = request.getPage() != null ? request.getPage() : 0;
        int pageSize = request.getSize() != null ? request.getSize() : 20;
        String building = blankToNull(request.getBuilding());
        String unit = blankToNull(request.getUnit());
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        Page<Household> page = request.getStatus() == null
                ? householdRepository.findByFilters(complexId, building, unit, pageable)
                : householdRepository.findByFiltersAndStatus(complexId, building, unit, request.getStatus(), pageable);
        List<Household> households = page.getContent();
        List<Long> householdIds = households.stream()
                .map(Household::getId)
                .toList();

        // 세대 목록의 부가 정보는 세대별 반복 조회 대신 한 번에 조회해 N+1을 방지한다.
        Map<Long, List<ExpectedResident>> expectedResidentsByHouseholdId = householdIds.isEmpty()
                ? Map.of()
                : expectedResidentRepository.findByHouseholdIdInAndStatusNot(householdIds, ExpectedResidentStatus.DISABLED)
                .stream()
                .collect(Collectors.groupingBy(ExpectedResident::getHouseholdId));

        Map<Long, Long> activeMemberCountByHouseholdId = householdIds.isEmpty()
                ? Map.of()
                : householdMemberRepository.countActiveMembersByHouseholdIds(householdIds)
                .stream()
                .collect(Collectors.toMap(
                        HouseholdMemberCountProjection::getHouseholdId,
                        HouseholdMemberCountProjection::getMemberCount
                ));

        Map<Long, UserCache> userCacheMap = userCacheRepository.findAllById(
                        households.stream()
                                .map(Household::getHeadUserId)
                                .filter(java.util.Objects::nonNull)
                                .distinct()
                                .toList()
                )
                .stream()
                .collect(Collectors.toMap(UserCache::getId, Function.identity()));

        Map<Long, String> typeNameMap = householdTypeRepository.findAllById(
                        households.stream()
                                .map(Household::getTypeId)
                                .filter(java.util.Objects::nonNull)
                                .distinct()
                                .toList()
                )
                .stream()
                .filter(type -> Boolean.TRUE.equals(type.getIsActive()))
                .collect(Collectors.toMap(HouseholdType::getId, HouseholdType::getTypeName));

        return HouseholdListRes.builder()
                .content(households.stream()
                        .map(household -> toHouseholdListItem(
                                household,
                                expectedResidentsByHouseholdId.getOrDefault(household.getId(), List.of()),
                                activeMemberCountByHouseholdId.getOrDefault(household.getId(), 0L),
                                userCacheMap,
                                typeNameMap
                        ))
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .summary(buildHouseholdSummary(complexId))
                .build();
    }

    // 세대 상세 조회 서비스이다.
    public HouseholdDetailRes getHouseholdDetail(Long complexId, Long householdId) {
        Household household = getHouseholdForComplex(complexId, householdId);
        // 세대 기본 정보와 등록입주민 명부 기준 요약 정보를 조회한다.
        List<ExpectedResident> expectedResidents = expectedResidentRepository.findByHouseholdIdAndStatusNot(
                household.getId(),
                ExpectedResidentStatus.DISABLED
        );
        String headName = household.getHeadUserId() == null
                ? null
                : userCacheRepository.findById(household.getHeadUserId()).map(UserCache::getName).orElse(null);

        return HouseholdDetailRes.builder()
                .householdId(household.getId())
                .complexId(household.getComplexId())
                .building(household.getBuilding())
                .unit(household.getUnit())
                .typeId(household.getTypeId())
                .typeName(resolveTypeName(household.getTypeId()))
                .status(household.getStatus())
                .headName(headName)
                .memberCount(expectedResidents.isEmpty()
                        ? householdMemberRepository.countByHouseholdIdAndIsActiveTrue(household.getId())
                        : expectedResidents.size())
                .expectedResidentCount((long) expectedResidents.size())
                .carCount(0L)
                .moveInDate(expectedResidents.stream()
                        .map(ExpectedResident::getMoveInDate)
                        .filter(java.util.Objects::nonNull)
                        .min(LocalDate::compareTo)
                        .orElse(null))
                .createdAt(household.getCreatedAt())
                .updatedAt(household.getUpdatedAt())
                .build();
    }

    // 세대 정보 수정 서비스이다.
    public HouseholdPatchRes updateHousehold(Long complexId, Long householdId, HouseholdPatchReq request) {
        validateActiveComplex(complexId);
        Household household = getHouseholdForComplex(complexId, householdId);
        String nextBuilding = request.getBuilding() != null ? request.getBuilding() : household.getBuilding();
        String nextUnit = request.getUnit() != null ? request.getUnit() : household.getUnit();
        Long nextTypeId = request.getTypeId() != null ? request.getTypeId() : household.getTypeId();

        if (householdRepository.existsByComplexIdAndBuildingAndUnitAndIdNot(
                complexId,
                nextBuilding,
                nextUnit,
                household.getId()
        )) {
            throw new BusinessException(HouseholdErrorCode.DUPLICATE_HOUSEHOLD);
        }

        // 요청 본문의 단지 식별자 값이 있더라도 Header에서 해석한 complexId를 우선 사용한다.
        household.update(
                complexId,
                nextBuilding,
                nextUnit,
                nextTypeId
        );
        Household savedHousehold = householdRepository.save(household);

        // 세대 기본 정보 수정과 같은 트랜잭션 안에서 수정 이벤트를 outbox에 적재한다.
        householdOutboxService.saveHouseholdUpdatedEvent(savedHousehold);
        saveHouseholdHistory(savedHousehold, "세대 정보 수정");

        return HouseholdPatchRes.builder()
                .householdId(savedHousehold.getId())
                .building(savedHousehold.getBuilding())
                .unit(savedHousehold.getUnit())
                .typeId(savedHousehold.getTypeId())
                .status(savedHousehold.getStatus())
                .updatedAt(savedHousehold.getUpdatedAt())
                .build();
    }

    // 세대 상태 변경 서비스이다.
    public HouseholdStatusPatchRes changeHouseholdStatus(Long complexId, Long householdId, HouseholdStatusPatchReq request) {
        validateActiveComplex(complexId);
        Household household = getHouseholdForComplex(complexId, householdId);

        if (request.getStatus() == null) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_STATUS_INVALID);
        }
        HouseholdStatus fromStatus = household.getStatus();
        household.changeStatus(request.getStatus());
        if (request.getStatus() == HouseholdStatus.MOVED_OUT) {
            applyMoveOutCascade(household);
        }
        Household savedHousehold = householdRepository.save(household);

        saveHouseholdHistory(
                savedHousehold,
                fromStatus,
                savedHousehold.getStatus(),
                request.getReason() == null || request.getReason().isBlank()
                        ? "세대 상태 변경"
                        : request.getReason()
        );

        // 세대 상태 변경도 같은 트랜잭션 안에서 outbox에 적재한다.
        if (request.getStatus() == HouseholdStatus.MOVED_OUT) {
            householdOutboxService.saveHouseholdDeactivatedEvent(savedHousehold);
        } else {
            householdOutboxService.saveHouseholdUpdatedEvent(savedHousehold);
        }

        return HouseholdStatusPatchRes.builder()
                .householdId(savedHousehold.getId())
                .status(savedHousehold.getStatus())
                .changedAt(savedHousehold.getUpdatedAt())
                .build();
    }

    // 입주와 퇴거 이력 조회 서비스이다.
    public List<HouseholdHistoryRes> getHouseholdHistory(Long complexId, Long householdId) {
        getHouseholdForComplex(complexId, householdId);
        return householdHistoryRepository.findByHouseholdIdOrderByChangedAtDesc(householdId).stream()
                .map(history -> HouseholdHistoryRes.builder()
                        .historyId(history.getId())
                        .fromStatus(history.getFromStatus() == null ? null : history.getFromStatus().name())
                        .toStatus(history.getToStatus().name())
                        .reason(history.getReason())
                        .changedAt(history.getChangedAt())
                        .build())
                .toList();
    }

    // 세대원 등록 서비스이다.
    public HouseholdMemberPostRes addHouseholdMember(Long complexId, Long householdId, HouseholdMemberPostReq request) {
        Household household = getHouseholdForComplex(complexId, householdId);
        UserCache userCache = getUserCacheOrThrow(request.getUserId());
        HouseholdMemberRole role = request.getRole() != null ? request.getRole() : HouseholdMemberRole.MEMBER;

        // 세대와 사용자 조합 중복 등록을 막는다.
        if (householdMemberRepository.existsByHouseholdIdAndUserId(householdId, request.getUserId())) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_ALREADY_LINKED);
        }

        // 활성 세대주는 한 명만 유지한다.
        validateHeadDuplication(householdId, role, null);

        HouseholdMember householdMember = householdMemberRepository.save(
                HouseholdMember.builder()
                        .householdId(household.getId())
                        .userId(userCache.getId())
                        .role(role)
                        .isActive(true)
                        .build()
        );

        // 세대원 저장과 같은 트랜잭션 안에서 생성 이벤트를 outbox에 적재한다.
        householdOutboxService.saveHouseholdMemberCreatedEvent(householdMember);
        saveHouseholdHistory(household, "세대원 추가: " + userCache.getName());

        return HouseholdMemberPostRes.builder()
                .householdMemberId(householdMember.getId())
                .householdId(householdId)
                .userId(householdMember.getUserId())
                .role(householdMember.getRole())
                .isActive(householdMember.getIsActive())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 세대원 조회 서비스이다.
    public List<HouseholdMemberListRes> getHouseholdMembers(Long complexId, Long householdId) {
        getHouseholdForComplex(complexId, householdId);
        List<HouseholdMember> members = householdMemberRepository.findByHouseholdId(householdId);
        Map<Long, UserCache> userCacheMap = userCacheRepository.findAllById(
                        members.stream().map(HouseholdMember::getUserId).collect(Collectors.toSet())
                )
                .stream()
                .collect(Collectors.toMap(UserCache::getId, Function.identity()));

        return members.stream()
                .map(member -> {
                    UserCache userCache = userCacheMap.get(member.getUserId());
                    return HouseholdMemberListRes.builder()
                            .householdMemberId(member.getId())
                            .userId(member.getUserId())
                            .role(member.getRole())
                            .name(userCache == null ? null : userCache.getName())
                            .phone(userCache == null ? null : userCache.getPhone())
                            .birthDate(userCache == null ? null : userCache.getBirthDate())
                            .isActive(member.getIsActive())
                            .createdAt(member.getCreatedAt())
                            .updatedAt(member.getUpdatedAt())
                            .build();
                })
                .toList();
    }

    // 전 세대원 이벤트 재발행 서비스이다.
    // parking-vehicle-service의 household_member_cache 초기 백필을 위해 관리자가 1회 호출한다.
    public HouseholdMemberRepublishRes republishAllHouseholdMembers() {
        // 전 세대원을 한 번에 조회한다. 데이터 규모가 커지면 페이징 기반 재발행으로 전환이 필요하다.
        List<HouseholdMember> members = householdMemberRepository.findAll();

        // 각 세대원을 기존 생성 이벤트로 outbox에 적재한다. consumer가 upsert라 created/updated 구분이 불필요하다.
        members.forEach(householdOutboxService::saveHouseholdMemberCreatedEvent);

        return HouseholdMemberRepublishRes.builder()
                .republishedCount(members.size())
                .message("전 세대원 이벤트 재발행 완료")
                .build();
    }

    // 세대원 수정 서비스이다.
    public HouseholdMemberPatchRes updateHouseholdMember(Long complexId, Long householdMemberId, HouseholdMemberPatchReq request) {
        HouseholdMember householdMember = getHouseholdMemberForComplex(complexId, householdMemberId);
        HouseholdMemberRole nextRole = request.getRole() != null ? request.getRole() : householdMember.getRole();
        Boolean nextIsActive = request.getIsActive() != null ? request.getIsActive() : householdMember.getIsActive();

        validateHeadRemoval(householdMember, nextRole, nextIsActive);
        validateHeadDuplication(householdMember.getHouseholdId(), nextRole, householdMember.getId());

        householdMember.update(nextRole, nextIsActive);
        HouseholdMember savedHouseholdMember = householdMemberRepository.save(householdMember);

        // 세대원 수정과 같은 트랜잭션 안에서 수정 이벤트를 outbox에 적재한다.
        householdOutboxService.saveHouseholdMemberUpdatedEvent(savedHouseholdMember);
        saveHouseholdHistory(
                getHouseholdForComplex(complexId, savedHouseholdMember.getHouseholdId()),
                "세대원 정보 수정"
        );

        return HouseholdMemberPatchRes.builder()
                .householdMemberId(savedHouseholdMember.getId())
                .role(savedHouseholdMember.getRole())
                .isActive(savedHouseholdMember.getIsActive())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 세대원 삭제 서비스이다.
    public HouseholdMemberDeleteRes deleteHouseholdMember(Long complexId, Long householdMemberId) {
        HouseholdMember householdMember = getHouseholdMemberForComplex(complexId, householdMemberId);

        validateHeadRemoval(householdMember, householdMember.getRole(), false);

        // 물리 삭제 대신 비활성 처리 후 removed 이벤트를 적재한다.
        householdMember.changeActive(false);
        HouseholdMember savedHouseholdMember = householdMemberRepository.save(householdMember);

        // 세대원 비활성 처리와 같은 트랜잭션 안에서 제거 이벤트를 outbox에 적재한다.
        householdOutboxService.saveHouseholdMemberRemovedEvent(savedHouseholdMember);
        saveHouseholdHistory(
                getHouseholdForComplex(complexId, savedHouseholdMember.getHouseholdId()),
                "세대원 삭제"
        );

        return HouseholdMemberDeleteRes.builder()
                .householdMemberId(savedHouseholdMember.getId())
                .message("세대원 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // 세대주 권한 변경 서비스이다.
    public HouseholdHeadPatchRes changeHouseholdHead(Long complexId, Long householdId, HouseholdHeadPatchReq request) {
        Household household = getHouseholdForComplex(complexId, householdId);

        HouseholdMember newHead = householdMemberRepository.findByHouseholdIdAndUserIdAndIsActiveTrue(householdId, request.getUserId())
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_MEMBER_NOT_FOUND));

        HouseholdMember currentHead = householdMemberRepository.findByHouseholdIdAndRoleAndIsActiveTrue(householdId, HouseholdMemberRole.HEAD)
                .orElse(null);

        if (currentHead != null && !currentHead.getId().equals(newHead.getId())) {
            currentHead.update(HouseholdMemberRole.MEMBER, true);
            HouseholdMember savedCurrentHead = householdMemberRepository.save(currentHead);
            // 기존 세대주의 역할 변경도 별도 이벤트로 반영한다.
            householdOutboxService.saveHouseholdHeadChangedEvent(savedCurrentHead);
        }

        newHead.update(HouseholdMemberRole.HEAD, true);
        HouseholdMember savedNewHead = householdMemberRepository.save(newHead);
        // 신규 세대주의 역할 변경도 별도 이벤트로 반영한다.
        householdOutboxService.saveHouseholdHeadChangedEvent(savedNewHead);

        // 세대주가 바뀌면 세대 캐시가 참조하는 headUserId도 함께 갱신한다.
        household.changeHeadUserId(savedNewHead.getUserId());
        Household savedHousehold = householdRepository.save(household);
        householdOutboxService.saveHouseholdUpdatedEvent(savedHousehold);
        saveHouseholdHistory(savedHousehold, "세대주 변경");

        return HouseholdHeadPatchRes.builder()
                .householdId(householdId)
                .headUserId(savedNewHead.getUserId())
                .updatedAt(savedHousehold.getUpdatedAt())
                .build();
    }

    // 내 세대 정보 조회 서비스이다.
    public MyHouseholdRes getMyHousehold(Long userId, Long complexId) {
        HouseholdMember myMember = householdMemberRepository.findActiveByUserIdAndComplexId(userId, complexId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_MEMBER_NOT_FOUND));
        Household household = getHouseholdForComplex(complexId, myMember.getHouseholdId());

        List<HouseholdMember> members = householdMemberRepository.findByHouseholdId(household.getId());
        Map<Long, UserCache> userCacheMap = userCacheRepository.findAllById(
                        members.stream()
                                .map(HouseholdMember::getUserId)
                                .distinct()
                                .toList()
                )
                .stream()
                .collect(Collectors.toMap(UserCache::getId, Function.identity()));

        return MyHouseholdRes.builder()
                .householdId(household.getId())
                .complexId(household.getComplexId())
                .building(household.getBuilding())
                .unit(household.getUnit())
                .status(household.getStatus())
                .members(members.stream()
                        .map(member -> {
                            UserCache userCache = userCacheMap.get(member.getUserId());
                            return MyHouseholdRes.MemberItem.builder()
                                    .userId(member.getUserId())
                                    .name(userCache != null ? userCache.getName() : null)
                                    .phone(userCache != null ? userCache.getPhone() : null)
                                    .role(member.getRole())
                                    .isActive(member.getIsActive())
                                    .build();
                        })
                        .toList())
                .build();
    }

    // 세대 삭제 서비스이다.
    public void deleteHousehold(Long complexId, Long householdId) {
        Household household = getHouseholdForComplex(complexId, householdId);
        if (householdMemberRepository.countByHouseholdIdAndIsActiveTrue(householdId) > 0) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_HAS_MEMBER);
        }
        if (!expectedResidentRepository.findByHouseholdId(householdId).isEmpty()) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_HAS_EXPECTED_RESIDENT);
        }
        householdRepository.delete(household);
    }

    private HouseholdListRes.Item toHouseholdListItem(
            Household household,
            List<ExpectedResident> expectedResidents,
            long activeMemberCount,
            Map<Long, UserCache> userCacheMap,
            Map<Long, String> typeNameMap
    ) {
        String headName = null;
        if (household.getHeadUserId() != null) {
            UserCache userCache = userCacheMap.get(household.getHeadUserId());
            headName = userCache == null ? null : userCache.getName();
        }
        if (headName == null) {
            headName = expectedResidents.stream()
                    .filter(expectedResident -> expectedResident.getHouseholdRole() == HouseholdMemberRole.HEAD)
                    .map(ExpectedResident::getName)
                    .findFirst()
                    .orElse(null);
        }
        long memberCount = expectedResidents.isEmpty()
                ? activeMemberCount
                : expectedResidents.size();

        return HouseholdListRes.Item.builder()
                .householdId(household.getId())
                .complexId(household.getComplexId())
                .building(household.getBuilding())
                .unit(household.getUnit())
                .typeId(household.getTypeId())
                .typeName(typeNameMap.get(household.getTypeId()))
                .status(household.getStatus())
                .headName(headName)
                .memberCount(memberCount)
                .carCount(0L)
                .createdAt(household.getCreatedAt())
                .build();
    }

    // 상단 통계 카드를 단지 전체 기준으로 집계한다.
    private HouseholdListRes.Summary buildHouseholdSummary(Long complexId) {
        LocalDate now = LocalDate.now();
        LocalDate from = now.withDayOfMonth(1);
        LocalDate to = now.plusMonths(1).withDayOfMonth(1);

        return HouseholdListRes.Summary.builder()
                .totalHouseholds(householdRepository.countByComplexId(complexId))
                .occupiedHouseholds(householdRepository.countByComplexIdAndStatus(complexId, HouseholdStatus.OCCUPIED))
                .vacantHouseholds(householdRepository.countByComplexIdAndStatus(complexId, HouseholdStatus.VACANT))
                .currentMonthMoveIns(expectedResidentRepository.countDistinctHouseholdsByMoveInDateBetween(
                        complexId,
                        ExpectedResidentStatus.DISABLED,
                        from,
                        to
                ))
                .build();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    // 단지 캐시 기준으로 존재 여부와 활성 상태를 검증한다.
    private void validateActiveComplex(Long complexId) {
        if (complexId == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        ComplexCache complexCache = complexCacheRepository.findById(complexId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.COMPLEX_NOT_FOUND));
        if (complexCache.getStatus() != ComplexCacheStatus.ACTIVE) {
            throw new BusinessException(HouseholdErrorCode.COMPLEX_NOT_FOUND);
        }
    }

    // 퇴거 처리 시 활성 세대원과 등록입주민 명부를 비활성화한다.
    private void applyMoveOutCascade(Household household) {
        household.changeHeadUserId(null);

        List<HouseholdMember> activeMembers = householdMemberRepository.findByHouseholdId(household.getId()).stream()
                .filter(member -> Boolean.TRUE.equals(member.getIsActive()))
                .toList();
        activeMembers.forEach(member -> member.changeActive(false));
        householdMemberRepository.saveAll(activeMembers);
        activeMembers.forEach(householdOutboxService::saveHouseholdMemberRemovedEvent);

        List<ExpectedResident> activeExpectedResidents = expectedResidentRepository.findByHouseholdIdAndStatusNot(
                household.getId(),
                ExpectedResidentStatus.DISABLED
        );
        activeExpectedResidents.forEach(ExpectedResident::disable);
        expectedResidentRepository.saveAll(activeExpectedResidents);
    }

    private String resolveTypeName(Long typeId) {
        if (typeId == null) {
            return null;
        }
        return householdTypeRepository.findById(typeId)
                .map(type -> Boolean.TRUE.equals(type.getIsActive()) ? type.getTypeName() : null)
                .orElse(null);
    }

    private void validateBulkCreateRequest(HouseholdBulkCreateReq request) {
        if (request == null
                || request.getBuilding() == null
                || request.getBuilding().isBlank()
                || request.getFloorEnd() == null
                || request.getLineStart() == null
                || request.getLineEnd() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        int floorStart = request.getFloorStart() == null ? 1 : request.getFloorStart();
        int floorEnd = request.getFloorEnd();
        int lineStart = request.getLineStart();
        int lineEnd = request.getLineEnd();

        if (floorStart < 1 || floorEnd < 1 || floorStart > floorEnd
                || lineStart < 1 || lineEnd < 1 || lineStart > lineEnd || lineEnd > 99) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private Household getHouseholdOrThrow(Long householdId) {
        return householdRepository.findById(householdId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND));
    }

    // 세대가 현재 단지 소속인지 확인하고 없으면 예외를 던진다.
    private Household getHouseholdForComplex(Long complexId, Long householdId) {
        return householdRepository.findByIdAndComplexId(householdId, complexId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_NOT_FOUND));
    }

    // 사용자 캐시 존재 여부를 확인하고 없으면 예외를 던진다.
    private UserCache getUserCacheOrThrow(Long userId) {
        return userCacheRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.USER_NOT_FOUND));
    }

    private void saveHouseholdHistory(Household household, String reason) {
        saveHouseholdHistory(household, household.getStatus(), household.getStatus(), reason);
    }

    private void saveHouseholdHistory(
            Household household,
            HouseholdStatus fromStatus,
            HouseholdStatus toStatus,
            String reason
    ) {
        householdHistoryRepository.save(HouseholdHistory.builder()
                .householdId(household.getId())
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .reason(reason)
                .changedAt(LocalDateTime.now())
                .build());
    }

    // 세대원 존재 여부를 확인하고 없으면 예외를 던진다.
    private HouseholdMember getHouseholdMemberOrThrow(Long householdMemberId) {
        return householdMemberRepository.findById(householdMemberId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_MEMBER_NOT_FOUND));
    }

    // 세대원이 현재 단지 소속 세대에 속하는지 확인하고 없으면 예외를 던진다.
    private HouseholdMember getHouseholdMemberForComplex(Long complexId, Long householdMemberId) {
        return householdMemberRepository.findByIdAndComplexId(householdMemberId, complexId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_MEMBER_NOT_FOUND));
    }

    // 활성 세대주 중복을 막는다.
    private void validateHeadDuplication(Long householdId, HouseholdMemberRole nextRole, Long currentMemberId) {
        if (nextRole != HouseholdMemberRole.HEAD) {
            return;
        }

        householdMemberRepository.findByHouseholdIdAndRoleAndIsActiveTrue(householdId, HouseholdMemberRole.HEAD)
                .filter(existingHead -> currentMemberId == null || !existingHead.getId().equals(currentMemberId))
                .ifPresent(existingHead -> {
                    throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_HEAD_DUPLICATED);
                });
    }

    // 세대주 제거로 인해 세대주가 공백이 되는 상황을 막는다.
    private void validateHeadRemoval(HouseholdMember householdMember, HouseholdMemberRole nextRole, Boolean nextIsActive) {
        if (householdMember.getRole() != HouseholdMemberRole.HEAD) {
            return;
        }

        boolean roleChanged = nextRole != HouseholdMemberRole.HEAD;
        boolean deactivated = !Boolean.TRUE.equals(nextIsActive);

        if (roleChanged || deactivated) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_HEAD_REQUIRED);
        }
    }

    // 요청에 평형이 없으면 동/호 라인 설정으로 평형을 해석한다.
    private Long resolveCreateTypeId(Long complexId, HouseholdCreateReq request) {
        if (request.getTypeId() != null) {
            return request.getTypeId();
        }
        return householdTypeService.resolveTypeIdOrNull(complexId, request.getBuilding(), request.getUnit());
    }
}
