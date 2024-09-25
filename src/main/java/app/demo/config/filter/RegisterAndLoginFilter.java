package app.demo.config.filter;

import app.demo.exception.response.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static app.demo.exception.response.BaseResponseStatus.REQUEST_ERROR;

@Component
public class RegisterAndLoginFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isRegisterRequestOrLogin(request.getRequestURI())) {
            filterChain.doFilter(request, response);
        } else {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(new BaseResponse<>(REQUEST_ERROR)));
        }
    }

    private boolean isRegisterRequestOrLogin(String uri) {
        final String START_URI = "/api/v1/users/";
        final String REGISTER_URI = START_URI + "register";
        final String LOGIN_URI = START_URI + "login";

        return uri.startsWith(REGISTER_URI) || uri.startsWith(LOGIN_URI);
    }
}
