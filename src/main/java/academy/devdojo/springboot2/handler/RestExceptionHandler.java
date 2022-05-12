package academy.devdojo.springboot2.handler;

import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.exception.BadRequestExceptionDetails;
import academy.devdojo.springboot2.exception.ExceptionDetails;
import academy.devdojo.springboot2.exception.ValidationExceptionDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<BadRequestExceptionDetails> handleBadRequestException(BadRequestException exception) {
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

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ExceptionDetails details = ExceptionDetails.builder()
                                                   .title(exception.getCause().getMessage())
                                                   .details(exception.getMessage())
                                                   .developerMessage(exception.getClass().getName())
                                                   .code(status.value())
                                                   .timestamp(LocalDateTime.now())
                                                   .build();

        return new ResponseEntity<>(details, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<FieldError> fieldErrors   = exception.getBindingResult().getFieldErrors();
        String           fields        = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
        String           fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));

        ValidationExceptionDetails details =
                ValidationExceptionDetails.builder()
                                          .title("Bad Request Exception")
                                          .details(exception.getMessage())
                                          .developerMessage(exception.getClass().getName())
                                          .code(status.value())
                                          .timestamp(LocalDateTime.now())
                                          .fields(fields)
                                          .fieldsMessage(fieldsMessage)
                                          .build();

        return new ResponseEntity<>(details, status);
    }
}
