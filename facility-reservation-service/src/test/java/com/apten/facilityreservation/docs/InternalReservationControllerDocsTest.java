package com.apten.facilityreservation.docs;

import com.apten.facilityreservation.application.controller.InternalReservationController;
import com.apten.facilityreservation.application.service.GxReservationService;
import com.apten.facilityreservation.application.service.ReservationService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = InternalReservationController.class)
class InternalReservationControllerDocsTest extends FacilityDocsTestBase {

    @MockBean
    private ReservationService reservationService;
    @MockBean
    private GxReservationService gxReservationService;

    @Test
    void 좌석_임시_선점_만료() throws Exception {
        given(reservationService.expireSeatHolds()).willReturn(null);
        mockMvc.perform(post("/internal/reservation-temp-holds/expire"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-temp-hold-expire",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 예약").summary("좌석 임시 선점 만료 처리").build())));
    }

    @Test
    void 일반_예약_완료_처리() throws Exception {
        given(reservationService.completeReservations()).willReturn(null);
        mockMvc.perform(post("/internal/reservations/complete"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-reservation-complete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 예약").summary("일반 시설 예약 완료 처리").build())));
    }

    @Test
    void GX_예약_완료_처리() throws Exception {
        given(gxReservationService.completeGxReservations()).willReturn(null);
        mockMvc.perform(post("/internal/gx-reservations/complete"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-gx-reservation-complete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 예약").summary("GX 예약 완료 처리").build())));
    }
}
