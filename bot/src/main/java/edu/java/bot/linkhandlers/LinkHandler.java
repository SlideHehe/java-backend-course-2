package edu.java.bot.linkhandlers;

import java.net.URI;
import org.jetbrains.annotations.NotNull;

public interface LinkHandler {
    String hostname();

    boolean canHandle(@NotNull URI url);

    void handleLink(@NotNull URI uri, @NotNull Long chatId);
}
