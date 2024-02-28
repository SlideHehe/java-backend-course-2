package edu.java.bot.telegram.linkhandler;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.net.URI;

@Slf4j
@Component
public class StackoverflowLinkHandler implements LinkHandler {
    @Override
    public String hostname() {
        return LinkHandlersConstants.STACKOVERFLOW_HOST;
    }

    @Override
    public boolean canHandle(@NotNull URI url) {
        return url.getHost().equals(hostname())
                && !url.getPath().isEmpty();  // TODO add check for supported api paths
    }

    @Override
    public void handleLink(@NotNull URI uri, @NotNull Long chatId) {
        log.info("ID " + chatId + " started tracking stackoverflow link " + uri); // TODO implement
    }
}
