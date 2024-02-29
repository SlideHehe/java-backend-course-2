package edu.java.scrapper.api.links.dto;

import jakarta.validation.constraints.NotBlank;
import java.net.URI;

public record AddLinkRequest(
    @NotBlank
    URI link
) {
}
