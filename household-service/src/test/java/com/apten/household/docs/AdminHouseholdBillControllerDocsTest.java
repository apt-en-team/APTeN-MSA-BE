package com.apten.household.docs;

import com.apten.household.application.controller.AdminHouseholdBillController;
import com.apten.household.application.model.response.*;
import com.apten.household.application.service.HouseholdBillService;
import com.apten.household.domain.enums.HouseholdBillStatus;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 관리자 세대 관리비 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AdminHouseholdBillController.class)
class AdminHouseholdBillControllerDocsTest extends HouseholdDocsTestBase {

    @MockBean
    private HouseholdBillService householdBillService;


    @Test
    void 기본_관리비_반영() throws Exception {
        Map<String, Object> req = Map.of("billYear", 2026, "billMonth", 1);

        BaseFeeReflectRes res = BaseFeeReflectRes.builder()
                .complexId(10L).billYear(2026).billMonth(1)
                .affectedHouseholdCount(50).reflectedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(householdBillService.reflectBaseFee(any(), any())).willReturn(res);

        mockMvc.perform(post("/api/admin/household-bills/base-fees")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-bill-base-fee-reflect",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 관리비").summary("기본 관리비 반영").build())));
    }

    @Test
    void 관리자_관리비_목록_조회() throws Exception {
        AdminHouseholdBillListRes.Item item = AdminHouseholdBillListRes.Item.builder()
                .billId(1L).householdId(100L).building("101").unit("101")
                .billYear(2026).billMonth(1).build();

        AdminHouseholdBillListRes res = AdminHouseholdBillListRes.builder()
                .content(List.of(item)).page(0).size(20).totalElements(1L)
                .totalBillCount(1L).draftCount(1L).confirmedCount(0L)
                .totalPages(1).hasNext(false).build();

        given(householdBillService.getAdminBills(any(), any())).willReturn(res);

        mockMvc.perform(get("/api/admin/household-bills")
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10")
                        .param("billYear", "2026").param("billMonth", "1"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-bill-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 관리비").summary("관리자 관리비 목록 조회").build())));
    }

    @Test
    void 관리자_관리비_상세_조회() throws Exception {
        AdminHouseholdBillDetailRes res = AdminHouseholdBillDetailRes.builder()
                .billId(1L).householdId(100L).building("101").unit("101")
                .billYear(2026).billMonth(1)
                .baseFee(BigDecimal.valueOf(50000)).vehicleFee(BigDecimal.ZERO)
                .facilityFee(BigDecimal.ZERO).visitorFee(BigDecimal.ZERO)
                .totalFee(BigDecimal.valueOf(50000)).lateFee(BigDecimal.ZERO)
                .payableAmount(BigDecimal.valueOf(50000))
                .sendDate(LocalDate.of(2026, 1, 1)).dueDate(LocalDate.of(2026, 1, 15))
                .status(HouseholdBillStatus.DRAFT).build();

        given(householdBillService.getAdminBillDetail(any(), eq(1L))).willReturn(res);

        mockMvc.perform(get("/api/admin/household-bills/{billId}", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-bill-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 관리비").summary("관리자 관리비 상세 조회").build())));
    }

    @Test
    void 관리비_확정() throws Exception {
        BillConfirmRes res = BillConfirmRes.builder()
                .billId(1L).status(HouseholdBillStatus.CONFIRMED)
                .confirmedAt(LocalDateTime.of(2026, 1, 10, 10, 0)).build();

        given(householdBillService.confirmBill(any(), eq(1L))).willReturn(res);

        mockMvc.perform(patch("/api/admin/household-bills/{billId}/confirm", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-bill-confirm",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 관리비").summary("관리비 확정").build())));
    }

    @Test
    void 관리비_확정_취소() throws Exception {
        BillUnconfirmRes res = BillUnconfirmRes.builder()
                .billId(1L).status(HouseholdBillStatus.DRAFT)
                .updatedAt(LocalDateTime.of(2026, 1, 11, 10, 0)).build();

        given(householdBillService.unconfirmBill(any(), eq(1L))).willReturn(res);

        mockMvc.perform(patch("/api/admin/household-bills/{billId}/unconfirm", 1L)
                        .header("X-User-Id", "100").header("X-User-Role", "MANAGER")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("admin-bill-unconfirm",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Admin - 세대 관리비").summary("관리비 확정 취소").build())));
    }
}
