package edu.java.scrapper.api.exception;

import edu.java.scrapper.api.exception.dto.ApiErrorResponse;
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ApiErrorResponse handleChatAlreadyExists(ChatAlreadyExistsException exception) {
        return ApiErrorResponse.create(
            "Невозможно повторно зарегестрировать чат",
            HttpStatus.BAD_REQUEST.toString(),
            exception
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ApiErrorResponse handleLinkAlreadyExists(LinkAlreadyExistsException exception) {
        return ApiErrorResponse.create(
            "Невозможно повторно добавить уже отслеживаемую ссылку",
            HttpStatus.BAD_REQUEST.toString(),
            exception
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ApiErrorResponse handleResourceNotFound(ResourceNotFoundException exception) {
        return ApiErrorResponse.create(
            "Указанный ресурс не существует в системе",
            HttpStatus.NOT_FOUND.toString(),
            exception
        );
    }
}
