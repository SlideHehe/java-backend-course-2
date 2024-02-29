package edu.java.scrapper.api.links.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record ListLinkResponse(
    @NotNull
    List<LinkResponse> links,

    @Min(0)
    Integer size

) {
}
