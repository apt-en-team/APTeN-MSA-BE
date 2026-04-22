package com.apten.board.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 게시판 서비스 에러코드
@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode {

    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "BRD_404_01", "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "BRD_404_02", "댓글을 찾을 수 없습니다."),
    BOARD_WRITER_MISMATCH(HttpStatus.FORBIDDEN, "BRD_403_01", "본인이 작성한 게시글만 수정 또는 삭제할 수 있습니다."),
    COMMENT_WRITER_MISMATCH(HttpStatus.FORBIDDEN, "BRD_403_02", "본인이 작성한 댓글만 수정 또는 삭제할 수 있습니다."),

    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "BRD_404_03", "투표를 찾을 수 없습니다."),
    DUPLICATE_VOTE(HttpStatus.CONFLICT, "BRD_409_01", "이미 투표에 참여하셨습니다."),
    VOTE_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "BRD_400_01", "투표 기간이 아닙니다."),
    NOT_HOUSEHOLDER(HttpStatus.FORBIDDEN, "BRD_403_03", "세대주만 투표에 참여할 수 있습니다."),
    INVALID_VOTE_DATE(HttpStatus.BAD_REQUEST, "BRD_400_02", "투표 종료일이 시작일보다 앞설 수 없습니다."),

    FILE_EMPTY(HttpStatus.BAD_REQUEST, "BRD_400_03", "파일이 비어 있습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "BRD_400_04", "파일 크기는 20MB를 초과할 수 없습니다."),
    FILE_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "BRD_400_05", "허용되지 않는 파일 형식입니다."),
    IMAGE_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "BRD_400_06", "이미지 파일만 업로드할 수 있습니다."),
    IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "BRD_400_07", "이미지 크기는 20MB를 초과할 수 없습니다."),
    FILE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BRD_500_01", "파일 저장 중 오류가 발생했습니다."),
    FILE_MOVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BRD_500_02", "파일 이동 중 오류가 발생했습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BRD_500_03", "파일 삭제 중 오류가 발생했습니다."),
    INVALID_FILE_PATH(HttpStatus.BAD_REQUEST, "BRD_400_08", "올바르지 않은 파일 경로입니다."),
    FILE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "BRD_403_04", "파일 접근 권한이 없습니다.");

    // HTTP 상태값
    private final HttpStatus status;

    // 서비스 코드
    private final String code;

    // 기본 메시지
    private final String message;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
