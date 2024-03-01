package edu.java.bot.client.scrapper.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.net.URI;

public record RemoveLinkRequest(
    @NotNull
    URI link
) {
}
