package app.demo.controller;

import app.demo.domain.user.User;
import app.demo.domain.user.UserAuthenticationResponse;
import app.demo.domain.user.UserLoginRequestBody;
import app.demo.domain.user.UserRegisterRequestBody;
import app.demo.exception.BaseException;
import app.demo.exception.response.BaseResponse;
import app.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static app.demo.exception.response.BaseResponseStatus.EMAIL_DUPLICATION_ERROR;
import static app.demo.exception.response.BaseResponseStatus.LOGIN_FAILED_ERROR;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 01. 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<User>> register(@Valid @RequestBody UserRegisterRequestBody userRegisterRequestBody) {
        try {
            User registeredUser = userService.register(userRegisterRequestBody);
            BaseResponse<User> response = new BaseResponse<>(registeredUser);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (BaseException e) {
            BaseResponse<User> response = new BaseResponse<>(EMAIL_DUPLICATION_ERROR);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    /**
     * 02. 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<UserAuthenticationResponse>> login(@Valid @RequestBody UserLoginRequestBody userLoginRequestBody) {
        try {
            UserAuthenticationResponse loginSuccessToken = userService.login(userLoginRequestBody);
            BaseResponse<UserAuthenticationResponse> response = new BaseResponse<>(loginSuccessToken);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BaseException e) {
            BaseResponse<UserAuthenticationResponse> response = new BaseResponse<>(LOGIN_FAILED_ERROR);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
