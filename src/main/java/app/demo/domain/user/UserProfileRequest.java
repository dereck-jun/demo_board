package app.demo.domain.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserProfileRequest(
        @Size(min = 2, max = 12, message = "닉네임은 최소 2자리, 최대 12자리까지 가능합니다.")
        String nickname,

        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "하이픈을 포함한 전화번호 형식을 갖춰야 합니다.")
        String phoneNumber,

        @Size(max = 250, message = "설명은 250자를 넘어설 수 없습니다.")
        String description) {
}
