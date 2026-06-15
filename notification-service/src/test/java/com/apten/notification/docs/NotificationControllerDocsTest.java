package com.apten.notification.docs;

import com.apten.notification.application.controller.NotificationController;
import com.apten.notification.application.model.response.*;
import com.apten.notification.application.service.NotificationService;
import com.apten.notification.application.service.NotificationSettingService;
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
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 사용자 알림 API 문서화 테스트
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = NotificationController.class)
class NotificationControllerDocsTest {

    private MockMvc mockMvc;

    @MockBean
    @SuppressWarnings("unused")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationSettingService notificationSettingService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext ctx, RestDocumentationContextProvider restDocs) {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocs))
                .build();
    }

    @Test
    void 알림_목록_조회() throws Exception {
        NotificationRes item = NotificationRes.builder()
                .notificationId(1L).title("예약 완료").content("시설 예약이 완료되었습니다.")
                .type("FACILITY_RESERVATION").typeCode("01").typeValue("시설 예약")
                .targetType("RESERVATION").targetTypeCode("01").targetTypeValue("예약")
                .targetId(100L).linkPath("/reservations/100").build();

        NotificationGetPageRes res = NotificationGetPageRes.builder()
                .content(List.of(item)).page(0).size(20).totalElements(1).totalPages(1).hasNext(false).build();

        given(notificationService.getNotificationList(eq(100L), any())).willReturn(res);

        mockMvc.perform(get("/api/notifications")
                        .header("X-User-Id", "100")
                        .param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("notification-list",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Notification - 알림").summary("알림 목록 조회").build())));
    }

    @Test
    void 미읽음_알림_수_조회() throws Exception {
        NotificationUnreadCountRes res = NotificationUnreadCountRes.builder().unreadCount(3L).build();

        given(notificationService.getUnreadCount(eq(100L))).willReturn(res);

        mockMvc.perform(get("/api/notifications/unread-count").header("X-User-Id", "100"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("notification-unread-count",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Notification - 알림").summary("미읽음 알림 수 조회").build())));
    }

    @Test
    void 알림_읽음_처리() throws Exception {
        NotificationReadRes res = NotificationReadRes.builder()
                .notificationId(1L).isRead(true).readAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(notificationService.readNotification(eq(100L), eq(1L))).willReturn(res);

        mockMvc.perform(patch("/api/notifications/{notificationId}/read", 1L)
                        .header("X-User-Id", "100"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("notification-read",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Notification - 알림").summary("알림 읽음 처리").build())));
    }

    @Test
    void 전체_알림_읽음_처리() throws Exception {
        NotificationReadAllRes res = NotificationReadAllRes.builder()
                .updatedCount(5).readAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(notificationService.readAllNotifications(eq(100L))).willReturn(res);

        mockMvc.perform(patch("/api/notifications/read-all").header("X-User-Id", "100"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("notification-read-all",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Notification - 알림").summary("전체 알림 읽음 처리").build())));
    }

    @Test
    void 알림_설정_조회() throws Exception {
        NotificationSettingItemRes settingItem = NotificationSettingItemRes.builder()
                .category("FACILITY_RESERVATION").categoryCode("01").categoryValue("시설 예약").enabled(true).build();

        NotificationSettingGetRes res = NotificationSettingGetRes.builder().settings(List.of(settingItem)).build();

        given(notificationSettingService.getSettings(eq(100L), any())).willReturn(res);

        mockMvc.perform(get("/api/notifications/settings")
                        .header("X-User-Id", "100")
                        .header("X-Complex-Id", "10"))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("notification-settings-get",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Notification - 알림").summary("알림 설정 조회").build())));
    }

    @Test
    void 알림_설정_변경() throws Exception {
        Map<String, Object> req = Map.of("settings",
                List.of(Map.of("category", "FACILITY_RESERVATION", "enabled", false)));

        NotificationSettingItemRes settingItem = NotificationSettingItemRes.builder()
                .category("FACILITY_RESERVATION").categoryCode("01").categoryValue("시설 예약").enabled(false).build();

        NotificationSettingPatchRes res = NotificationSettingPatchRes.builder()
                .updated(true).settings(List.of(settingItem))
                .updatedAt(LocalDateTime.of(2026, 1, 5, 10, 0)).build();

        given(notificationSettingService.updateSettings(eq(100L), any(), any())).willReturn(res);

        mockMvc.perform(patch("/api/notifications/settings")
                        .header("X-User-Id", "100")
                        .header("X-Complex-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andDo(MockMvcRestDocumentationWrapper.document("notification-settings-update",
                        ResourceDocumentation.resource(ResourceSnippetParameters.builder()
                                .tag("Notification - 알림").summary("알림 설정 변경").build())));
    }
}
