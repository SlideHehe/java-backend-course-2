package edu.java.bot.api.exception;

import edu.java.bot.api.updates.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleNotValidArguments(MethodArgumentNotValidException exception) {
        return new ApiErrorResponse(
                "Некорректные параметры запроса",
                HttpStatus.BAD_REQUEST.toString(),
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                Arrays.stream(exception.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleUpdateAlreadyExists(UpdateAlreadyExistsException exception) {
        return new ApiErrorResponse(
                "Невозможно добавить уже существующий update",
                HttpStatus.BAD_REQUEST.toString(),
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                Arrays.stream(exception.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toList()
        );
    }
}
