package app.demo.controller;

import app.demo.domain.user.User;
import app.demo.domain.user.UserLoginRequestBody;
import app.demo.domain.user.UserRegisterRequestBody;
import app.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 01. 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegisterRequestBody userRegisterRequestBody) {
        User registeredUser = userService.register(userRegisterRequestBody);
        return ResponseEntity.ok(registeredUser);
    }

    /**
     * 02. 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody UserLoginRequestBody userLoginRequestBody) {
        User user = userService.login(userLoginRequestBody);
        return ResponseEntity.ok(user);
    }
}
