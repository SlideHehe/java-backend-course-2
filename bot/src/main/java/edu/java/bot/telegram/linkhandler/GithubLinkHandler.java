package edu.java.bot.telegram.linkhandler;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GithubLinkHandler implements LinkHandler {

    @Override
    public String hostname() {
        return LinkHandlersConstants.GITHUB_HOST;
    }

    @Override
    public boolean canHandle(@NotNull URI url) {
        return url.getHost().equals(hostname())
            && !url.getPath().isEmpty(); // TODO add check for supported api paths
    }

    @Override
    public void handleLink(@NotNull URI uri, @NotNull Long chatId) {
        log.info("ID " + chatId + " started tracking github link " + uri); // TODO implement
    }
}
