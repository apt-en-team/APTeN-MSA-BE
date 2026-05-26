package com.apten.household.application.service;

import com.apten.common.exception.BusinessException;
import com.apten.common.exception.CommonErrorCode;
import com.apten.common.security.UserRole;
import com.apten.household.application.model.dto.HouseholdRequestContext;
import com.apten.household.application.model.request.BuildingLineTypeCreateReq;
import com.apten.household.application.model.request.BuildingLineTypePatchReq;
import com.apten.household.application.model.request.HouseholdTypeCreateReq;
import com.apten.household.application.model.request.HouseholdTypePatchReq;
import com.apten.household.application.model.response.BuildingLineTypeRes;
import com.apten.household.application.model.response.HouseholdTypeRes;
import com.apten.household.domain.entity.BuildingLineType;
import com.apten.household.domain.entity.HouseholdType;
import com.apten.household.domain.repository.BuildingLineTypeRepository;
import com.apten.household.domain.repository.HouseholdTypeRepository;
import com.apten.household.exception.HouseholdErrorCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class HouseholdTypeService {

    private final HouseholdTypeRepository householdTypeRepository;
    private final BuildingLineTypeRepository buildingLineTypeRepository;

    public HouseholdTypeRes createHouseholdType(HouseholdRequestContext context, HouseholdTypeCreateReq request) {
        validateMasterOrManager(context);
        validateTypeRequest(request.getTypeCode(), request.getTypeName(), request.getExclusiveAreaM2());

        if (householdTypeRepository.existsByTypeCode(request.getTypeCode().trim())) {
            throw new BusinessException(HouseholdErrorCode.DUPLICATE_HOUSEHOLD_TYPE);
        }

        HouseholdType savedType = householdTypeRepository.save(HouseholdType.builder()
                .typeCode(request.getTypeCode().trim())
                .typeName(request.getTypeName().trim())
                .exclusiveAreaM2(request.getExclusiveAreaM2())
                .description(normalizeText(request.getDescription()))
                .isActive(true)
                .build());
        return toTypeRes(savedType);
    }

    @Transactional(readOnly = true)
    public List<HouseholdTypeRes> getHouseholdTypes() {
        return householdTypeRepository.findByIsActiveTrue().stream()
                .map(this::toTypeRes)
                .toList();
    }

    public HouseholdTypeRes updateHouseholdType(HouseholdRequestContext context, Long typeId, HouseholdTypePatchReq request) {
        validateMasterOrManager(context);
        HouseholdType householdType = getHouseholdType(typeId);

        String typeName = normalizeRequired(request.getTypeName());
        BigDecimal exclusiveAreaM2 = request.getExclusiveAreaM2();
        if (exclusiveAreaM2 == null || exclusiveAreaM2.signum() <= 0) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        householdType.update(
                typeName,
                exclusiveAreaM2,
                normalizeText(request.getDescription()),
                request.getIsActive() == null ? householdType.getIsActive() : request.getIsActive()
        );
        return toTypeRes(householdTypeRepository.save(householdType));
    }

    public HouseholdTypeRes deleteHouseholdType(HouseholdRequestContext context, Long typeId) {
        validateMasterOrManager(context);
        HouseholdType householdType = getHouseholdType(typeId);
        if (buildingLineTypeRepository.existsByTypeIdAndIsActiveTrue(typeId)) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_TYPE_IN_USE);
        }
        householdType.deactivate();
        return toTypeRes(householdTypeRepository.save(householdType));
    }

    public BuildingLineTypeRes createBuildingLineType(HouseholdRequestContext context, BuildingLineTypeCreateReq request) {
        validateMasterOrManager(context);
        validateLineRequest(request.getBuilding(), request.getLineStart(), request.getLineEnd(), request.getTypeId());
        HouseholdType householdType = getActiveHouseholdType(request.getTypeId());
        validateLineOverlap(context.getComplexId(), request.getBuilding().trim(), request.getLineStart(), request.getLineEnd(), null);

        BuildingLineType savedLineType = buildingLineTypeRepository.save(BuildingLineType.builder()
                .complexId(context.getComplexId())
                .building(request.getBuilding().trim())
                .lineStart(request.getLineStart())
                .lineEnd(request.getLineEnd())
                .typeId(householdType.getId())
                .isActive(true)
                .build());
        return toLineRes(savedLineType, householdType);
    }

    @Transactional(readOnly = true)
    public List<BuildingLineTypeRes> getBuildingLineTypes(HouseholdRequestContext context, String building) {
        String normalizedBuilding = normalizeText(building);
        List<BuildingLineType> lineTypes = buildingLineTypeRepository.findActiveByFilters(context.getComplexId(), normalizedBuilding);
        Map<Long, HouseholdType> typeMap = householdTypeRepository.findAllById(
                        lineTypes.stream().map(BuildingLineType::getTypeId).collect(Collectors.toSet())
                ).stream()
                .collect(Collectors.toMap(HouseholdType::getId, Function.identity()));

        return lineTypes.stream()
                .map(lineType -> toLineRes(lineType, typeMap.get(lineType.getTypeId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public BuildingLineTypeRes resolveBuildingLineType(HouseholdRequestContext context, String building, String unit) {
        BuildingLineType lineType = resolveBuildingLineTypeOrNull(context.getComplexId(), building, unit);
        if (lineType == null) {
            throw new BusinessException(HouseholdErrorCode.BUILDING_LINE_TYPE_NOT_FOUND);
        }
        HouseholdType householdType = householdTypeRepository.findById(lineType.getTypeId()).orElse(null);
        return toLineRes(lineType, householdType);
    }

    @Transactional(readOnly = true)
    public Long resolveTypeIdOrNull(Long complexId, String building, String unit) {
        BuildingLineType lineType = resolveBuildingLineTypeOrNull(complexId, building, unit);
        return lineType == null ? null : lineType.getTypeId();
    }

    public BuildingLineTypeRes updateBuildingLineType(
            HouseholdRequestContext context,
            Long lineTypeId,
            BuildingLineTypePatchReq request
    ) {
        validateMasterOrManager(context);
        BuildingLineType lineType = buildingLineTypeRepository.findByIdAndComplexId(lineTypeId, context.getComplexId())
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.BUILDING_LINE_TYPE_NOT_FOUND));

        Integer lineStart = request.getLineStart();
        Integer lineEnd = request.getLineEnd();
        Long typeId = request.getTypeId();
        validateLineRequest(lineType.getBuilding(), lineStart, lineEnd, typeId);
        HouseholdType householdType = getActiveHouseholdType(typeId);
        validateLineOverlap(context.getComplexId(), lineType.getBuilding(), lineStart, lineEnd, lineTypeId);

        lineType.update(
                lineStart,
                lineEnd,
                householdType.getId(),
                request.getIsActive() == null ? lineType.getIsActive() : request.getIsActive()
        );
        return toLineRes(buildingLineTypeRepository.save(lineType), householdType);
    }

    public BuildingLineTypeRes deleteBuildingLineType(HouseholdRequestContext context, Long lineTypeId) {
        validateMasterOrManager(context);
        BuildingLineType lineType = buildingLineTypeRepository.findByIdAndComplexId(lineTypeId, context.getComplexId())
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.BUILDING_LINE_TYPE_NOT_FOUND));
        HouseholdType householdType = householdTypeRepository.findById(lineType.getTypeId()).orElse(null);
        lineType.deactivate();
        return toLineRes(buildingLineTypeRepository.save(lineType), householdType);
    }

    private void validateMasterOrManager(HouseholdRequestContext context) {
        if (context.getUserRole() != UserRole.MASTER && context.getUserRole() != UserRole.MANAGER) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
    }

    private void validateTypeRequest(String typeCode, String typeName, BigDecimal exclusiveAreaM2) {
        normalizeRequired(typeCode);
        normalizeRequired(typeName);
        if (exclusiveAreaM2 == null || exclusiveAreaM2.signum() <= 0) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private void validateLineRequest(String building, Integer lineStart, Integer lineEnd, Long typeId) {
        normalizeRequired(building);
        if (lineStart == null || lineEnd == null || typeId == null || lineStart <= 0 || lineEnd <= 0 || lineStart > lineEnd) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private void validateLineOverlap(Long complexId, String building, Integer lineStart, Integer lineEnd, Long excludeId) {
        if (buildingLineTypeRepository.existsActiveOverlap(complexId, building, lineStart, lineEnd, excludeId)) {
            throw new BusinessException(HouseholdErrorCode.BUILDING_LINE_TYPE_OVERLAPPED);
        }
    }

    private BuildingLineType resolveBuildingLineTypeOrNull(Long complexId, String building, String unit) {
        String normalizedBuilding = normalizeRequired(building);
        Integer lineNumber = extractLineNumber(unit);
        return buildingLineTypeRepository.findActiveByUnitLine(complexId, normalizedBuilding, lineNumber)
                .orElse(null);
    }

    private Integer extractLineNumber(String unit) {
        String normalizedUnit = normalizeRequired(unit);
        String digits = normalizedUnit.replaceAll("[^0-9]", "");
        if (digits.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        int unitNumber = Integer.parseInt(digits);
        int lineNumber = unitNumber % 100;
        return lineNumber == 0 ? 100 : lineNumber;
    }

    private HouseholdType getHouseholdType(Long typeId) {
        return householdTypeRepository.findById(typeId)
                .orElseThrow(() -> new BusinessException(HouseholdErrorCode.HOUSEHOLD_TYPE_NOT_FOUND));
    }

    private HouseholdType getActiveHouseholdType(Long typeId) {
        HouseholdType householdType = getHouseholdType(typeId);
        if (!Boolean.TRUE.equals(householdType.getIsActive())) {
            throw new BusinessException(HouseholdErrorCode.HOUSEHOLD_TYPE_NOT_FOUND);
        }
        return householdType;
    }

    private String normalizeRequired(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
        return value.trim();
    }

    private String normalizeText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private HouseholdTypeRes toTypeRes(HouseholdType householdType) {
        return HouseholdTypeRes.builder()
                .typeId(householdType.getId())
                .typeCode(householdType.getTypeCode())
                .typeName(householdType.getTypeName())
                .exclusiveAreaM2(householdType.getExclusiveAreaM2())
                .description(householdType.getDescription())
                .isActive(householdType.getIsActive())
                .build();
    }

    private BuildingLineTypeRes toLineRes(BuildingLineType lineType, HouseholdType householdType) {
        return BuildingLineTypeRes.builder()
                .lineTypeId(lineType.getId())
                .complexId(lineType.getComplexId())
                .building(lineType.getBuilding())
                .lineStart(lineType.getLineStart())
                .lineEnd(lineType.getLineEnd())
                .typeId(lineType.getTypeId())
                .typeCode(householdType == null ? null : householdType.getTypeCode())
                .typeName(householdType == null ? null : householdType.getTypeName())
                .exclusiveAreaM2(householdType == null ? null : householdType.getExclusiveAreaM2())
                .isActive(lineType.getIsActive())
                .build();
    }
}
