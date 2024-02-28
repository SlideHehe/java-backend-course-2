package edu.java.bot.telegram.linkhandler;

import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class StackoverflowLinkHandler implements LinkHandler {
    private static final Logger LOGGER = LogManager.getLogger();

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
        LOGGER.info("ID " + chatId + " started tracking stackoverflow link " + uri); // TODO implement
    }
}
