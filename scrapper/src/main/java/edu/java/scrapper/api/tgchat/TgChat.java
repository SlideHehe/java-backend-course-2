package edu.java.scrapper.api.tgchat;

import java.time.OffsetDateTime;

public record TgChat(
    Long id,
    OffsetDateTime createdAt
) {
}
