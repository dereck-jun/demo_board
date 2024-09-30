package app.demo.domain.user;

import org.springframework.data.annotation.ReadOnlyProperty;

public record UserAuthenticationResponse(@ReadOnlyProperty String accessToken) {
}
