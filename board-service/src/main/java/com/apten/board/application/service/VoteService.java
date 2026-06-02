package com.apten.board.application.service;

import com.apten.board.application.model.request.VoteCreateReq;
import com.apten.board.application.model.request.VoteListReq;
import com.apten.board.application.model.request.VoteParticipationReq;
import com.apten.board.application.model.request.VotePatchReq;
import com.apten.board.application.model.response.PageResponse;
import com.apten.board.application.model.response.VoteCloseRes;
import com.apten.board.application.model.response.VoteCreateRes;
import com.apten.board.application.model.response.VoteDeleteRes;
import com.apten.board.application.model.response.VoteDetailRes;
import com.apten.board.application.model.response.VoteListRes;
import com.apten.board.application.model.response.VotePageRes;
import com.apten.board.application.model.response.VoteParticipationRes;
import com.apten.board.application.model.response.VotePatchRes;
import com.apten.board.application.model.response.VoteResultRes;
import com.apten.board.domain.entity.HouseholdMemberCache;
import com.apten.board.domain.entity.Vote;
import com.apten.board.domain.entity.VoteParticipation;
import com.apten.board.domain.enums.HouseholdMemberRole;
import com.apten.board.domain.enums.VoteChoice;
import com.apten.board.domain.enums.VoteStatus;
import com.apten.board.domain.repository.HouseholdMemberCacheRepository;
import com.apten.board.domain.repository.VoteParticipationRepository;
import com.apten.board.domain.repository.VoteRepository;
import com.apten.board.exception.BoardErrorCode;
import com.apten.board.infrastructure.kafka.BoardOutboxService;
import com.apten.common.enums.FeatureCode;
import com.apten.common.exception.BusinessException;
import com.apten.common.security.UserContext;
import com.apten.common.security.UserContextHolder;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 투표 응용 서비스이다.
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteParticipationRepository voteParticipationRepository;
    private final HouseholdMemberCacheRepository householdMemberCacheRepository;
    private final BoardOutboxService boardOutboxService;
    private final FeatureAccessService featureAccessService;

    // 투표 생성
    @Transactional
    public VoteCreateRes createVote(VoteCreateReq request) {
        Long complexId = validateVoteFeatureAndGetComplexId();
        validateVoteDate(request.getStartAt(), request.getEndAt());

        Vote vote = voteRepository.save(Vote.builder()
                .complexId(complexId)
                .noticeId(request.getNoticeId())
                .title(request.getTitle())
                .description(request.getDescription())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .build());

        boardOutboxService.saveVoteCreatedEvent(vote);

        return VoteCreateRes.builder()
                .voteId(vote.getId())
                .noticeId(vote.getNoticeId())
                .title(vote.getTitle())
                .startAt(vote.getStartAt())
                .endAt(vote.getEndAt())
                .status(vote.getStatus())
                .createdAt(vote.getCreatedAt())
                .build();
    }

    // 투표 참여
    @Transactional
    public VoteParticipationRes participateVote(Long voteId, VoteParticipationReq request) {
        validateVoteFeatureAndGetComplexId();
        Vote vote = getVote(voteId);
        HouseholdMemberCache memberCache = householdMemberCacheRepository.findByUserIdAndIsActiveTrue(currentUserId())
                .orElseThrow(() -> new BusinessException(BoardErrorCode.VOTE_HEAD_ONLY));

        if (memberCache.getRole() != HouseholdMemberRole.HEAD) {
            throw new BusinessException(BoardErrorCode.VOTE_HEAD_ONLY);
        }

        if (voteParticipationRepository.existsByVoteIdAndHouseholdId(voteId, memberCache.getHouseholdId())) {
            throw new BusinessException(BoardErrorCode.DUPLICATE_VOTE);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(vote.getStartAt()) || now.isAfter(vote.getEndAt())) {
            throw new BusinessException(BoardErrorCode.VOTE_PERIOD_INVALID);
        }
        if (vote.getStatus() == VoteStatus.CLOSED) {
            throw new BusinessException(BoardErrorCode.INVALID_VOTE_STATUS);
        }
        if (vote.getStatus() == VoteStatus.READY) {
            vote.open();
        }

        VoteChoice choice = VoteChoice.valueOf(request.getChoice());
        VoteParticipation participation = voteParticipationRepository.save(VoteParticipation.builder()
                .voteId(voteId)
                .householdId(memberCache.getHouseholdId())
                .userId(currentUserId())
                .choice(choice)
                .build());

        if (choice == VoteChoice.AGREE) {
            vote.increaseAgree();
        } else {
            vote.increaseDisagree();
        }

        return VoteParticipationRes.builder()
                .voteId(vote.getId())
                .householdId(participation.getHouseholdId())
                .choice(participation.getChoice())
                .participatedAt(participation.getCreatedAt())
                .build();
    }

    // 투표 결과 조회
    @Transactional(readOnly = true)
    public VoteResultRes getVoteResult(Long voteId) {
        validateVoteFeatureAndGetComplexId();
        Vote vote = getVote(voteId);
        return VoteResultRes.builder()
                .voteId(vote.getId())
                .title(vote.getTitle())
                .status(vote.getStatus())
                .agreeCount(vote.getAgreeCount())
                .disagreeCount(vote.getDisagreeCount())
                .householdCount(vote.getHouseholdCount())
                .build();
    }

    // 입주민 투표 목록 조회
    // - 투표중 탭: OPEN 상태 + READY이면서 startAt <= now (시작일 지났지만 아직 첫 참여 없는 투표 포함)
    // - 결과발표 탭: CLOSED 상태
    @Transactional(readOnly = true)
    public PageResponse<VoteListRes> getVoteList(VoteListReq request) {
        Long complexId = validateVoteFeatureAndGetComplexId();
        Pageable pageable = buildPageable(request.getPage(), request.getSize());

        Page<Vote> page;
        String status = request.getStatus();

        if ("CLOSED".equals(status)) {
            page = voteRepository.findByComplexIdAndStatusOrderByCreatedAtDesc(complexId, VoteStatus.CLOSED, pageable);
        } else {
            // 기본(투표중 탭): OPEN + 시작일 지난 READY 포함
            page = voteRepository.findActiveVotes(complexId, LocalDateTime.now(), VoteStatus.OPEN, VoteStatus.READY, pageable);
        }

        return toVotePage(page);
    }

    // 투표 상세 조회
    @Transactional(readOnly = true)
    public VoteDetailRes getVoteDetail(Long voteId) {
        validateVoteFeatureAndGetComplexId();
        Vote vote = getVote(voteId);
        return toVoteDetailRes(vote);
    }

    // 관리자 투표 목록 조회 — 상태별 통계 포함
    @Transactional(readOnly = true)
    public VotePageRes getAdminVoteList(VoteListReq request) {
        Long complexId = validateVoteFeatureAndGetComplexId();
        Pageable pageable = buildPageable(request.getPage(), request.getSize());

        Page<Vote> page;
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            VoteStatus status = VoteStatus.valueOf(request.getStatus());
            page = voteRepository.findByComplexIdAndStatusOrderByCreatedAtDesc(complexId, status, pageable);
        } else {
            page = voteRepository.findByComplexIdOrderByCreatedAtDesc(complexId, pageable);
        }

        long total = voteRepository.countByComplexId(complexId);
        long open = voteRepository.countByComplexIdAndStatus(complexId, VoteStatus.OPEN);
        long ready = voteRepository.countByComplexIdAndStatus(complexId, VoteStatus.READY);
        long closed = voteRepository.countByComplexIdAndStatus(complexId, VoteStatus.CLOSED);

        return VotePageRes.builder()
                .content(page.getContent().stream().map(vote -> VoteListRes.builder()
                        .voteId(vote.getId())
                        .title(vote.getTitle())
                        .description(vote.getDescription())
                        .startAt(vote.getStartAt())
                        .endAt(vote.getEndAt())
                        .status(vote.getStatus())
                        .agreeCount(vote.getAgreeCount())
                        .disagreeCount(vote.getDisagreeCount())
                        .householdCount(vote.getHouseholdCount())
                        .build()).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .summary(VotePageRes.Summary.builder()
                        .total(total)
                        .open(open)
                        .ready(ready)
                        .closed(closed)
                        .build())
                .build();
    }

    // 관리자 투표 상세 조회
    @Transactional(readOnly = true)
    public VoteDetailRes getAdminVoteDetail(Long voteId) {
        return getVoteDetail(voteId);
    }

    // 투표 수정
    @Transactional
    public VotePatchRes updateVote(Long voteId, VotePatchReq request) {
        validateVoteFeatureAndGetComplexId();
        Vote vote = getVote(voteId);
        validateVoteDate(request.getStartAt(), request.getEndAt());
        vote.update(request.getTitle(), request.getDescription(), request.getStartAt(), request.getEndAt());

        return VotePatchRes.builder()
                .voteId(vote.getId())
                .title(vote.getTitle())
                .startAt(vote.getStartAt())
                .endAt(vote.getEndAt())
                .updatedAt(vote.getUpdatedAt())
                .build();
    }

    // 투표 삭제
    @Transactional
    public VoteDeleteRes deleteVote(Long voteId) {
        validateVoteFeatureAndGetComplexId();
        Vote vote = getVote(voteId);
        voteRepository.delete(vote);

        return VoteDeleteRes.builder()
                .voteId(voteId)
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // 투표 종료 처리
    @Transactional
    public VoteCloseRes closeVote(Long voteId) {
        validateVoteFeatureAndGetComplexId();
        Vote vote = getVote(voteId);
        vote.close();
        boardOutboxService.saveVoteClosedEvent(vote);

        return VoteCloseRes.builder()
                .voteId(vote.getId())
                .status(vote.getStatus())
                .closedAt(LocalDateTime.now())
                .build();
    }

    private Vote getVote(Long voteId) {
        return voteRepository.findByIdAndComplexId(voteId, currentComplexId())
                .orElseThrow(() -> new BusinessException(BoardErrorCode.VOTE_NOT_FOUND));
    }

    private Long validateVoteFeatureAndGetComplexId() {
        Long complexId = currentComplexId();
        featureAccessService.validateEnabled(complexId, FeatureCode.VOTE);
        return complexId;
    }

    private void validateVoteDate(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null || endAt == null || endAt.isBefore(startAt)) {
            throw new BusinessException(BoardErrorCode.INVALID_VOTE_DATE);
        }
    }

    private Long currentUserId() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null || userContext.getUserId() == null) {
            throw new BusinessException(BoardErrorCode.UNAUTHORIZED);
        }
        return userContext.getUserId();
    }

    private Long currentComplexId() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null || userContext.getComplexId() == null) {
            throw new BusinessException(BoardErrorCode.INVALID_PARAMETER);
        }
        return userContext.getComplexId();
    }

    private Pageable buildPageable(Integer page, Integer size) {
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size <= 0 ? 20 : size;
        return PageRequest.of(safePage, safeSize);
    }

    private PageResponse<VoteListRes> toVotePage(Page<Vote> page) {
        return PageResponse.<VoteListRes>builder()
                .content(page.getContent().stream().map(vote -> VoteListRes.builder()
                        .voteId(vote.getId())
                        .title(vote.getTitle())
                        .description(vote.getDescription())
                        .startAt(vote.getStartAt())
                        .endAt(vote.getEndAt())
                        .status(vote.getStatus())
                        .agreeCount(vote.getAgreeCount())
                        .disagreeCount(vote.getDisagreeCount())
                        .householdCount(vote.getHouseholdCount())
                        .build()).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();
    }

    private VoteDetailRes toVoteDetailRes(Vote vote) {
        // 현재 사용자의 참여 이력을 조회한다.
        Long userId = currentUserIdOrNull();
        VoteParticipation participation = userId != null
                ? voteParticipationRepository.findByVoteIdAndUserId(vote.getId(), userId).orElse(null)
                : null;

        return VoteDetailRes.builder()
                .voteId(vote.getId())
                .noticeId(vote.getNoticeId())
                .complexId(vote.getComplexId())
                .title(vote.getTitle())
                .description(vote.getDescription())
                .startAt(vote.getStartAt())
                .endAt(vote.getEndAt())
                .status(vote.getStatus())
                .agreeCount(vote.getAgreeCount())
                .disagreeCount(vote.getDisagreeCount())
                .householdCount(vote.getHouseholdCount())
                .createdAt(vote.getCreatedAt())
                .updatedAt(vote.getUpdatedAt())
                .isParticipated(participation != null)
                .myChoice(participation != null ? participation.getChoice() : null)
                .build();
    }

    // 현재 사용자 ID를 가져온다. 비로그인이면 null을 반환한다.
    private Long currentUserIdOrNull() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null) return null;
        return userContext.getUserId();
    }
}