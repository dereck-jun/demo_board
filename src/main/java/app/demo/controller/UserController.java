package app.demo.controller;

import app.demo.domain.entity.UserEntity;
import app.demo.domain.user.*;
import app.demo.exception.BaseException;
import app.demo.exception.response.BaseResponse;
import app.demo.exception.response.BaseResponseStatus;
import app.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static app.demo.exception.response.BaseResponseStatus.*;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private static <T> ResponseEntity<BaseResponse<T>> responseErrorStatusToJson(BaseException e) {
        BaseResponseStatus status = e.getStatus();
        return switch (status) {
            case USER_NOT_FOUND ->
                    new ResponseEntity<>(new BaseResponse<>(USER_NOT_FOUND), status. getStatus());
            case USER_NOT_ALLOWED_ERROR ->
                    new ResponseEntity<>(new BaseResponse<>(USER_NOT_ALLOWED_ERROR), status. getStatus());
            case USER_ALREADY_DELETED_ERROR ->
                    new ResponseEntity<>(new BaseResponse<>(USER_ALREADY_DELETED_ERROR), status. getStatus());
            case INVALID_AUTH_HEADER ->
                    new ResponseEntity<>(new BaseResponse<>(INVALID_AUTH_HEADER), status. getStatus());
            case INVALID_USER_ROLE ->
                    new ResponseEntity<>(new BaseResponse<>(INVALID_USER_ROLE), status. getStatus());
            case EXPIRED_JWT_TOKEN ->
                    new ResponseEntity<>(new BaseResponse<>(EXPIRED_JWT_TOKEN), status. getStatus());
            case INVALID_JWT_TOKEN ->
                    new ResponseEntity<>(new BaseResponse<>(INVALID_JWT_TOKEN), status. getStatus());
            case EMAIL_DUPLICATION_ERROR ->
                    new ResponseEntity<>(new BaseResponse<>(EMAIL_DUPLICATION_ERROR), status. getStatus());
            case LOGIN_FAILED_ERROR ->
                new ResponseEntity<>(new BaseResponse<>(LOGIN_FAILED_ERROR), status. getStatus());

            default -> throw e;
        };
    }

    /**
     * 01. 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<User>> register(@Valid @RequestBody UserRegisterRequestBody userRegisterRequestBody) {
        try {
            User registeredUser = userService.register(userRegisterRequestBody);
            return new ResponseEntity<>(new BaseResponse<>(registeredUser), CREATED);
        } catch (BaseException e) {
            return responseErrorStatusToJson(e);
        }
    }

    /**
     * 02. 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<UserAuthenticationResponse>> login(@Valid @RequestBody UserLoginRequestBody userLoginRequestBody) {
        try {
            UserAuthenticationResponse loginSuccessToken = userService.login(userLoginRequestBody);
            return new ResponseEntity<>(new BaseResponse<>(loginSuccessToken), OK);
        } catch (BaseException e) {
            return responseErrorStatusToJson(e);
        }
    }

    /**
     * 03. 회원 프로필 확인
     */
    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<User>> getUserProfile(@PathVariable Long userId) {
        try {
            User userProfile = userService.getUserProfile(userId);
            return new ResponseEntity<>(new BaseResponse<>(userProfile), OK);
        } catch (BaseException e) {
            return responseErrorStatusToJson(e);
        }
    }

    /**
     * 04. 회원 프로필 수정
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<BaseResponse<User>> updateUserProfile(@PathVariable Long userId, @Valid @RequestBody UserProfileRequest userProfileRequest, Authentication authentication) {
        try {
            User updatedUser = userService.updateUserProfile(userId, userProfileRequest, (UserEntity) authentication.getPrincipal());
            return new ResponseEntity<>(new BaseResponse<>(updatedUser), OK);
        } catch (BaseException e) {
            return responseErrorStatusToJson(e);
        }
    }

    /**
     * 05. 회원 삭제
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable Long userId, Authentication authentication) {
        try {
            userService.deleteUser(userId, (UserEntity) authentication.getPrincipal());
            return new ResponseEntity<>(new BaseResponse<>(SUCCESS), OK);
        } catch (BaseException e) {
            return responseErrorStatusToJson(e);
        }
    }
}
