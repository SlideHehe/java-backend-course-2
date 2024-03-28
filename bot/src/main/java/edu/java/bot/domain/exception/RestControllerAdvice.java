package edu.java.bot.domain.exception;

import edu.java.bot.domain.exception.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ApiErrorResponse handleNotValidArguments(MethodArgumentNotValidException exception) {
        return ApiErrorResponse.create(
            "Некорректные параметры запроса",
            HttpStatus.BAD_REQUEST.toString(),
            exception
        );
    }
}
