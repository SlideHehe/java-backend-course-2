package edu.java.bot.client.scrapper.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record LinkResponse(
    @Min(1L)
    Long id,

    @NotNull
    URI url
) {
}
