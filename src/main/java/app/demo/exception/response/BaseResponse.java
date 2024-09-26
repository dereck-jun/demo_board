package app.demo.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@JsonPropertyOrder({"isSuccess", "status", "message", "result"})
public class BaseResponse<T> {

    private final boolean isSuccess;
    private final HttpStatus status;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 요청 성공 시
    public BaseResponse(T result) {
        this.isSuccess = BaseResponseStatus.SUCCESS.isSuccess();
        this.status = BaseResponseStatus.SUCCESS.getStatus();
        this.message = BaseResponseStatus.SUCCESS.getMessage();
        this.result = result;
    }

    // 요청 실패 시
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.status = status.getStatus();
        this.message = status.getMessage();
    }
    
    // 요청 실패 시 defaultMessages 까지 출력
    public BaseResponse(BaseResponseStatus status, T errorMessages) {
        this.isSuccess = status.isSuccess();
        this.status = status.getStatus();
        this.message = status.getMessage();
        this.result = errorMessages;
    }
}
