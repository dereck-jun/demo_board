package app.demo.service;

import app.demo.domain.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.DeserializationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.util.Date;

@Slf4j
@Service
@Transactional
public class JwtService {

    private static final KeyPair KEY_PAIR = Jwts.SIG.ES256.keyPair().build();

    public String generateAccessToken(UserEntity userEntity) {
        return generateToken(userEntity.getEmail(), userEntity.getUserStatus().toString(), userEntity.getUserRole().toString());
    }

    public String getEmailFromToken(String accessToken) {
        return getSubject(accessToken);
    }

    public String getStatusFromToken(String accessToken) {
        return getUserStatus(accessToken);
    }

    public String getRoleFromToken(String accessToken) {
        return getUserRole(accessToken);
    }

    /**
     * JWT 토큰 생성
     * @param subject 유저 이메일
     * @param userStatus 유저 상태(ACTIVE, DELETED)
     * @param userRole 유저 권한
     * @return JWT Token
     */
    private String generateToken(String subject, String userStatus, String userRole) {
        // Jwt 생성 시간과 만료 시간 설정을 위한 Date 객체 생성 후 exp 1시간 설정
        Date now = new Date();
        Date exp = new Date(now.getTime() + (1000 * 60 * 60));

        // Jwt 토큰 직렬화 (=compact)
        // subject 외부에서 받아오고, 양방향 알고리즘이기 때문에 private KEY_PAIR 사용해서 싸인만들고, 생성 시간과 만료 시간 설정
        // 이때 KEY_PAIR.getPrivate() -> KEY_PAIR.getPublic() 변경 시 InvalidKeyException 발생
        return Jwts.builder()
                .subject(subject)
                .claim("status", userStatus)
                .claim("role", userRole)
                .signWith(KEY_PAIR.getPrivate())
                .issuedAt(now)
                .expiration(exp)
                .compact();
    }

    /**
     * JWT에서 subject를 가져옴
     * @param token
     * @return JWT에 사용된 subject(Email)
     */
    private String getSubject(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY_PAIR.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (DeserializationException de){
            log.error("[DeserializationException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(de), de.getMessage());
            throw de;
        } catch (JwtException je) {
            log.error("[Jwt] subject 정보를 가져오는데 실패했습니다. cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(je), je.getMessage());
            throw je;
        }
    }

    private String getUserStatus(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY_PAIR.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("status", String.class);
        } catch (JwtException e) {
            log.error("[Jwt] status 정보를 가져오는데 실패했습니다. cause: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getCause());
            throw e;
        }
    }

    private String getUserRole(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY_PAIR.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("role", String.class);
        } catch (JwtException e) {
            log.error("[Jwt] role 정보를 가져오는데 실패했습니다. cause: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getCause());
            throw e;
        }
    }
}
