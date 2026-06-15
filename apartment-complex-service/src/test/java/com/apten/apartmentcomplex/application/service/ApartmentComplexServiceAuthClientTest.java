package com.apten.apartmentcomplex.application.service;

import com.apten.apartmentcomplex.domain.entity.ApartmentComplex;
import com.apten.apartmentcomplex.domain.entity.ComplexAdmin;
import com.apten.apartmentcomplex.domain.enums.ApartmentComplexStatus;
import com.apten.apartmentcomplex.domain.repository.ApartmentComplexRepository;
import com.apten.apartmentcomplex.domain.repository.ComplexAdminRepository;
import com.apten.apartmentcomplex.domain.repository.ComplexFeatureRepository;
import com.apten.apartmentcomplex.exception.ApartmentComplexErrorCode;
import com.apten.apartmentcomplex.infrastructure.client.AuthInternalFeignClient;
import com.apten.apartmentcomplex.infrastructure.client.model.InternalAdminDeleteRes;
import com.apten.apartmentcomplex.infrastructure.kafka.ApartmentComplexOutboxService;
import com.apten.apartmentcomplex.infrastructure.mapper.ApartmentComplexMapper;
import com.apten.common.exception.BusinessException;
import com.apten.common.response.ResultResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApartmentComplexServiceAuthClientTest {

    @Mock private ApartmentComplexRepository apartmentComplexRepository;
    @Mock private ComplexAdminRepository complexAdminRepository;
    @Mock private ComplexFeatureRepository complexFeatureRepository;
    @Mock private ApartmentComplexOutboxService apartmentComplexOutboxService;
    @Mock private AuthInternalFeignClient authInternalFeignClient;
    @Mock private ApartmentComplexMapper apartmentComplexMapper;

    @InjectMocks
    private ApartmentComplexService apartmentComplexService;

    private ApartmentComplex activeComplex(Long id) {
        return ApartmentComplex.builder()
                .code("C001").name("테스트단지")
                .status(ApartmentComplexStatus.ACTIVE)
                .build();
    }

    private ComplexAdmin activeAdmin(Long complexId, Long userId) {
        return ComplexAdmin.builder()
                .complexId(complexId).adminUserId(userId)
                .adminName("홍길동").adminEmail("admin@test.com")
                .adminPhone("010-0000-0000").adminRole("01")
                .isActive(true).assignedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("관리자 해제 — softDeleteAdmin FeignClient 호출 확인")
    void unassignAdmin_callsSoftDeleteFeignClient() {
        Long complexId = 1L;
        Long userId = 100L;

        ApartmentComplex complex = activeComplex(complexId);
        ComplexAdmin admin = activeAdmin(complexId, userId);

        InternalAdminDeleteRes deleteRes = InternalAdminDeleteRes.builder()
                .deletedAt(LocalDateTime.now()).build();

        given(apartmentComplexRepository.findById(complexId)).willReturn(Optional.of(complex));
        given(complexAdminRepository.findByComplexIdAndAdminUserId(any(), eq(userId)))
                .willReturn(Optional.of(admin));
        given(authInternalFeignClient.softDeleteAdmin(userId))
                .willReturn(ResultResponse.<InternalAdminDeleteRes>builder()
                        .success(true).code("SUCCESS").data(deleteRes).build());

        // MANAGER 역할로 본인 단지 관리자 해제
        apartmentComplexService.unassignAdminFromMyComplex(complexId, null, "MANAGER", userId);

        verify(authInternalFeignClient).softDeleteAdmin(userId);
    }

    @Test
    @DisplayName("관리자 해제 — 이미 비활성 상태이면 예외 발생")
    void unassignAdmin_alreadyInactive_throwsException() {
        Long complexId = 1L;
        Long userId = 100L;

        ApartmentComplex complex = activeComplex(complexId);
        ComplexAdmin inactiveAdmin = ComplexAdmin.builder()
                .complexId(complexId).adminUserId(userId)
                .isActive(false).assignedAt(LocalDateTime.now()).build();

        given(apartmentComplexRepository.findById(complexId)).willReturn(Optional.of(complex));
        given(complexAdminRepository.findByComplexIdAndAdminUserId(any(), eq(userId)))
                .willReturn(Optional.of(inactiveAdmin));

        assertThatThrownBy(() ->
                apartmentComplexService.unassignAdminFromMyComplex(complexId, null, "MANAGER", userId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("관리자 해제 — 배정 이력 없으면 COMPLEX_ADMIN_NOT_FOUND")
    void unassignAdmin_notFound_throwsComplexAdminNotFound() {
        Long complexId = 1L;
        Long userId = 999L;

        given(apartmentComplexRepository.findById(complexId)).willReturn(Optional.of(activeComplex(complexId)));
        given(complexAdminRepository.findByComplexIdAndAdminUserId(any(), eq(userId)))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                apartmentComplexService.unassignAdminFromMyComplex(complexId, null, "MANAGER", userId))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ApartmentComplexErrorCode.COMPLEX_ADMIN_NOT_FOUND));
    }

    @Test
    @DisplayName("관리자 해제 — RESIDENT 역할이면 FORBIDDEN")
    void unassignAdmin_invalidRole_throwsForbidden() {
        assertThatThrownBy(() ->
                apartmentComplexService.unassignAdminFromMyComplex(1L, null, "RESIDENT", 100L))
                .isInstanceOf(BusinessException.class);
    }
}
