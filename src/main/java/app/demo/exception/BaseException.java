package app.demo.exception;

import app.demo.exception.response.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    final BaseResponseStatus status;
}
