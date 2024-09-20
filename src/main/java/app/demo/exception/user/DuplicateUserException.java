package app.demo.exception.user;

import lombok.Getter;
import org.springframework.dao.DuplicateKeyException;

@Getter
public class DuplicateUserException extends DuplicateKeyException {
    public DuplicateUserException(String message) {
        super(message);
    }
}
