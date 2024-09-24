package app.demo.config;

import app.demo.exception.BaseException;
import app.demo.exception.response.BaseResponseStatus;
import app.demo.service.JwtService;
import app.demo.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String BEARER_PREFIX = "Bearer ";

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // SecurityContext 의 세부 내용은 SecurityContextHolder 에 저장되기 때문에 해당 클래스에서 컨텍스트를 가져옴. 없으면 null
        SecurityContext securityContext = SecurityContextHolder.getContext();

        // Header 가 비어있거나, BEARER_PREFIX 값으로 시작하지 않으면
        if (ObjectUtils.isEmpty(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_HEADER);
        }

        // BEARER_PREFIX 이후 값이 비어있다면 (=토큰이 없음)
        String token = authorization.substring(BEARER_PREFIX.length());
        if (ObjectUtils.isEmpty(token)) {
            throw new BaseException(BaseResponseStatus.INVALID_JWT_TOKEN);
        }

        if (!ObjectUtils.isEmpty(authorization) && !ObjectUtils.isEmpty(token) && authorization.startsWith(BEARER_PREFIX) && securityContext.getAuthentication() == null) {
            try {
                String userEmail = jwtService.getEmailFromToken(token);
                UserDetails loadUserEmail = userService.loadUserByUsername(userEmail);

                // 사용자 이름과 비밀번호, UserRole 을 받아서 인증 정보 생성 (사용자 이름, 비밀번호, authorities(=GrantedAuthority Collection))
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loadUserEmail, null, loadUserEmail.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);

                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException e) {
                throw new BaseException(BaseResponseStatus.EXPIRED_JWT_TOKEN);
            } catch (JwtException e) {
                throw new BaseException(BaseResponseStatus.INVALID_JWT_TOKEN);
            }
        }
    }
}
