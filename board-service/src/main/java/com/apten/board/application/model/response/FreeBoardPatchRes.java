package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 자유게시판 수정 응답 DTO
@Getter
@Builder
public class FreeBoardPatchRes {
    private final String boardUid;
    private final String title;
    private final LocalDateTime updatedAt;
}
