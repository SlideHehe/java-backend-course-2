package edu.java.scrapper.api.links.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;

public record LinkResponse(
    @Min(1L)
    Long id,

    @NotBlank
    URI url
) {
}
