package edu.java.scrapper.client.bot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record LinkUpdateRequest(
    @Min(1L)
    Long id,

    @NotNull
    URI url,

    @NotBlank
    String description,

    @NotEmpty
    List<@Min(1L) Long> tgChatIds
) {
}
