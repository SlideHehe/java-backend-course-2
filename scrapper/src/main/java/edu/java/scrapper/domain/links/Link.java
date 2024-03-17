package edu.java.scrapper.domain.links;

import java.net.URI;
import java.time.OffsetDateTime;

public record Link(
    Long id,
    URI url,
    OffsetDateTime updatedAt,
    OffsetDateTime checkedAt,
    Type type,
    Integer answerCount,
    Integer commentCount,
    Integer pullRequestCount,
    Integer commitCount
) {
}
