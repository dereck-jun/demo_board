package app.demo.config.filter;

import app.demo.exception.BaseException;
import app.demo.exception.response.BaseResponse;
import app.demo.exception.response.BaseResponseStatus;
import app.demo.service.JwtService;
import app.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

import static app.demo.exception.response.BaseResponseStatus.*;
import static jakarta.servlet.http.HttpServletResponse.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    private static void setErrorResponse(HttpServletResponse response, BaseResponseStatus baseResponseStatus) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        switch (baseResponseStatus.getStatus()) {
            case UNAUTHORIZED -> response.setStatus(SC_UNAUTHORIZED);
            case FORBIDDEN -> response.setStatus(SC_FORBIDDEN);
            case NOT_FOUND -> response.setStatus(SC_NOT_FOUND);
            case BAD_REQUEST -> response.setStatus(SC_BAD_REQUEST);

            default -> response.setStatus(SC_INTERNAL_SERVER_ERROR);
        }
        response.setCharacterEncoding("UTF-8");

        BaseResponse<Object> errorResponse = new BaseResponse<>(baseResponseStatus);
        String responseJson = new ObjectMapper().writeValueAsString(errorResponse);
        response.getWriter().write(responseJson);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isRegisterRequestOrLogin(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String BEARER_PREFIX = "Bearer ";

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // SecurityContext 의 세부 내용은 SecurityContextHolder 에 저장되기 때문에 해당 클래스에서 컨텍스트를 가져옴. 없으면 null
        SecurityContext securityContext = SecurityContextHolder.getContext();

        // Header 가 비어있거나, BEARER_PREFIX 값으로 시작하지 않으면
        if (ObjectUtils.isEmpty(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            setErrorResponse(response, INVALID_AUTH_HEADER);
            return;
        }

        // BEARER_PREFIX 이후 값이 비어있다면 (=토큰이 없음)
        String token = authorization.substring(BEARER_PREFIX.length());
        if (ObjectUtils.isEmpty(token)) {
            setErrorResponse(response, INVALID_JWT_TOKEN);
            return;
        }

        if (!ObjectUtils.isEmpty(authorization) && !ObjectUtils.isEmpty(token) && authorization.startsWith(BEARER_PREFIX) && securityContext.getAuthentication() == null) {
            try {
                String userEmail = jwtService.getEmailFromToken(token);
                UserDetails loadUserEmail = userService.loadUserByUsername(userEmail);
                Collection<? extends GrantedAuthority> authorities = loadUserEmail.getAuthorities();
                boolean hasUserRole = authorities.stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

                if (!hasUserRole) {
                    throw new BaseException(INVALID_USER_ROLE);
                }

                // 사용자 이름과 비밀번호, UserRole 을 받아서 인증 정보 생성 (사용자 이름, 비밀번호, authorities(=GrantedAuthority Collection))
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loadUserEmail, null, loadUserEmail.getAuthorities());
                authenticationToken.setDetails(
                        // 현재 요청 정보를 기반으로 인증의 추가적인 세부 정보 설정
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // SecurityContext 에 Authentication 객체를 설정하여 인증된 사용자로 간주되도록 함
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);

                filterChain.doFilter(request, response);
            } catch (BaseException e) {
                setErrorResponse(response, e.getStatus());
            } catch (ExpiredJwtException e) {
                setErrorResponse(response, EXPIRED_JWT_TOKEN);
            } catch (JwtException e) {
                log.error("[JwtException] message: {}", e.getMessage());
                setErrorResponse(response, INVALID_JWT_TOKEN);
            }
        }
    }

    private boolean isRegisterRequestOrLogin(String uri) {
        final String START_URI = "/api/v1/users/";
        final String REGISTER_URI = START_URI + "register";
        final String LOGIN_URI = START_URI + "login";

        return uri.startsWith(REGISTER_URI) || uri.startsWith(LOGIN_URI);
    }
}
