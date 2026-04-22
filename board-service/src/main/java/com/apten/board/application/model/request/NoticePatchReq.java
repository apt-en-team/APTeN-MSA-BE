package com.apten.board.application.model.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 공지 수정 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticePatchReq {
    private String title;
    private String content;
    private Boolean isPinned;
    private List<String> attachmentUids;
}
