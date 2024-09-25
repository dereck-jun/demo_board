package app.demo.service;

import app.demo.domain.entity.UserEntity;
import app.demo.domain.user.User;
import app.demo.domain.user.UserAuthenticationResponse;
import app.demo.domain.user.UserLoginRequestBody;
import app.demo.domain.user.UserRegisterRequestBody;
import app.demo.exception.BaseException;
import app.demo.exception.response.BaseResponseStatus;
import app.demo.repository.UserEntityRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static app.demo.exception.response.BaseResponseStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserEntityRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 01. 회원가입
    @Transactional
    public User register(@Valid UserRegisterRequestBody userRegisterRequestBody) {
        userRepository.findByEmail(userRegisterRequestBody.email()).ifPresent(user -> {
            throw new BaseException(EMAIL_DUPLICATION_ERROR);
        });

        // UserEntity.of 메서드로 String 타입의 email, password 를 UserEntity 타입으로 바꿈
        return User.from(userRepository.save(UserEntity.of(userRegisterRequestBody.email(), passwordEncoder.encode(userRegisterRequestBody.password()))));
    }
    
    // 02. 로그인 & 인증 정보 부여
    public UserAuthenticationResponse login(@Valid UserLoginRequestBody userLoginRequestBody) {
        UserEntity userEntity = userRepository.findByEmail(userLoginRequestBody.email())
                .orElseThrow(() -> new BaseException(LOGIN_FAILED_ERROR));

        if (passwordEncoder.matches(userLoginRequestBody.password(), userEntity.getPassword())) {
            String accessToken = jwtService.generateAccessToken(userEntity);
            return new UserAuthenticationResponse(accessToken);
        } else {
            throw new BaseException(LOGIN_FAILED_ERROR);
        }
    }

    
    // UserDetailsService 구현
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

        if (!userEntity.isEnabled()) {
            throw new BaseException(ACCOUNT_DELETED_ERROR);
        }
        return userEntity;
    }
}