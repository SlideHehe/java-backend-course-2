package edu.java.scrapper.scheduler;

import java.net.URI;
import java.time.OffsetDateTime;

public record UpdateInfo(
    Long linkId,
    URI url,
    String description,
    OffsetDateTime updatedAt
) {
}
