package com.apten.board.application.model.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 투표 등록 요청 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotePostReq {
    private String title;
    private String description;
    private List<String> options;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
