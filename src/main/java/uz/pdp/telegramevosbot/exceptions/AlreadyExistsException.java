package uz.pdp.telegramevosbot.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ToString
@Component
@NoArgsConstructor
@AllArgsConstructor
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyExistsException extends RuntimeException {
    private String message;
}
