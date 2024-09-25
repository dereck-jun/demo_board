package app.demo.exception.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BaseResponseStatus {

    /**
     * 1000: 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     *  2000: 요청 실패
     */
    REQUEST_ERROR(false, 2000, "잘못된 요청입니다."),

    USER_ALREADY_EXISTS_ERROR(false, 2400, "이미 존재하는 사용자입니다."),
    EMAIL_DUPLICATION_ERROR(false, 2401, "해당 이메일은 이미 사용 중입니다."),
    NICKNAME_DUPLICATION_ERROR(false, 2402, "해당 닉네임은 이미 사용 중입니다."),
    LOGIN_FAILED_ERROR(false, 2403, "이메일과 비밀번호를 확인해주세요."),

    NOT_VALID_ERROR(false, 2411, "검증에 실패했습니다. 양식에 맞추어 다시 작성해주세요."),
    VALID_INPUT_NULL(false, 2412, "입력되지 않은 값이 있습니다."),
    VALID_INPUT_BLANK(false, 2413, "입력되지 않은 값이 있습니다."),
    INVALID_URI_PATH(false, 2414, "URI 경로가 잘못되었습니다."),
    MISSING_PARAMETER_ERROR(false, 2415, "필수 요청 데이터가 누락되었습니다."),
    INVALID_AUTH_HEADER(false, 2416, "유효하지 않거나 잘못된 헤더입니다."),
    EXPIRED_JWT_TOKEN(false, 2417, "만료된 토큰입니다"),
    INVALID_JWT_TOKEN(false, 2418, "유효하지 않은 토큰입니다."),
    INVALID_USER_ROLE(false, 2419, "일반 사용자에 해당하는 권한이 없습니다."),

    TYPE_MISMATCH_ERROR(false, 2421, "요청 값이 잘못되었습니다."),
    HTTP_STATUS_NOT_SUPPORTED_ERROR(false, 2422, "요청이 지원되지 않거나, 잘못된 요청입니다."),


    /**
     * 3000: 응답 실패
     */
    RESPONSE_ERROR(false, 3000, "응답에 실패했습니다."),
    ENTITY_NOT_FOUND(false, 3401, "찾으려는 객체가 없습니다."),
    USER_NOT_FOUND(false, 3402, "찾으려는 유저가 없습니다."),
    ACCOUNT_DELETED_ERROR(false, 3403, "해당 계정은 삭제된 계정입니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;
}
