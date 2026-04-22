package com.apten.board.application.service;

import com.apten.board.application.model.request.CommentSearchReq;
import com.apten.board.application.model.request.FreeBoardPatchReq;
import com.apten.board.application.model.request.FreeBoardPostReq;
import com.apten.board.application.model.request.FreeBoardSearchReq;
import com.apten.board.application.model.response.BoardLikeToggleRes;
import com.apten.board.application.model.response.BoardViewPostRes;
import com.apten.board.application.model.response.FreeBoardDeleteRes;
import com.apten.board.application.model.response.FreeBoardGetDetailRes;
import com.apten.board.application.model.response.FreeBoardGetPageRes;
import com.apten.board.application.model.response.FreeBoardPatchRes;
import com.apten.board.application.model.response.FreeBoardPostRes;
import com.apten.board.application.model.response.MyBoardGetPageRes;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

// 자유게시판 응용 서비스
// 게시글 조회와 등록, 수정, 삭제, 좋아요와 조회수 시그니처를 이 서비스에 모아둔다
@Service
public class FreeBoardService {

    // 자유게시판 목록 조회 서비스
    public FreeBoardGetPageRes getFreeBoardList(FreeBoardSearchReq request) {
        // TODO: 자유게시판 목록 조회 로직 구현
        return FreeBoardGetPageRes.empty(request.getPage(), request.getSize());
    }

    // 자유게시판 상세 조회 서비스
    public FreeBoardGetDetailRes getFreeBoardDetail(String boardUid) {
        // TODO: 자유게시판 상세 조회 로직 구현
        return FreeBoardGetDetailRes.builder().build();
    }

    // 자유게시판 등록 서비스
    public FreeBoardPostRes createFreeBoard(FreeBoardPostReq request) {
        // TODO: 자유게시판 등록 로직 구현
        return FreeBoardPostRes.builder()
                .title(request.getTitle())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 자유게시판 수정 서비스
    public FreeBoardPatchRes updateFreeBoard(String boardUid, FreeBoardPatchReq request) {
        // TODO: 자유게시판 수정 로직 구현
        return FreeBoardPatchRes.builder()
                .boardUid(boardUid)
                .title(request.getTitle())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 자유게시판 삭제 서비스
    public FreeBoardDeleteRes deleteFreeBoard(String boardUid) {
        // TODO: 자유게시판 삭제 로직 구현
        return FreeBoardDeleteRes.builder()
                .message("자유게시판 삭제 완료")
                .deletedAt(LocalDateTime.now())
                .build();
    }

    // 내 게시글 조회 서비스
    public MyBoardGetPageRes getMyBoardList(CommentSearchReq request) {
        // TODO: 내 게시글 조회 로직 구현
        return MyBoardGetPageRes.empty(request.getPage(), request.getSize());
    }

    // 게시글 조회수 증가 서비스
    public BoardViewPostRes increaseBoardView(String boardUid) {
        // TODO: 게시글 조회수 증가 로직 구현
        return BoardViewPostRes.builder()
                .boardUid(boardUid)
                .viewCount(0)
                .build();
    }

    // 게시글 좋아요 토글 서비스
    public BoardLikeToggleRes toggleBoardLike(String boardUid) {
        // TODO: 좋아요 토글 로직 구현
        // TODO: 좋아요 수 증감 처리
        return BoardLikeToggleRes.builder()
                .boardUid(boardUid)
                .isLiked(false)
                .likeCount(0)
                .build();
    }
}
