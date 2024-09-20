package app.demo.domain.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestBody(
        @NotBlank String email,
        @NotBlank String password
) {
}
