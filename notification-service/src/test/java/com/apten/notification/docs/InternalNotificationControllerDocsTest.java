package com.apten.notification.docs;

import com.apten.notification.application.controller.InternalNotificationController;
import com.apten.notification.application.model.request.NotificationPostReq;
import com.apten.notification.application.model.response.*;
import com.apten.notification.application.service.NotificationDeliveryService;
import com.apten.notification.application.service.NotificationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 내부 알림 생성 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = InternalNotificationController.class)
class InternalNotificationControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationDeliveryService notificationDeliveryService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 내부_알림_생성() throws Exception {
        Map<String, Object> req = Map.of(
                "receiverUserId", 100L,
                "type", "FACILITY_RESERVATION",
                "targetType", "RESERVATION",
                "targetId", 200L,
                "title", "예약 완료",
                "content", "시설 예약이 완료되었습니다."
        );

        NotificationPostRes res = NotificationPostRes.builder()
                .notificationId(1L).receiverUserId(100L)
                .createdAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(notificationService.createNotification(any(NotificationPostReq.class))).willReturn(res);

        mockMvc.perform(post("/internal/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-notification-create",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 알림").summary("내부 알림 생성").build())));
    }

    @Test
    void 내부_푸시_발송() throws Exception {
        Map<String, Object> req = Map.of("notificationId", 1L, "receiverUserId", 100L);

        NotificationPushPostRes res = NotificationPushPostRes.builder()
                .notificationId(1L).sent(true)
                .sentAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(notificationDeliveryService.sendPushNotification(any())).willReturn(res);

        mockMvc.perform(post("/internal/notifications/push")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-notification-push",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 알림").summary("내부 푸시 발송").build())));
    }

    @Test
    void 관리자_브로드캐스트_알림_생성() throws Exception {
        Map<String, Object> req = Map.of(
                "complexId", 10L,
                "type", "ADMIN_BROADCAST",
                "title", "공지사항 등록",
                "content", "새 공지가 등록되었습니다."
        );

        NotificationAdminBroadcastPostRes res = NotificationAdminBroadcastPostRes.builder()
                .targetCount(5).createdCount(4).build();

        given(notificationService.createAdminBroadcastNotification(any())).willReturn(res);

        mockMvc.perform(post("/internal/notifications/admin-broadcast")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-notification-admin-broadcast",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 알림").summary("관리자 브로드캐스트 알림 생성").build())));
    }

    @Test
    void 오래된_알림_정리() throws Exception {
        NotificationCleanupRes res = NotificationCleanupRes.builder()
                .deletedCount(30).executedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(notificationService.cleanupOldNotifications()).willReturn(res);

        mockMvc.perform(post("/internal/notifications/cleanup"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("internal-notification-cleanup",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Internal - 알림").summary("오래된 알림 정리").build())));
    }
}
