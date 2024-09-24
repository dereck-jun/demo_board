package app.demo.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegisterRequestBody(
        @NotBlank
        @Email(message = "이메일 형식이어야 합니다.")
        String email,

        @NotBlank
        @Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "최소 1개 이상의 대소문자, 숫자, 특수문자가 들어가야 합니다.")
        String password
) {
}
