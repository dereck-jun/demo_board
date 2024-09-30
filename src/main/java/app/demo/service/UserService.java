package app.demo.service;

import app.demo.domain.entity.UserEntity;
import app.demo.domain.user.*;
import app.demo.exception.BaseException;
import app.demo.repository.UserEntityRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

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

    // 03. 회원 프로필 확인
    public User getUserProfile(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

        return User.from(userEntity);
    }

    // 04. 회원 프로필 수정
    @Transactional
    public User updateUserProfile(Long userId, @RequestBody UserProfileRequest userProfileRequest, UserEntity currentUser) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

        // 인증 정보와 접근하려는 유저가 다름
        if (!userEntity.equals(currentUser)) {
            throw new BaseException(PRINCIPAL_MISMATCH_ERROR);
        }

        // 닉네임이 이전과 동일
        if (userProfileRequest.nickname().equals(userEntity.getNickname())) {
            throw new BaseException(NICKNAME_NOT_CHANGED_ERROR);
        }

        // 닉네임이 이미 존재
        if (userRepository.findByNickname(userProfileRequest.nickname()).isPresent()) {
            throw new BaseException(NICKNAME_DUPLICATION_ERROR);
        }

        userEntity.setNickname(userProfileRequest.nickname());
        userEntity.setPhoneNumber(userProfileRequest.phoneNumber());
        userEntity.setDescription(userProfileRequest.description());
        return User.from(userRepository.save(userEntity));
    }

    // 05. 회원 삭제
    @Transactional
    public void deleteUser(Long userId, UserEntity currentUser) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));

        if (!userEntity.equals(currentUser)) {
            throw new BaseException(PRINCIPAL_MISMATCH_ERROR);
        }

        if (userEntity.getUserStatus() == UserStatus.DELETED) {
            throw new BaseException(USER_ALREADY_DELETED_ERROR);
        }

        userRepository.delete(userEntity);
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