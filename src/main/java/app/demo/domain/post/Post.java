package app.demo.domain.post;

import app.demo.domain.entity.PostEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Post(
        String title,
        String content,
        String description,
        String author,
        PostStatus status,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime) {

    public static Post from(PostEntity entity) {
        return new Post(
                entity.getTitle(),
                entity.getContent(),
                entity.getDescription(),
                entity.getAuthor(),
                entity.getStatus(),
                entity.getCreatedDateTime(),
                entity.getUpdatedDateTime()
        );
    }
}
