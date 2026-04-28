package com.apten.board.infrastructure.kafka;

import com.apten.board.domain.entity.BoardComment;
import com.apten.board.domain.entity.BoardPost;
import com.apten.board.domain.entity.Notice;
import com.apten.board.domain.entity.Vote;
import com.apten.board.exception.BoardErrorCode;
import com.apten.board.infrastructure.kafka.payload.CommentCreatedEventPayload;
import com.apten.board.infrastructure.kafka.payload.NoticeCreatedEventPayload;
import com.apten.board.infrastructure.kafka.payload.PostCreatedEventPayload;
import com.apten.board.infrastructure.kafka.payload.VoteClosedEventPayload;
import com.apten.board.infrastructure.kafka.payload.VoteCreatedEventPayload;
import com.apten.common.exception.BusinessException;
import com.apten.common.outbox.Outbox;
import com.apten.common.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 게시판 도메인 이벤트를 Outbox에 적재하는 서비스이다.
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apten.outbox", name = "enabled", havingValue = "true")
public class BoardOutboxService {

    // 공지 생성 토픽이다.
    private static final String NOTICE_CREATED_TOPIC = "notice.created";

    // 게시글 생성 토픽이다.
    private static final String POST_CREATED_TOPIC = "post.created";

    // 댓글 생성 토픽이다.
    private static final String COMMENT_CREATED_TOPIC = "comment.created";

    // 투표 생성 토픽이다.
    private static final String VOTE_CREATED_TOPIC = "vote.created";

    // 투표 종료 토픽이다.
    private static final String VOTE_CLOSED_TOPIC = "vote.closed";

    // Outbox 저장소이다.
    private final OutboxRepository outboxRepository;

    // JSON 직렬화 도구이다.
    private final ObjectMapper objectMapper;

    // 공지 생성 이벤트를 적재한다.
    public void saveNoticeCreatedEvent(Notice notice) {
        NoticeCreatedEventPayload payload = NoticeCreatedEventPayload.builder()
                .noticeId(notice.getId())
                .complexId(notice.getComplexId())
                .userId(notice.getUserId())
                .title(notice.getTitle())
                .createdAt(notice.getCreatedAt())
                .build();
        saveOutboxEvent(NOTICE_CREATED_TOPIC, NOTICE_CREATED_TOPIC, notice.getId(), payload);
    }

    // 게시글 생성 이벤트를 적재한다.
    public void savePostCreatedEvent(BoardPost post) {
        PostCreatedEventPayload payload = PostCreatedEventPayload.builder()
                .postId(post.getId())
                .complexId(post.getComplexId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .createdAt(post.getCreatedAt())
                .build();
        saveOutboxEvent(POST_CREATED_TOPIC, POST_CREATED_TOPIC, post.getId(), payload);
    }

    // 댓글 생성 이벤트를 적재한다.
    public void saveCommentCreatedEvent(BoardComment comment, Long complexId) {
        CommentCreatedEventPayload payload = CommentCreatedEventPayload.builder()
                .commentId(comment.getId())
                .postId(comment.getPostId())
                .complexId(complexId)
                .userId(comment.getUserId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
        saveOutboxEvent(COMMENT_CREATED_TOPIC, COMMENT_CREATED_TOPIC, comment.getId(), payload);
    }

    // 투표 생성 이벤트를 적재한다.
    public void saveVoteCreatedEvent(Vote vote) {
        VoteCreatedEventPayload payload = VoteCreatedEventPayload.builder()
                .voteId(vote.getId())
                .noticeId(vote.getNoticeId())
                .complexId(vote.getComplexId())
                .title(vote.getTitle())
                .startAt(vote.getStartAt())
                .endAt(vote.getEndAt())
                .createdAt(vote.getCreatedAt())
                .build();
        saveOutboxEvent(VOTE_CREATED_TOPIC, VOTE_CREATED_TOPIC, vote.getId(), payload);
    }

    // 투표 종료 이벤트를 적재한다.
    public void saveVoteClosedEvent(Vote vote) {
        VoteClosedEventPayload payload = VoteClosedEventPayload.builder()
                .voteId(vote.getId())
                .complexId(vote.getComplexId())
                .title(vote.getTitle())
                .closedAt(LocalDateTime.now())
                .build();
        saveOutboxEvent(VOTE_CLOSED_TOPIC, VOTE_CLOSED_TOPIC, vote.getId(), payload);
    }

    // payload를 공통 envelope로 감싸서 Outbox에 저장한다.
    private <T> void saveOutboxEvent(String topic, String eventType, Long aggregateId, T payload) {
        BoardEventEnvelope<T> eventEnvelope = BoardEventEnvelope.<T>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .version(1)
                .occurredAt(Instant.now())
                .producer("board-service")
                .payload(payload)
                .build();

        Outbox outbox = Outbox.builder()
                .topic(topic)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(writePayload(eventEnvelope))
                .build();

        outboxRepository.save(outbox);
    }

    // 직렬화 실패는 비즈니스 예외로 감싼다.
    private String writePayload(Object eventEnvelope) {
        try {
            return objectMapper.writeValueAsString(eventEnvelope);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(BoardErrorCode.EVENT_PUBLISH_FAILED);
        }
    }
}
