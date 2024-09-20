package app.demo.exception.user;

import app.demo.exception.common.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserLoginFailedException extends RuntimeException {
    private final ErrorCode errorCode;
}
