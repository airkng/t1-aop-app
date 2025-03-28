package t1.edu.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionMessage handleNotFound(NotFoundException e) {
        return new ExceptionMessage()
                .setCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .setMessage(e.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionMessage handleNotFound(AlreadyExistsException e) {
        return new ExceptionMessage()
                .setCode(String.valueOf(HttpStatus.CONFLICT.value()))
                .setMessage(e.getMessage());
    }
}
