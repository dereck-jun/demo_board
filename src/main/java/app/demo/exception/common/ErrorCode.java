package app.demo.exception.common;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String name();
    HttpStatus getStatus();
    int getCode();
    String getMessage();
}
