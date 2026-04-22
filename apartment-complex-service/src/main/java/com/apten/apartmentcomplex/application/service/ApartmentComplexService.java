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
import com.apten.apartmentcomplex.application.model.response.ComplexAdminPostRes;
import com.apten.apartmentcomplex.domain.repository.ApartmentComplexRepository;
import com.apten.apartmentcomplex.infrastructure.mapper.ApartmentComplexQueryMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

// 단지 도메인 응용 서비스
// 단지 등록, 조회, 수정과 관리자 소속 지정을 이 서비스가 묶는다
@Service
@RequiredArgsConstructor
public class ApartmentComplexService {

    // 단지 기본 저장소
    private final ApartmentComplexRepository apartmentComplexRepository;

    // 단지 조회용 MyBatis 매퍼
    private final ObjectProvider<ApartmentComplexQueryMapper> apartmentComplexQueryMapperProvider;

    // 단지 등록 서비스
    public ApartmentComplexPostRes createApartmentComplex(ApartmentComplexPostReq request) {
        // TODO: 단지 등록 로직 구현
        // TODO: 단지 생성 이벤트 Kafka 발행
        return ApartmentComplexPostRes.builder()
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
    public ApartmentComplexPatchRes updateApartmentComplex(String apartmentComplexUid, ApartmentComplexPatchReq request) {
        // TODO: 단지 수정 로직 구현
        // TODO: 단지 수정 이벤트 Kafka 발행
        return ApartmentComplexPatchRes.builder()
                .apartmentComplexUid(apartmentComplexUid)
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
