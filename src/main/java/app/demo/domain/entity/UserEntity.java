package app.demo.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;

@Entity
@Getter
@EqualsAndHashCode
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update user set deleted_date_time = current_timestamp where user_id = ?")
@SQLRestriction("deleted_date_time is null")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    @Setter(AccessLevel.PRIVATE)
    @NotBlank
    @Email
    private String email;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String phoneNumber;

    @Column
    private String description;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdDateTime;

    @Column
    private ZonedDateTime updatedDateTime;

    @Column
    private ZonedDateTime deletedDateTime;

    public static UserEntity of(String email, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword(password);
        return userEntity;
    }

    @PrePersist
    public void prePersist() {
        this.createdDateTime = ZonedDateTime.now();
        this.updatedDateTime = this.createdDateTime;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDateTime = ZonedDateTime.now();
    }
}

