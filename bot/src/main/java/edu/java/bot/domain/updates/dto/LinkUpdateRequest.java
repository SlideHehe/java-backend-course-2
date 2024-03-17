package edu.java.bot.domain.updates.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record LinkUpdateRequest(
    @NotNull
    URI url,

    @NotBlank
    String description,

    @NotEmpty
    List<@Min(1L) Long> tgChatIds
) {
}
