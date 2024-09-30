package app.demo.domain.entity;

import app.demo.domain.user.UserRole;
import app.demo.domain.user.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@EqualsAndHashCode(callSuper = false)
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update user set deleted_date_time = current_timestamp where user_id = ?")
@SQLRestriction("deleted_date_time is null")
public class UserEntity extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, unique = true)
    private String email;

    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(unique = true)
    private String nickname;

    @Setter
    @Column
    private String phoneNumber;

    @Setter
    @Column
    private String description;

    @Setter(AccessLevel.PRIVATE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus userStatus;

    @Setter(AccessLevel.PRIVATE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    public static UserEntity of(String email, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword(password);
        userEntity.generateDefaultNickname();
        userEntity.setUserStatus(UserStatus.ACTIVE);
        userEntity.setUserRole(UserRole.USER);
        return userEntity;
    }

    private void generateDefaultNickname() {
        final String PREFIX = "user_";
        String randomNickname = UUID.randomUUID().toString().substring(0, 8);

        this.nickname = randomNickname;
        while (this.nickname.equals(randomNickname)) {
            randomNickname = UUID.randomUUID().toString().substring(0, 8);
        }

        this.nickname = PREFIX + randomNickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(this.userRole.toAuthority());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return Objects.requireNonNull(this.getUserStatus()) == UserStatus.ACTIVE;
    }
}

