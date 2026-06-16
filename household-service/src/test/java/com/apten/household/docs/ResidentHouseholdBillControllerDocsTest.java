package com.apten.household.docs;

import com.apten.household.application.controller.ResidentHouseholdBillController;
import com.apten.household.application.model.response.AdminHouseholdBillDetailRes;
import com.apten.household.application.model.response.BillComparisonRes;
import com.apten.household.application.model.response.MyBillListRes;
import com.apten.household.application.service.HouseholdBillService;
import com.apten.household.domain.enums.HouseholdBillStatus;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 입주민 세대 관리비 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ResidentHouseholdBillController.class)
class ResidentHouseholdBillControllerDocsTest extends HouseholdDocsTestBase {

    @MockBean
    private HouseholdBillService householdBillService;


    @Test
    void 내_세대_관리비_목록_조회() throws Exception {
        MyBillListRes.Item item = MyBillListRes.Item.builder()
                .billId(1L).billYear(2026).billMonth(1)
                .totalFee(BigDecimal.valueOf(50000)).lateFee(BigDecimal.ZERO)
                .payableAmount(BigDecimal.valueOf(50000))
                .dueDate(LocalDate.of(2026, 1, 15))
                .status(HouseholdBillStatus.CONFIRMED).build();

        MyBillListRes res = MyBillListRes.builder()
                .content(List.of(item)).page(0).size(12).totalElements(1L)
                .totalPages(1).hasNext(false).build();

        given(householdBillService.getMyBills(any(), any(), any())).willReturn(res);

        mockMvc.perform(get("/api/household-bills")
                        .header("X-User-Id", "100")
                        .header("X-User-Role", "RESIDENT")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-bill-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 관리비").summary("내 관리비 목록 조회").build())));
    }

    @Test
    void 홈_화면_관리비_카드_조회() throws Exception {
        MyBillListRes.Item res = MyBillListRes.Item.builder()
                .billId(1L).billYear(2026).billMonth(1)
                .totalFee(BigDecimal.valueOf(50000)).payableAmount(BigDecimal.valueOf(50000))
                .dueDate(LocalDate.of(2026, 1, 15))
                .status(HouseholdBillStatus.CONFIRMED).build();

        given(householdBillService.getMyHomeBill(any(), any())).willReturn(res);

        mockMvc.perform(get("/api/household-bills/home")
                        .header("X-User-Id", "100")
                        .header("X-User-Role", "RESIDENT")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-bill-home",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 관리비").summary("홈 화면 관리비 카드 조회").build())));
    }

    @Test
    void 내_관리비_상세_조회() throws Exception {
        AdminHouseholdBillDetailRes res = AdminHouseholdBillDetailRes.builder()
                .billId(1L).householdId(100L).building("101").unit("101")
                .billYear(2026).billMonth(1)
                .baseFee(BigDecimal.valueOf(50000)).vehicleFee(BigDecimal.ZERO)
                .facilityFee(BigDecimal.ZERO).visitorFee(BigDecimal.ZERO)
                .totalFee(BigDecimal.valueOf(50000)).lateFee(BigDecimal.ZERO)
                .payableAmount(BigDecimal.valueOf(50000))
                .dueDate(LocalDate.of(2026, 1, 15))
                .status(HouseholdBillStatus.CONFIRMED)
                .confirmedAt(LocalDateTime.of(2026, 1, 10, 10, 0)).build();

        given(householdBillService.getMyBillDetail(any(), any(), eq(1L))).willReturn(res);

        mockMvc.perform(get("/api/household-bills/{billId}", 1L)
                        .header("X-User-Id", "100")
                        .header("X-User-Role", "RESIDENT")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-bill-detail",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 관리비").summary("내 관리비 상세 조회").build())));
    }

    @Test
    void 관리비_비교_조회() throws Exception {
        BillComparisonRes res = BillComparisonRes.builder()
                .areaSize(BigDecimal.valueOf(84.2))
                .myAmounts(List.of(BigDecimal.valueOf(50000), BigDecimal.valueOf(52000)))
                .myAverage(BigDecimal.valueOf(51000))
                .avgAmounts(List.of(BigDecimal.valueOf(48000), BigDecimal.valueOf(49000)))
                .sameTypeAverage(BigDecimal.valueOf(48500)).build();

        given(householdBillService.getMyBillComparison(any(), any(), eq(1L))).willReturn(res);

        mockMvc.perform(get("/api/household-bills/{billId}/comparison", 1L)
                        .header("X-User-Id", "100")
                        .header("X-User-Role", "RESIDENT")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-bill-comparison",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 관리비").summary("관리비 비교 조회").build())));
    }
}
