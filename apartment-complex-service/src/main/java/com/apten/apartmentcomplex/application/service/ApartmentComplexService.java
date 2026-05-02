package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.application.model.request.ApartmentComplexReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexSearchReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexStatusPatchReq;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPostReq;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetDetailRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetPageRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPatchRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPostRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPublicRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexStatusPatchRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminDeleteRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminGetRes;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminPostRes;
import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import com.apten.apartmentcomplex.domain.entity.ComplexAdmin;
import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import com.apten.apartmentcomplex.domain.repository.ApartmentComplexRepository;
import com.apten.apartmentcomplex.domain.repository.ComplexAdminRepository;
import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.apartmentcomplex.infrastructure.client.AuthInternalClient;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminCreateReq;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminCreateRes;
import com.apten.apartmentcomplex.infrastructure.kafka.ApartmentComplexOutboxService;
import com.apten.apartmentcomplex.infrastructure.mapper.ApartmentComplexMapper;
import com.apten.common.exception.CommonErrorCode;
import com.apten.common.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 단지 도메인 응용 서비스이다.
// 단지 등록과 조회, 수정, 상태 변경, 관리자 배정 흐름을 이 서비스가 묶는다.
@Service
@Slf4j
@RequiredArgsConstructor
public class ApartmentComplexService {

    private final ApartmentComplexRepository apartmentComplexRepository;
    private final ObjectProvider<ApartmentComplexMapper> apartmentComplexMapper;
    private final ApartmentComplexOutboxService apartmentComplexOutboxService;
    private final ComplexAdminRepository complexAdminRepository;
    private final AuthInternalClient authInternalClient;

    // 최소 생성 정보를 검증한다.
    private void validateCreateApartmentComplexReq(ApartmentComplexReq req) {
        if (req == null
                || isBlank(req.getName())
                || isBlank(req.getAddress())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    // null 또는 blank 문자열인지 확인한다.
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    // 다음 단지 코드를 만들기 위해 마지막 단지 코드를 조회한다.
    private String generateNextComplexCode() {
        return apartmentComplexRepository.findLastCode()
                .map(code -> increaseComplexCode(code))
                .orElse("APT-0001");
    }

    // 마지막 단지 코드 숫자를 1 증가시켜 새 코드를 만든다.
    private String increaseComplexCode(String lastCode) {
        int number = Integer.parseInt(lastCode.substring(4));
        return String.format("APT-%04d", number + 1);
    }

    // 단지 등록 서비스 API-201
    @Transactional
    public ApartmentComplexPostRes createApartmentComplex(ApartmentComplexReq req) {

        validateCreateApartmentComplexReq(req);

        // 단지 등록 요청에 포함된 최초 관리자 정보를 검증한다.
        validateInitialManager(req);

        //중복 체크
        if (apartmentComplexRepository.existsByName(req.getName())) {
            throw new BusinessException(ApartmentComplexErrorCode.DUPLICATE_COMPLEX);
        }

        String code = generateNextComplexCode();

        // 단지 정보를 먼저 저장해 complexId를 확보한다.
        ApartmentComplex apartmentComplex = ApartmentComplex.builder()
                .code(code)
                .name(req.getName())
                .address(req.getAddress())
                .addressDetail(req.getAddressDetail())
                .zipCode(req.getZipCode())
                .status(ApartmentComplexStatus.ACTIVE)
                .description(req.getDescription())
                .build();
        ApartmentComplex savedApartmentComplex = apartmentComplexRepository.save(apartmentComplex);

        // Auth Service 내부 API를 호출해 최초 관리자 계정을 생성한다.
        InternalAdminCreateRes createdAdmin = authInternalClient.createAdmin(
                InternalAdminCreateReq.builder()
                        .complexId(savedApartmentComplex.getId())
                        .email(req.getManagerEmail())
                        .password(req.getManagerPassword())
                        .name(req.getManagerName())
                        .phone(req.getManagerPhone())
                        .adminRole("01")
                        .build()
        );

        // Auth 응답의 userId를 기준으로 단지 관리자 소속을 저장한다.
        ComplexAdmin complexAdmin = ComplexAdmin.builder()
                .complexId(savedApartmentComplex.getId())
                .adminUserId(createdAdmin.getUserId())
                .adminName(createdAdmin.getName())
                .adminRole("01")
                .isActive(true)
                .assignedAt(LocalDateTime.now())
                .build();
        complexAdminRepository.save(complexAdmin);

        // user_cache는 Auth 이벤트를 통해 비동기로 동기화되므로 여기서 직접 저장하지 않는다.
        // Kafka Outbox 구조는 기존 단지 이벤트 발행 흐름을 그대로 유지한다.
        // TODO: 외부 Auth 호출이 트랜잭션 안에 있으므로 이후 보상 처리 전략을 검토한다.

        // Kafka 전송은 relay가 담당하므로 같은 트랜잭션 안에서는 Outbox row만 남긴다
        apartmentComplexOutboxService.saveCreatedEvent(savedApartmentComplex);

        return ApartmentComplexPostRes.builder()
                .complexId(savedApartmentComplex.getId())
                .code(savedApartmentComplex.getCode())
                .name(savedApartmentComplex.getName())
                .managerUserId(createdAdmin.getUserId())
                .managerName(createdAdmin.getName())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 관리자 단지 목록 조회 서비스 API-202
    @Transactional(readOnly = true)
    public ApartmentComplexGetPageRes getApartmentComplexList(ApartmentComplexSearchReq req) {
        // 요청값이 없을 때 기본 페이지와 사이즈를 설정한다.
        int page = req.getPage();
        int size = req.getSize();

        // JPA 페이징 처리를 위한 PageRequest를 생성한다.
        PageRequest pageRequest = PageRequest.of(page, size);

        // 키워드 조건을 적용해 단지 목록을 페이지 단위로 조회한다.
        Page<ApartmentComplex> result = apartmentComplexRepository.findPageByKeyword(
                req.getKeyword(),
                pageRequest
        );

        // 조회된 엔티티 목록을 API 응답 DTO 목록으로 변환한다.
        List<ApartmentComplexGetRes> content = result.getContent()
                .stream()
                .map(complex -> ApartmentComplexGetRes.builder()
                        .code(complex.getCode())
                        .name(complex.getName())
                        .address(complex.getAddress())
                        .status(complex.getStatus())
                        .description(complex.getDescription())
                        .createdAt(complex.getCreatedAt())
                        .build())
                .toList();

        // 페이지 메타데이터와 목록 데이터를 함께 응답한다.
        return ApartmentComplexGetPageRes.builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .hasNext(result.hasNext())
                .build();
    }

    // 관리자 단지 상세 조회 서비스 API-203
    public ApartmentComplexGetDetailRes getApartmentComplexDetail(String code) {
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));
        return ApartmentComplexGetDetailRes.builder()
                .apartmentComplexId(complex.getId())
                .code(complex.getCode())
                .name(complex.getName())
                .address(complex.getAddress())
                .addressDetail(complex.getAddressDetail())
                .zipcode(complex.getZipCode())
                .status(complex.getStatus())
                .description(complex.getDescription())
                .createdAt(complex.getCreatedAt())
                .updatedAt(complex.getUpdatedAt())
                .build();
    }

    // 단지 수정 서비스 API-204
    @Transactional
    public ApartmentComplexPatchRes updateApartmentComplex(String code, ApartmentComplexReq req) {
        // API의 단지 code로 보고 수정 대상 단지를 조회한다
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        // 단지 원본 정보를 갱신하되 상태 변경 로직은 별도 API가 생길 때 분리한다
        complex.update(
                req.getName(),
                req.getAddress(),
                req.getAddressDetail(),
                req.getZipCode(),
                req.getDescription()
        );

        // Kafka 직접 발행 대신 수정 이벤트를 같은 트랜잭션 안에서 Outbox에 적재한다
        apartmentComplexOutboxService.saveUpdatedEvent(complex);

        return ApartmentComplexPatchRes.builder()
                .code(code)
                .name(complex.getName())
                .updatedAt(complex.getUpdatedAt())
                .build();
    }

    // 관리자 단지 소속 지정 서비스 API-206이다.
    @Transactional
    public ComplexAdminPostRes assignAdminToComplex(String code, ComplexAdminPostReq req) {
        // 단지 코드로 배정 대상을 먼저 찾는다.
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        if (complex.getStatus() == ApartmentComplexStatus.DELETED) {
            throw new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND);
        }

        // 관리자 생성 요청의 필수값과 역할 코드를 검증한다.
        if (req == null
                || isBlank(req.getEmail())
                || isBlank(req.getPassword())
                || isBlank(req.getName())
                || isBlank(req.getPhone())
                || isBlank(req.getAdminRole())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        validateAdminRole(req.getAdminRole());

        // 관리자 생성 시 Auth Service 내부 API 호출 예정이다.
        InternalAdminCreateRes createdAdmin = authInternalClient.createAdmin(
                InternalAdminCreateReq.builder()
                        .complexId(complex.getId())
                        .email(req.getEmail())
                        .password(req.getPassword())
                        .name(req.getName())
                        .phone(req.getPhone())
                        .adminRole(req.getAdminRole())
                        .build()
        );

        // 같은 단지에 대한 기존 배정 이력이 있으면 활성 상태에 따라 재사용한다.
        ComplexAdmin admin = complexAdminRepository.findByComplexIdAndAdminUserId(complex.getId(), createdAdmin.getUserId())
                .map(existingAdmin -> reactivateAdminAssignment(existingAdmin, createdAdmin))
                .orElseGet(() -> ComplexAdmin.builder()
                        .complexId(complex.getId())
                        .adminUserId(createdAdmin.getUserId())
                        .adminName(createdAdmin.getName())
                        .adminRole(req.getAdminRole())
                        .isActive(true)
                        .assignedAt(LocalDateTime.now())
                        .build());

        // Auth 응답의 userId를 기준으로 단지 관리자 소속을 저장한다.
        complexAdminRepository.save(admin);

        // user_cache는 Auth 이벤트를 통해 비동기로 동기화되므로 여기서 직접 저장하지 않는다.

        return ComplexAdminPostRes.builder()
                .code(code)
                .userId(admin.getAdminUserId())
                .name(admin.getAdminName())
                .email(createdAdmin.getEmail())
                .adminRole(admin.getAdminRole())
                .isActive(admin.getIsActive())
                .assignedAt(admin.getAssignedAt())
                .build();
    }

    // 단지 관리자 배정을 해제한다.
    @Transactional
    public ComplexAdminDeleteRes unassignAdminFromComplex(String code, Long userId) {
        // 단지 코드로 해제 대상을 먼저 찾는다.
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        // complex_admin에서 현재 또는 과거 배정 이력을 조회한다.
        ComplexAdmin admin = complexAdminRepository.findByComplexIdAndAdminUserId(complex.getId(), userId)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_ADMIN_NOT_FOUND));

        // 이미 비활성 상태면 다시 해제할 수 없으므로 잘못된 요청으로 처리한다.
        if (!Boolean.TRUE.equals(admin.getIsActive())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 실제 삭제 대신 소프트 해제로 배정 상태만 끈다.
        admin.unassign();
        complexAdminRepository.save(admin);

        // TODO: 관리자 삭제 시 소속 해제 후 Auth Service 내부 API로 계정을 소프트 삭제한다.

        return ComplexAdminDeleteRes.builder()
                .code(code)
                .userId(admin.getAdminUserId())
                .isActive(admin.getIsActive())
                .unassignedAt(admin.getUnassignedAt())
                .build();
    }

    // 단지별 현재 활성 관리자 목록을 조회한다.
    @Transactional(readOnly = true)
    public List<ComplexAdminGetRes> getComplexAdminList(String code) {
        // 단지 코드로 조회 대상을 먼저 찾는다.
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        // 현재 활성화된 관리자만 조회해 상세 화면에 내려준다.
        return complexAdminRepository.findByComplexIdAndIsActiveTrue(complex.getId()).stream()
                .map(admin -> ComplexAdminGetRes.builder()
                        .userId(admin.getAdminUserId())
                        .name(admin.getAdminName())
                        .isActive(admin.getIsActive())
                        .assignedAt(admin.getAssignedAt())
                        .unassignedAt(admin.getUnassignedAt())
                        .build())
                .toList();
    }

    // 단지 활성 상태를 별도 API에서 변경한다.
    @Transactional
    public ApartmentComplexStatusPatchRes changeApartmentComplexStatus(String code, ApartmentComplexStatusPatchReq req) {
        // 단지 코드로 상태 변경 대상을 먼저 찾는다.
        ApartmentComplex complex = apartmentComplexRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.COMPLEX_NOT_FOUND));

        // 바꿀 상태가 없으면 요청이 잘못된 것으로 본다.
        if (req == null || req.getStatus() == null) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }

        // 단지 상태를 별도 API에서만 바꾸도록 명시적으로 반영한다.
        complex.changeStatus(req.getStatus());

        // 단지 비활성화는 별도 이벤트로, 나머지 상태 변경은 일반 수정 이벤트로 적재한다.
        if (req.getStatus() == ApartmentComplexStatus.INACTIVE) {
            apartmentComplexOutboxService.saveDeactivatedEvent(complex);
        } else {
            apartmentComplexOutboxService.saveUpdatedEvent(complex);
        }

        return ApartmentComplexStatusPatchRes.builder()
                .code(complex.getCode())
                .status(complex.getStatus())
                .updatedAt(complex.getUpdatedAt())
                .build();
    }

    // 공개 단지 목록 조회 서비스 API-209이다.
    @Transactional(readOnly = true)
    public List<ApartmentComplexPublicRes> getAvailableApartmentComplexes(String keyword) {
        // 회원가입과 단지 선택 화면에는 활성 단지만 노출한다.
        return apartmentComplexRepository.findPublicListByKeyword(keyword, ApartmentComplexStatus.ACTIVE)
                .stream()
                .map(complex -> ApartmentComplexPublicRes.builder()
                        .code(complex.getCode())
                        .name(complex.getName())
                        .address(complex.getAddress())
                        .build())
                .toList();
    }

    private void validateInitialManager(ApartmentComplexReq req) {
        if (isBlank(req.getManagerEmail())
                || isBlank(req.getManagerPassword())
                || isBlank(req.getManagerName())
                || isBlank(req.getManagerPhone())) {
            throw new BusinessException(CommonErrorCode.INVALID_PARAMETER);
        }
    }

    private void validateAdminRole(String adminRole) {
        if (!"01".equals(adminRole) && !"02".equals(adminRole)) {
            throw new BusinessException(ApartmentComplexErrorCode.INVALID_ADMIN_ROLE);
        }
    }

    // 기존 비활성 배정은 재활성화하고, 이미 활성 배정이면 중복으로 본다.
    private ComplexAdmin reactivateAdminAssignment(ComplexAdmin existingAdmin, InternalAdminCreateRes createdAdmin) {
        // 이미 활성화된 배정이면 같은 단지에 다시 배정할 수 없다.
        if (Boolean.TRUE.equals(existingAdmin.getIsActive())) {
            throw new BusinessException(ApartmentComplexErrorCode.DUPLICATE_COMPLEX_ADMIN);
        }

        // 비활성 이력은 이름을 최신화하고 다시 활성 상태로 전환한다.
        existingAdmin.reassign(createdAdmin.getName(), createdAdmin.getAdminRole());
        return existingAdmin;
    }
}
