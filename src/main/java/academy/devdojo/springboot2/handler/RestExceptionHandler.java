package academy.devdojo.springboot2.handler;

import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.exception.BadRequestExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<BadRequestExceptionDetails> handlerBadRequestException(BadRequestException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        BadRequestExceptionDetails details =
                BadRequestExceptionDetails.builder()
                                          .title("Bad Request Exception")
                                          .details(exception.getMessage())
                                          .developerMessage(exception.getClass().getName())
                                          .code(status.value())
                                          .timestamp(LocalDateTime.now())
                                          .build();

        return new ResponseEntity<>(details, status);
    }
}
