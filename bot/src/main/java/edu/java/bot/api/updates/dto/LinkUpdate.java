package edu.java.bot.api.updates.dto;

import java.net.URI;

public record LinkUpdate(URI url, String description) {
    @Override public String toString() {
        return "Ссылка " + url + ": " + description;
    }
}
