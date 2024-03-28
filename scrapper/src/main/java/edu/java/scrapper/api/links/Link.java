package edu.java.scrapper.api.links;

import java.net.URI;
import java.time.OffsetDateTime;

public record Link(
    Long id,
    URI url,
    OffsetDateTime updatedAt,
    OffsetDateTime checkedAt
) {
}
