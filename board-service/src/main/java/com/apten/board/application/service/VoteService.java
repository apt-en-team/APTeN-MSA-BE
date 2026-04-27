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
import com.apten.board.domain.repository.NoticeRepository;
import com.apten.board.domain.repository.VoteParticipationRepository;
import com.apten.board.domain.repository.VoteRepository;
import com.apten.board.exception.BoardErrorCode;
import com.apten.board.infrastructure.kafka.BoardOutboxService;
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

    // 투표 저장소이다.
    private final VoteRepository voteRepository;

    // 공지 저장소이다.
    private final NoticeRepository noticeRepository;

    // 투표 참여 저장소이다.
    private final VoteParticipationRepository voteParticipationRepository;

    // 세대원 캐시 저장소이다.
    private final HouseholdMemberCacheRepository householdMemberCacheRepository;

    // 투표 outbox 서비스이다.
    private final BoardOutboxService boardOutboxService;

    //투표 생성
    @Transactional
    public VoteCreateRes createVote(VoteCreateReq request) {
        validateVoteDate(request.getStartAt(), request.getEndAt());
        validateNotice(request.getNoticeId());

        Vote vote = voteRepository.save(Vote.builder()
                .complexId(currentComplexId())
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

    //투표 참여
    @Transactional
    public VoteParticipationRes participateVote(Long voteId, VoteParticipationReq request) {
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

    //투표 결과 조회
    @Transactional(readOnly = true)
    public VoteResultRes getVoteResult(Long voteId) {
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

    //투표 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<VoteListRes> getVoteList(VoteListReq request) {
        Pageable pageable = buildPageable(request.getPage(), request.getSize());
        Page<Vote> page = voteRepository.findByComplexId(currentComplexId(), pageable);

        return toVotePage(page);
    }

    //투표 상세 조회
    @Transactional(readOnly = true)
    public VoteDetailRes getVoteDetail(Long voteId) {
        Vote vote = getVote(voteId);
        return toVoteDetailRes(vote);
    }

    //관리자 투표 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<VoteListRes> getAdminVoteList(VoteListReq request) {
        return getVoteList(request);
    }

    //관리자 투표 상세 조회
    @Transactional(readOnly = true)
    public VoteDetailRes getAdminVoteDetail(Long voteId) {
        return getVoteDetail(voteId);
    }

    //투표 수정
    @Transactional
    public VotePatchRes updateVote(Long voteId, VotePatchReq request) {
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

    //투표 삭제
    @Transactional
    public VoteDeleteRes deleteVote(Long voteId) {
        Vote vote = getVote(voteId);
        voteRepository.delete(vote);

        return VoteDeleteRes.builder()
                .voteId(voteId)
                .deletedAt(LocalDateTime.now())
                .build();
    }

    //투표 종료 처리
    @Transactional
    public VoteCloseRes closeVote(Long voteId) {
        Vote vote = getVote(voteId);
        vote.close();
        boardOutboxService.saveVoteClosedEvent(vote);

        return VoteCloseRes.builder()
                .voteId(vote.getId())
                .status(vote.getStatus())
                .closedAt(LocalDateTime.now())
                .build();
    }

    //투표를 조회한다.
    private Vote getVote(Long voteId) {
        return voteRepository.findByIdAndComplexId(voteId, currentComplexId())
                .orElseThrow(() -> new BusinessException(BoardErrorCode.VOTE_NOT_FOUND));
    }

    //연결 공지 존재 여부를 검증한다.
    private void validateNotice(Long noticeId) {
        if (noticeRepository.findByIdAndComplexIdAndIsDeletedFalse(noticeId, currentComplexId()).isEmpty()) {
            throw new BusinessException(BoardErrorCode.NOTICE_NOT_FOUND);
        }
    }

    //투표 기간 검증이다.
    private void validateVoteDate(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null || endAt == null || endAt.isBefore(startAt)) {
            throw new BusinessException(BoardErrorCode.INVALID_VOTE_DATE);
        }
    }

    //현재 사용자 ID를 가져온다.
    private Long currentUserId() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null || userContext.getUserId() == null) {
            throw new BusinessException(BoardErrorCode.UNAUTHORIZED);
        }
        return userContext.getUserId();
    }

    //현재 단지 ID를 가져온다.
    private Long currentComplexId() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null || userContext.getComplexId() == null) {
            throw new BusinessException(BoardErrorCode.INVALID_PARAMETER);
        }
        return userContext.getComplexId();
    }

    //페이지 요청을 안전하게 만든다.
    private Pageable buildPageable(Integer page, Integer size) {
        int safePage = page == null || page < 0 ? 0 : page;
        int safeSize = size == null || size <= 0 ? 20 : size;
        return PageRequest.of(safePage, safeSize);
    }

    //투표 페이지 응답으로 변환한다.
    private PageResponse<VoteListRes> toVotePage(Page<Vote> page) {
        return PageResponse.<VoteListRes>builder()
                .content(page.getContent().stream().map(vote -> VoteListRes.builder()
                        .voteId(vote.getId())
                        .title(vote.getTitle())
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

    //투표 상세 응답으로 변환한다.
    private VoteDetailRes toVoteDetailRes(Vote vote) {
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
                .build();
    }
}
