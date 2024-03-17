package edu.java.scrapper.domain.links.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ListLinkResponse(
    @NotNull
    List<LinkResponse> links,

    @Min(0)
    Integer size
) {
}
