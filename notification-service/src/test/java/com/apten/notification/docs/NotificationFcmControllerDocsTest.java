package com.apten.notification.docs;

import com.apten.notification.application.controller.NotificationFcmController;
import com.apten.notification.application.model.response.*;
import com.apten.notification.application.service.NotificationFcmService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// FCM 토큰 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = NotificationFcmController.class)
class NotificationFcmControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private NotificationFcmService notificationFcmService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void FCM_토큰_등록() throws Exception {
        Map<String, String> req = Map.of("fcmToken", "fcm-token-value-abc123", "deviceType", "ANDROID");

        NotificationFcmTokenPostRes res = NotificationFcmTokenPostRes.builder()
                .tokenRegistered(true).updatedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(notificationFcmService.registerFcmToken(eq(100L), any(), any())).willReturn(res);

        mockMvc.perform(post("/api/notifications/fcm-tokens")
                        .header("X-User-Id", "100")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("notification-fcm-token-register",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Notification - FCM").summary("FCM 토큰 등록").build())));
    }

    @Test
    void FCM_토큰_해제() throws Exception {
        Map<String, String> req = Map.of("fcmToken", "fcm-token-value-abc123");

        NotificationFcmTokenDeleteRes res = NotificationFcmTokenDeleteRes.builder()
                .tokenDeleted(true).build();

        given(notificationFcmService.deleteFcmToken(eq(100L), any())).willReturn(res);

        mockMvc.perform(delete("/api/notifications/fcm-tokens")
                        .header("X-User-Id", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("notification-fcm-token-delete",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Notification - FCM").summary("FCM 토큰 해제").build())));
    }

    @Test
    void FCM_토큰_갱신() throws Exception {
        Map<String, String> req = Map.of("oldToken", "old-token-abc", "newToken", "new-token-xyz");

        NotificationFcmTokenPatchRes res = NotificationFcmTokenPatchRes.builder()
                .tokenUpdated(true).updatedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(notificationFcmService.updateFcmToken(eq(100L), any(), any())).willReturn(res);

        mockMvc.perform(patch("/api/notifications/fcm-tokens")
                        .header("X-User-Id", "100")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("notification-fcm-token-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Notification - FCM").summary("FCM 토큰 갱신").build())));
    }
}
