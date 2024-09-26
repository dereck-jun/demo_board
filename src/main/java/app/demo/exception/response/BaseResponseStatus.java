package app.demo.exception.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum BaseResponseStatus {

    /**
     * 1000: 요청 성공
     */
    SUCCESS(true, OK, "요청에 성공하였습니다."),

    /**
     *  2000: 요청 실패
     */
    REQUEST_ERROR(false, BAD_REQUEST, "잘못된 요청입니다."),

    USER_ALREADY_EXISTS_ERROR(false, CONFLICT, "이미 존재하는 사용자입니다."),
    LOGIN_FAILED_ERROR(false, UNAUTHORIZED, "이메일과 비밀번호를 확인해주세요."),
    EMAIL_DUPLICATION_ERROR(false, CONFLICT, "해당 이메일은 이미 사용 중입니다."),
    NICKNAME_DUPLICATION_ERROR(false, CONFLICT, "해당 닉네임은 이미 사용 중입니다."),
    NICKNAME_NOT_CHANGED_ERROR(false, BAD_REQUEST, "이전 닉네임과 동일할 수 없습니다."),
    INVALID_PHONE_NUMBER_ERROR(false, BAD_REQUEST, "유효하지 않은 휴대폰 번호 입니다."),

    VALID_INPUT_NULL(false, BAD_REQUEST, "입력되지 않은 값이 있습니다."),
    VALID_INPUT_BLANK(false, BAD_REQUEST, "입력되지 않은 값이 있습니다."),
    NOT_VALID_ERROR(false, BAD_REQUEST, "검증에 실패했습니다. 양식에 맞추어 다시 작성해주세요."),
    MISSING_PARAMETER_ERROR(false, BAD_REQUEST, "필수 요청 데이터가 누락되었습니다."),

    INVALID_URI_PATH(false, NOT_FOUND, "URI 경로가 잘못되었습니다."),
    INVALID_AUTH_HEADER(false, UNAUTHORIZED, "유효하지 않거나 잘못된 Header 입니다."),
    EXPIRED_JWT_TOKEN(false, UNAUTHORIZED, "만료된 토큰입니다"),
    INVALID_JWT_TOKEN(false, UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_USER_ROLE(false, FORBIDDEN, "일반 사용자에 해당하는 권한이 없습니다."),
    USER_ALREADY_DELETED_ERROR(false, NOT_FOUND, "이미 삭제된 유저입니다."),

    TYPE_MISMATCH_ERROR(false, BAD_REQUEST, "요청 값이 잘못되었습니다."),
    USER_NOT_ALLOWED_ERROR(false, FORBIDDEN, "해당 유저에 대한 권한이 존재하지 않습니다."),
    TOKEN_DESERIALIZATION_ERROR(false, INTERNAL_SERVER_ERROR, "토큰을 해석하는 중 에러가 발생했습니다."),  // 서버 에러
    HTTP_STATUS_NOT_SUPPORTED_ERROR(false, NOT_IMPLEMENTED, "요청이 지원되지 않거나, 잘못된 요청입니다."),  // 지원되지 않는 요청

    /**
     * 3000: 응답 실패
     */
    RESPONSE_ERROR(false, INTERNAL_SERVER_ERROR, "응답에 실패했습니다."),
    ENTITY_NOT_FOUND(false, NOT_FOUND, "찾으려는 객체가 없습니다."),  // 객체를 찾을 수 없음
    USER_NOT_FOUND(false, NOT_FOUND, "찾으려는 유저가 없습니다."),  // 유저를 찾을 수 없음
    ACCOUNT_DELETED_ERROR(false, GONE, "해당 계정은 삭제된 계정입니다.");

    private final boolean isSuccess;
    private final HttpStatus status;
    private final String message;
}
