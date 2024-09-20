package app.demo.exception.user;

import app.demo.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, 404, "존재하지 않는 사용자입니다."),
    MEMBER_ALREADY_EXISTS_ERROR(HttpStatus.CONFLICT, 409, "이미 존재하는 회원입니다."),
    UNAUTHENTICATED_USER_ERROR(HttpStatus.UNAUTHORIZED, 401, "로그인이 필요합니다."),
    UNAUTHORIZED_USER_ERROR(HttpStatus.FORBIDDEN, 403, "권한이 없는 사용자입니다.");

    private final HttpStatus status;
    private final int code;
    private final String message;
}
