package uz.pdp.telegramevosbot.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

@Component
@ResponseStatus(HttpStatus.NOT_FOUND)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NotFoundException extends RuntimeException {
    private String message;
}
