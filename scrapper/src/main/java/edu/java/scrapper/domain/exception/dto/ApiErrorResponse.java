package edu.java.scrapper.domain.exception.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;

public record ApiErrorResponse(
    @NotBlank
    String description,

    @NotBlank
    String code,

    @NotBlank
    String exceptionName,

    @NotBlank
    String exceptionMessage,

    @NotEmpty
    List<@NotBlank String> stacktrace
) {
    public static ApiErrorResponse create(String description, String code, Exception exception) {
        return new ApiErrorResponse(
            description,
            code,
            exception.getClass().getSimpleName(),
            exception.getMessage(),
            Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .toList()
        );
    }
}
