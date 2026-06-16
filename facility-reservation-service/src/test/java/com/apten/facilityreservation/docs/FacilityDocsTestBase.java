package com.apten.facilityreservation.docs;

import com.apten.common.security.UserRole;
import com.apten.facilityreservation.application.model.dto.FacilityRequestContext;
import com.apten.facilityreservation.application.service.FacilityRequestContextResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

abstract class FacilityDocsTestBase {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    protected FacilityRequestContextResolver facilityRequestContextResolver;

    @BeforeEach
    void setUpBase(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();

        FacilityRequestContext adminCtx = FacilityRequestContext.builder()
                .userId(100L).complexId(10L).userRole(UserRole.MANAGER).build();
        FacilityRequestContext residentCtx = FacilityRequestContext.builder()
                .userId(100L).complexId(10L).userRole(UserRole.USER).build();

        given(facilityRequestContextResolver.resolveAdminContext(any(), any(), any(), any())).willReturn(adminCtx);
        given(facilityRequestContextResolver.resolveResidentContext(any(), any(), any())).willReturn(residentCtx);
    }
}
