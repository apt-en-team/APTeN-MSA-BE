package com.apten.household.docs;

import com.apten.household.application.controller.ResidentHouseholdController;
import com.apten.household.application.model.response.MyHouseholdRes;
import com.apten.household.application.service.HouseholdService;
import com.apten.household.domain.enums.HouseholdStatus;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 입주민 세대 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ResidentHouseholdController.class)
class ResidentHouseholdControllerDocsTest extends HouseholdDocsTestBase {

    @MockBean
    private HouseholdService householdService;


    @Test
    void 내_세대_정보_조회() throws Exception {
        MyHouseholdRes res = MyHouseholdRes.builder()
                .householdId(1L).complexId(10L).building("101").unit("101")
                .status(HouseholdStatus.OCCUPIED).build();

        given(householdService.getMyHousehold(any(), any())).willReturn(res);

        mockMvc.perform(get("/api/households/me")
                        .header("X-User-Id", "100")
                        .header("X-User-Role", "RESIDENT")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("resident-household-my",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Resident - 세대").summary("내 세대 정보 조회").build())));
    }
}
