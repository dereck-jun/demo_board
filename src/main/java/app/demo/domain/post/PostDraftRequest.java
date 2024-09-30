package app.demo.domain.post;

import jakarta.validation.constraints.NotBlank;

public record PostDraftRequest(
        @NotBlank String title,
        String content,
        String description
) {
}
