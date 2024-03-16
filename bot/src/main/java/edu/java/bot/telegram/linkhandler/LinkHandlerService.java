package edu.java.bot.telegram.linkhandler;

import edu.java.bot.telegram.link.Link;
import edu.java.bot.telegram.link.User;
import edu.java.bot.telegram.link.UserRepository;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LinkHandlerService {
    private final List<LinkHandler> handlers;
    private final UserRepository userRepository;

    public String untrackLink(@NotNull String pageToUntrack, @NotNull Long chatId) {
        User user = userRepository.findById(chatId).get();

        Optional<Link> optionalLink = user.getLinks().stream()
            .filter(link -> link.getUrl().equals(pageToUntrack))
            .findAny(); // TODO get link from DB

        if (optionalLink.isEmpty()) {
            return LinkHandlersConstants.NOT_TRACKING_YET;
        }

        user.removeLink(optionalLink.get());
        return LinkHandlersConstants.UNTRACKED;
    }

    public String trackLink(@NotNull String pageToTrack, @NotNull Long chatId) {
        URI url;
        try {
            url = new URI(pageToTrack);
        } catch (URISyntaxException e) {
            return LinkHandlersConstants.WRONG_URL_FORMAT;
        }

        String scheme = url.getScheme();
        if (Objects.isNull(scheme)
            || !scheme.startsWith(LinkHandlersConstants.HTTP_SCHEME)
            && !scheme.startsWith(LinkHandlersConstants.HTTPS_SCHEME)) {
            return LinkHandlersConstants.NOT_HTTP_RESOURCE;
        }

        Optional<LinkHandler> optionalLinkHandler = handlers.stream()
            .filter(linkHandler -> linkHandler.canHandle(url))
            .findAny();

        User user = userRepository.findById(chatId).get();
        return optionalLinkHandler.map(linkHandler -> addLink(user, linkHandler, url, chatId))
            .orElse(LinkHandlersConstants.CURRENTLY_INCAPABLE);
    }

    private String addLink(User user, LinkHandler linkHandler, URI url, Long chatId) {
        boolean alreadyTracks = !user.addLink(
            new Link(linkHandler.hostname(), url.toString())
        );

        if (alreadyTracks) {
            return LinkHandlersConstants.ALREADY_TRACKING;
        }

        linkHandler.handleLink(url, chatId);
        return LinkHandlersConstants.NOW_TRACKING;
    }
}
