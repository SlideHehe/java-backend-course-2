package edu.java.bot.api.updates.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.net.URI;
import java.util.List;

public record LinkUpdateRequest(
    @Min(1L)
    Long id,

    @NotBlank
    URI url,

    @NotBlank
    String description,

    @NotEmpty
    List<@Min(1L) Long> tgChatIds
) {
}
