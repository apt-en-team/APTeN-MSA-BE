package com.apten.common.kafka.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventPayload {

    private Long receiverUserId;
    private Long complexId;
    private String type;
    private String targetType;
    private Long targetId;
    private String title;
    private String content;
    private String linkPath;
    private String payloadJson;
}
