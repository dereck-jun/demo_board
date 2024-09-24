package app.demo.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    GUEST("ROLE_GUEST", "가입하지 않은 사용자"),
    USER("ROLE_USER", "가입한 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");
    
    private final String type;
    private final String description;

    // spring security 의 GrantedAuthority 로 변환
    public GrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority(this.type);
    }
}
