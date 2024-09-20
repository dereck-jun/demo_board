package app.demo.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterRequestBody(
        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 8, max = 20)
        String password
) {
}
