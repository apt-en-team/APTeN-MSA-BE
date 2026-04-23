package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.application.model.request.ApartmentComplexPatchReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexPostReq;
import com.apten.apartmentcomplex.application.model.request.ApartmentComplexSearchReq;
import com.apten.apartmentcomplex.application.model.request.ComplexAdminPostReq;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetDetailRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexGetPageRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPatchRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPostRes;
import com.apten.apartmentcomplex.application.model.response.ApartmentComplexPublicRes;
import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import com.apten.apartmentcomplex.application.model.response.ComplexAdminPostRes;
import com.apten.apartmentcomplex.domain.repository.ApartmentComplexRepository;
import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.apartmentcomplex.infrastructure.kafka.ApartmentComplexOutboxService;
import com.apten.apartmentcomplex.infrastructure.mapper.ApartmentComplexQueryMapper;
import com.apten.common.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 단지 도메인 응용 서비스
// 단지 등록, 조회, 수정과 관리자 소속 지정을 이 서비스가 묶는다
@Service
@RequiredArgsConstructor
public class ApartmentComplexService {

    // 단지 기본 저장소
    private final ApartmentComplexRepository apartmentComplexRepository;

    // 단지 조회용 MyBatis 매퍼
    private final ObjectProvider<ApartmentComplexQueryMapper> apartmentComplexQueryMapperProvider;

    // 단지 원본 변경 이벤트를 Kafka 직접 발행 대신 Outbox에 저장하는 서비스
    private final ApartmentComplexOutboxService apartmentComplexOutboxService;

    // 단지 등록 서비스
    @Transactional
    public ApartmentComplexPostRes createApartmentComplex(ApartmentComplexPostReq request) {
        // 단지 원본을 먼저 저장해 aggregateId로 사용할 Long PK를 확보한다
        ApartmentComplex apartmentComplex = ApartmentComplex.builder()
                .code(UUID.randomUUID().toString())
                .name(request.getName())
                .address(request.getRoadAddress())
                .addressDetail(request.getDetailAddress())
                .zipCode(request.getZipcode())
                .status(ApartmentComplexStatus.ACTIVE)
                .build();
        ApartmentComplex savedApartmentComplex = apartmentComplexRepository.save(apartmentComplex);

        // Kafka 전송은 relay가 담당하므로 같은 트랜잭션 안에서는 Outbox row만 남긴다
        apartmentComplexOutboxService.saveCreatedEvent(savedApartmentComplex);

        return ApartmentComplexPostRes.builder()
                .apartmentComplexId(savedApartmentComplex.getId())
                .apartmentComplexUid(savedApartmentComplex.getCode())
                .name(savedApartmentComplex.getName())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 관리자 단지 목록 조회 서비스
    public ApartmentComplexGetPageRes getApartmentComplexList(ApartmentComplexSearchReq request) {
        // TODO: 단지 목록 조회 로직 구현
        return ApartmentComplexGetPageRes.empty(request.getPage(), request.getSize());
    }

    // 관리자 단지 상세 조회 서비스
    public ApartmentComplexGetDetailRes getApartmentComplexDetail(String apartmentComplexUid) {
        // TODO: 단지 상세 조회 로직 구현
        return ApartmentComplexGetDetailRes.builder().build();
    }

    // 단지 수정 서비스
    @Transactional
    public ApartmentComplexPatchRes updateApartmentComplex(String apartmentComplexUid, ApartmentComplexPatchReq request) {
        // API의 단지 UID를 엔티티 code로 보고 수정 대상 단지를 조회한다
        ApartmentComplex apartmentComplex = apartmentComplexRepository.findByCode(apartmentComplexUid)
                .orElseThrow(() -> new BusinessException(ApartmentComplexErrorCode.APARTMENT_COMPLEX_NOT_FOUND));

        // 단지 원본 정보를 갱신하되 상태 변경 로직은 별도 API가 생길 때 분리한다
        apartmentComplex.update(
                request.getName(),
                request.getRoadAddress(),
                request.getDetailAddress(),
                request.getZipcode(),
                apartmentComplex.getStatus(),
                null
        );

        // Kafka 직접 발행 대신 수정 이벤트를 같은 트랜잭션 안에서 Outbox에 적재한다
        apartmentComplexOutboxService.saveUpdatedEvent(apartmentComplex);

        return ApartmentComplexPatchRes.builder()
                .apartmentComplexUid(apartmentComplexUid)
                .name(apartmentComplex.getName())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 관리자 단지 소속 지정 서비스
    public ComplexAdminPostRes assignAdminToComplex(String apartmentComplexUid, ComplexAdminPostReq request) {
        // TODO: 관리자 단지 소속 지정 로직 구현
        return ComplexAdminPostRes.builder()
                .apartmentComplexUid(apartmentComplexUid)
                .userUid(request.getUserUid())
                .adminRole(request.getAdminRole())
                .assignedAt(LocalDateTime.now())
                .build();
    }

    // 공개 단지 목록 조회 서비스
    public List<ApartmentComplexPublicRes> getAvailableApartmentComplexes(String keyword) {
        // TODO: 가입 화면용 공개 단지 목록 조회 로직 구현
        return List.of();
    }
}
