package app.demo.exception;

import app.demo.exception.response.BaseResponse;
import app.demo.exception.response.BaseResponseStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

import static app.demo.exception.response.BaseResponseStatus.*;

@Slf4j
@RestControllerAdvice(basePackages = "app.demo")
public class BaseControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    public BaseResponse<BaseResponseStatus> handleEntityNotFoundException(EntityNotFoundException e) {
//        log.error("[EntityNotFoundException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
        return new BaseResponse<>(ENTITY_NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<BaseResponseStatus> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        log.error("[MethodArgumentNotValidException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
        return new BaseResponse<>(NOT_VALID_ERROR);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BaseResponse<BaseResponseStatus> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
//        log.error("[MissingServletRequestParameterException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
        return new BaseResponse<>(NOT_VALID_ERROR);
    }

    @ExceptionHandler(BindException.class)
    public BaseResponse<BaseResponseStatus> handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String bindCode = Objects.requireNonNull(bindingResult.getFieldError()).getCode();

        switch (bindCode) {
            case "NotBlank" -> {
                return new BaseResponse<>(VALID_INPUT_BLANK);
            }
        }
        return new BaseResponse<>(INVALID_URI_PATH);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public BaseResponse<BaseResponseStatus> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return new BaseResponse<>(TYPE_MISMATCH_ERROR);
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<BaseResponseStatus> handleBaseException(BaseException e) {
        log.error("[Exception] handleCustomException throw CustomException: {}", e.status);
        return new BaseResponse<>(e.status);
    }
}
