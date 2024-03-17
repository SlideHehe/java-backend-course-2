package edu.java.scrapper.domain.tgchat;

import java.time.OffsetDateTime;

public record TgChat(
    Long id,
    OffsetDateTime createdAt
) {
}
