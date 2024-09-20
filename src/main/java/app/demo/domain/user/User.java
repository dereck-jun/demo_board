package app.demo.domain.user;

import app.demo.domain.entity.UserEntity;

import java.time.ZonedDateTime;

public record User(
    String email,
    String nickname,
    String phoneNumber,
    String description,
    ZonedDateTime createdDateTime,
    ZonedDateTime updatedDateTime) {

    public static User from(UserEntity entity) {
        return new User(
                entity.getEmail(),
                entity.getNickname(),
                entity.getPhoneNumber(),
                entity.getDescription(),
                entity.getCreatedDateTime(),
                entity.getUpdatedDateTime()
        );
    }
}
