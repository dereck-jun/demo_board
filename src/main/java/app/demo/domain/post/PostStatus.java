package app.demo.domain.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostStatus {
    DRAFT("임시 저장", false, true),
    PUBLISHED("발행", true, true),
    DELETED("게시글 삭제", false, false);

    private final String description;
    private final boolean visible;
    private final boolean modifiable;
}
