package edu.java.bot.domain.updates.dto;

import java.net.URI;

public record LinkUpdate(URI url, String description) {
    @Override public String toString() {
        return "Ссылка: " + url + System.lineSeparator().repeat(2) + description;
    }
}
