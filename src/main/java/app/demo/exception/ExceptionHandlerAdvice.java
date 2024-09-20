package app.demo.exception;

import app.demo.exception.common.CommonErrorCode;
import app.demo.exception.common.ErrorCode;
import app.demo.exception.common.ErrorResponse;
import app.demo.exception.common.RestApiException;
import app.demo.exception.user.DuplicateUserException;
import app.demo.exception.user.UserErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    // 모든 에러 -> 하위 에러에서 못받을 때
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        // NestedExceptionUtils.getMostSpecificCause() -> 가장 구체적인 원인을 찾아서 반환
        log.error("[Exception] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());

        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<ErrorResponse> handleRestApiException(RestApiException e) {
        log.error("[SystemException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());

        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    // 메서드가 잘못되었거나 부적합한 인수를 전달했을 경우 -> 필수 파라미터 없을 시
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("[IllegalArgumentException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());

        ErrorCode errorCode = CommonErrorCode.ILLEGAL_ARGUMENT_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    // @Valid 유효성 검사에서 예외가 발생했을 떄 -> requestBody에 잘못 들어왔을 때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("[MethodArgumentNotValidException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());

        ErrorCode errorCode = CommonErrorCode.ILLEGAL_ARGUMENT_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    // 잘못된 포맷 요청 -> Json 이외의 형태로 전송
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("[HttpMessageNotReadableException] cause: {}, message:{}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());

        ErrorCode errorCode = CommonErrorCode.INVALID_FORMAT_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    // 중복 회원 처리
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientException(DuplicateUserException e) {
        log.error("[DuplicateUserException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());

        ErrorCode errorCode = UserErrorCode.MEMBER_ALREADY_EXISTS_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    // 회원을 찾을 수 없음
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("[EntityNotFoundException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());

        ErrorCode errorCode = UserErrorCode.MEMBER_NOT_FOUND_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }
}
