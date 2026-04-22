package com.apten.board.application.model.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 자유게시판 등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeBoardPostReq {
    private String title;
    private String content;
    private List<String> attachmentUids;
}
