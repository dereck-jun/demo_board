package app.demo.exception;

import app.demo.exception.response.BaseResponse;
import io.jsonwebtoken.io.DeserializationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static app.demo.exception.response.BaseResponseStatus.*;

@Slf4j
@RestControllerAdvice
public class BaseControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<BaseResponse<String>> handleEntityNotFoundException(EntityNotFoundException enfe) {
        log.error("[EntityNotFoundException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(enfe), enfe.getMessage());
        String errorMessage = enfe.getMessage();
        return new ResponseEntity<>(new BaseResponse<>(ENTITY_NOT_FOUND, errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException msrpe) {
        log.error("[MissingServletRequestParameterException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(msrpe), msrpe.getMessage());
        String errorMessage = msrpe.getMessage();
        return new ResponseEntity<>(new BaseResponse<>(NOT_VALID_ERROR, errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<List<String>>> handleValidationExceptions(MethodArgumentNotValidException manve) {
        log.error("[MethodArgumentNotValidException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(manve), manve.getMessage());
        List<String> errorMessages = new ArrayList<>();
        manve.getBindingResult().getFieldErrors().forEach(error -> errorMessages.add(error.getDefaultMessage()));
        return new ResponseEntity<>(new BaseResponse<>(NOT_VALID_ERROR, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException matme) {
        log.error("[MethodArgumentTypeMismatchException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(matme), matme.getMessage());
        String errorMessage = matme.getMessage();
        return new ResponseEntity<>(new BaseResponse<>(TYPE_MISMATCH_ERROR, errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeserializationException.class)
    public ResponseEntity<BaseResponse<String>> handleDeserializationException(DeserializationException de) {
        log.error("[DeserializationException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(de), de.getMessage());

        String errorMessage = de.getMessage();
        return new ResponseEntity<>(new BaseResponse<>(TOKEN_DESERIALIZATION_ERROR, errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<BaseResponse<List<String>>> handleBindException(BindException be) {

        List<String> errorMessages = new ArrayList<>();
        List<FieldError> fieldErrors = be.getBindingResult().getFieldErrors();

        fieldErrors.forEach(error -> errorMessages.add(error.getDefaultMessage()));

        // NotBlank 에러 체크
        boolean hasNotBlankError = fieldErrors.stream()
                .anyMatch(error -> Objects.equals(error.getCode(), "NotBlank"));

        // NotBlank 에러가 있는 경우
        if (hasNotBlankError) {
            return new ResponseEntity<>(new BaseResponse<>(VALID_INPUT_BLANK, errorMessages), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new BaseResponse<>(REQUEST_ERROR, errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<String>> handleBaseException(BaseException e) {
        log.error("[Exception] handleCustomException throw CustomException: {}", e.status);

        BaseResponse<String> response = new BaseResponse<>(e.getStatus().toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
