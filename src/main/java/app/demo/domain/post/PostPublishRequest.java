package app.demo.domain.post;

import jakarta.validation.constraints.NotBlank;

public record PostPublishRequest(
        @NotBlank String title,
        String content,
        String description
) {
}
