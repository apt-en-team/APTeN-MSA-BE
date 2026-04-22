package com.apten.board.application.model.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 자유게시판 수정 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeBoardPatchReq {
    private String title;
    private String content;
    private List<String> attachmentUids;
}
