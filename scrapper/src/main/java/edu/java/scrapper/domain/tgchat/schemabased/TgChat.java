package edu.java.scrapper.domain.tgchat.schemabased;

import java.time.OffsetDateTime;

public record TgChat(
    Long id,
    OffsetDateTime createdAt
) {
}
