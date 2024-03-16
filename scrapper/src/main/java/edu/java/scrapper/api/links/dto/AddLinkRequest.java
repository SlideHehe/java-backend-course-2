package edu.java.scrapper.api.links.dto;

import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record AddLinkRequest(
    @NotNull
    URI link
) {
}
