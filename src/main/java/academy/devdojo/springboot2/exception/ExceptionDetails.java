package academy.devdojo.springboot2.exception;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ExceptionDetails {
    protected String        title;
    protected String        details;
    protected String        developerMessage;
    protected int           code;
    protected LocalDateTime timestamp;
}
