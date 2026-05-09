package com.apten.board.application.model.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

// 관리자 게시글 강제 삭제 응답이다.
@Getter
@Builder
public class AdminPostDeleteRes {
    private final Long postId;
    private final LocalDateTime deletedAt;
}
