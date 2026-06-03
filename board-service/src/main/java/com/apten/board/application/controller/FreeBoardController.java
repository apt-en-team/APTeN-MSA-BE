package com.apten.board.application.controller;

import com.apten.board.application.model.request.MyPostListReq;
import com.apten.board.application.model.request.PopularPostListReq;
import com.apten.board.application.model.request.PostCreateReq;
import com.apten.board.application.model.request.PostListReq;
import com.apten.board.application.model.request.PostPatchReq;
import com.apten.board.application.model.response.MyPostListRes;
import com.apten.board.application.model.response.PageResponse;
import com.apten.board.application.model.response.PopularPostListRes;
import com.apten.board.application.model.response.PostCreateRes;
import com.apten.board.application.model.response.PostDeleteRes;
import com.apten.board.application.model.response.PostDetailRes;
import com.apten.board.application.model.response.PostLikeToggleRes;
import com.apten.board.application.model.response.PostListRes;
import com.apten.board.application.model.response.PostPatchRes;
import com.apten.board.application.service.FreeBoardService;
import com.apten.board.domain.enums.BoardCategory;
import com.apten.common.response.ResultResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// 게시글 API 컨트롤러이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class FreeBoardController {

    // 게시글 서비스이다.
    private final FreeBoardService freeBoardService;

    //게시글 작성
    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<PostCreateRes> createPost(@RequestBody PostCreateReq request) {
        return ResultResponse.success("게시글 작성 성공", freeBoardService.createPost(request));
    }

    @GetMapping("/posts")
    public ResultResponse<PageResponse<PostListRes>> getPostList(
            @RequestParam(required = false) BoardCategory category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        PostListReq request = PostListReq.builder()
                .category(category)
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();
        return ResultResponse.success("게시글 목록 조회 성공", freeBoardService.getPostList(request));
    }

    //게시글 상세 조회
    @GetMapping("/posts/{postId}")
    public ResultResponse<PostDetailRes> getPostDetail(@PathVariable Long postId) {
        return ResultResponse.success("게시글 상세 조회 성공", freeBoardService.getPostDetail(postId));
    }

    //게시글 수정
    @PatchMapping("/posts/{postId}")
    public ResultResponse<PostPatchRes> updatePost(@PathVariable Long postId, @RequestBody PostPatchReq request) {
        return ResultResponse.success("게시글 수정 성공", freeBoardService.updatePost(postId, request));
    }

    //게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResultResponse<PostDeleteRes> deletePost(@PathVariable Long postId) {
        return ResultResponse.success("게시글 삭제 성공", freeBoardService.deletePost(postId));
    }

    //좋아요 등록/취소
    @PostMapping("/posts/{postId}/likes/toggle")
    public ResultResponse<PostLikeToggleRes> togglePostLike(@PathVariable Long postId) {
        return ResultResponse.success("좋아요 등록 또는 취소 성공", freeBoardService.togglePostLike(postId));
    }

    //내 게시글 조회
    @GetMapping("/my-posts")
    public ResultResponse<PageResponse<MyPostListRes>> getMyPostList(@ModelAttribute MyPostListReq request) {
        return ResultResponse.success("내 게시글 조회 성공", freeBoardService.getMyPostList(request));
    }

    //인기글 조회
    @GetMapping("/posts/popular")
    public ResultResponse<List<PopularPostListRes>> getPopularPostList(@ModelAttribute PopularPostListReq request) {
        return ResultResponse.success("인기글 조회 성공", freeBoardService.getPopularPostList(request));
    }
}
