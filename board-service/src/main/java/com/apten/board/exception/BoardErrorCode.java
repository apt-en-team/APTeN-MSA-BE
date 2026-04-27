package com.apten.board.exception;

import com.apten.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 게시판 서비스 에러코드
@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode {

    // 잘못된 요청값이다.
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "BRD_400_01", "잘못된 요청 파라미터입니다."),

    // 인증이 필요한 요청이다.
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "BRD_401_01", "인증이 필요합니다."),

    // 토큰 값이 잘못되었다.
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "BRD_401_02", "유효하지 않은 토큰입니다."),

    // 토큰 유효시간이 끝났다.
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "BRD_401_03", "토큰이 만료되었습니다."),

    // 사용자를 찾을 수 없다.
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "BRD_404_01", "사용자를 찾을 수 없습니다."),

    // 게시글을 찾을 수 없다.
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "BRD_404_02", "게시글을 찾을 수 없습니다."),

    // 게시글 작성자가 아니다.
    POST_WRITER_MISMATCH(HttpStatus.FORBIDDEN, "BRD_403_01", "게시글 작성자만 수정 또는 삭제할 수 있습니다."),

    // 댓글을 찾을 수 없다.
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "BRD_404_03", "댓글을 찾을 수 없습니다."),

    // 댓글 작성자가 아니다.
    COMMENT_WRITER_MISMATCH(HttpStatus.FORBIDDEN, "BRD_403_02", "댓글 작성자만 수정 또는 삭제할 수 있습니다."),

    // 공지를 찾을 수 없다.
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "BRD_404_04", "공지를 찾을 수 없습니다."),

    // 투표를 찾을 수 없다.
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "BRD_404_05", "투표를 찾을 수 없습니다."),

    // 투표 기간이 유효하지 않다.
    VOTE_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "BRD_400_02", "투표 기간이 아닙니다."),

    // 세대주만 투표할 수 있다.
    VOTE_HEAD_ONLY(HttpStatus.FORBIDDEN, "BRD_403_03", "세대주만 투표할 수 있습니다."),

    // 세대당 중복 투표이다.
    DUPLICATE_VOTE(HttpStatus.CONFLICT, "BRD_409_01", "이미 투표에 참여했습니다."),

    // 투표 시작일과 종료일이 올바르지 않다.
    INVALID_VOTE_DATE(HttpStatus.BAD_REQUEST, "BRD_400_03", "투표 시작일과 종료일이 올바르지 않습니다."),

    // 현재 상태에서 허용되지 않는 투표 요청이다.
    INVALID_VOTE_STATUS(HttpStatus.BAD_REQUEST, "BRD_400_04", "현재 투표 상태에서는 요청을 처리할 수 없습니다."),

    // 업로드 파일이 비어 있다.
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "BRD_400_05", "파일이 비어 있습니다."),

    // 허용 용량을 넘겼다.
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "BRD_400_06", "파일 크기는 20MB를 초과할 수 없습니다."),

    // 허용되지 않는 파일 형식이다.
    FILE_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "BRD_400_07", "허용되지 않는 파일 형식입니다."),

    // 파일 메타데이터 저장 또는 실제 저장 중 오류가 났다.
    FILE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BRD_500_01", "파일 저장 중 오류가 발생했습니다."),

    // Outbox 이벤트 저장에 실패했다.
    EVENT_PUBLISH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BRD_500_02", "이벤트 발행 데이터 저장에 실패했습니다.");

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
