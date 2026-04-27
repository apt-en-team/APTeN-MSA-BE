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
import com.apten.common.response.ResultResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// 게시글 API 컨트롤러이다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class FreeBoardController {

    // 게시글 서비스이다.
    private final FreeBoardService freeBoardService;

    //게시글 작성 API-501
    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse<PostCreateRes> createPost(@RequestBody PostCreateReq request) {
        return ResultResponse.success("게시글 작성 성공", freeBoardService.createPost(request));
    }

    //게시글 목록 조회 API-502
    @GetMapping("/posts")
    public ResultResponse<PageResponse<PostListRes>> getPostList(@ModelAttribute PostListReq request) {
        return ResultResponse.success("게시글 목록 조회 성공", freeBoardService.getPostList(request));
    }

    //게시글 상세 조회 API-503
    @GetMapping("/posts/{postId}")
    public ResultResponse<PostDetailRes> getPostDetail(@PathVariable Long postId) {
        return ResultResponse.success("게시글 상세 조회 성공", freeBoardService.getPostDetail(postId));
    }

    //게시글 수정 API-504
    @PatchMapping("/posts/{postId}")
    public ResultResponse<PostPatchRes> updatePost(@PathVariable Long postId, @RequestBody PostPatchReq request) {
        return ResultResponse.success("게시글 수정 성공", freeBoardService.updatePost(postId, request));
    }

    //게시글 삭제 API-505
    @DeleteMapping("/posts/{postId}")
    public ResultResponse<PostDeleteRes> deletePost(@PathVariable Long postId) {
        return ResultResponse.success("게시글 삭제 성공", freeBoardService.deletePost(postId));
    }

    //좋아요 등록/취소 API-509
    @PostMapping("/posts/{postId}/likes/toggle")
    public ResultResponse<PostLikeToggleRes> togglePostLike(@PathVariable Long postId) {
        return ResultResponse.success("좋아요 등록 또는 취소 성공", freeBoardService.togglePostLike(postId));
    }

    //내 게시글 조회 API-522
    @GetMapping("/my-posts")
    public ResultResponse<PageResponse<MyPostListRes>> getMyPostList(@ModelAttribute MyPostListReq request) {
        return ResultResponse.success("내 게시글 조회 성공", freeBoardService.getMyPostList(request));
    }

    //인기글 조회 API-524
    @GetMapping("/posts/popular")
    public ResultResponse<List<PopularPostListRes>> getPopularPostList(@ModelAttribute PopularPostListReq request) {
        return ResultResponse.success("인기글 조회 성공", freeBoardService.getPopularPostList(request));
    }
}
