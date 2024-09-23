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
    REQUEST_ERROR(false, 2000, "입력 값을 확인해주세요."),
    EMAIL_DUPLICATION_ERROR(false, 2100, "해당 이메일은 이미 사용 중입니다."),
    NOT_VALID_ERROR(false, 2401, "유효성 검증에 실패했습니다."),
    VALID_INPUT_NULL(false, 2031, "입력되지 않은 값이 있습니다."),
    VALID_INPUT_BLANK(false, 2032, "입력되지 않은 값이 있습니다."),
    INVALID_URI_PATH(false, 2406, "URI 경로가 잘못되었습니다."),
    MISSING_PARAMETER_ERROR(false, 2402, "필수 요청 데이터가 누락되었습니다."),
    HTTP_STATUS_NOT_SUPPORTED_ERROR(false, 2901, "요청이 지원되지 않거나, 잘못된 요청입니다."),
    TYPE_MISMATCH_ERROR(false, 2405, "요청 값이 잘못되었습니다."),


    /**
     * 3000: 응답 실패
     */
    RESPONSE_ERROR(false, 3000, "응답에 실패했습니다."),
    ENTITY_NOT_FOUND(false, 3404, "찾으려는 객체가 없습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;
}
