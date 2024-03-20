package edu.java.bot.telegram.service;

import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.bot.client.scrapper.dto.AddLinkRequest;
import edu.java.bot.client.scrapper.dto.LinkResponse;
import edu.java.bot.client.scrapper.dto.RemoveLinkRequest;
import edu.java.bot.domain.exception.dto.ApiErrorResponse;
import edu.java.bot.telegram.service.linkvalidator.LinkValidator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RequiredArgsConstructor
@Service
public class LinkHandlerService {
    private final List<LinkValidator> handlers;
    private final ScrapperClient scrapperClient;

    public String untrackLink(@NotNull URI url, @NotNull Long chatId) {
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(url);
        LinkResponse linkResponse;

        try {
            linkResponse = scrapperClient.removeLink(chatId, removeLinkRequest);
        } catch (WebClientRequestException e) {
            return LinkHandlersConstants.REQUEST_ERROR;
        } catch (WebClientResponseException e) {
            ApiErrorResponse errorResponse = e.getResponseBodyAs(ApiErrorResponse.class);
            return errorResponse != null && errorResponse.description() != null
                ? errorResponse.description() : LinkHandlersConstants.UNKNOWN_RESPONSE_ERROR;
        }

        return LinkHandlersConstants.UNTRACKED.formatted(linkResponse.url());
    }

    public String trackLink(@NotNull String resourceLink, @NotNull Long chatId) {
        URI url;
        try {
            url = new URI(resourceLink);
        } catch (URISyntaxException e) {
            return LinkHandlersConstants.WRONG_URL_FORMAT;
        }

        String scheme = url.getScheme();
        if (Objects.isNull(scheme)
            || !scheme.startsWith(LinkHandlersConstants.HTTP_SCHEME)
               && !scheme.startsWith(LinkHandlersConstants.HTTPS_SCHEME)) {
            return LinkHandlersConstants.NOT_HTTP_RESOURCE;
        }

        boolean incapable = handlers.stream()
            .noneMatch(linkValidator -> linkValidator.supports(url));

        if (incapable) {
            return LinkHandlersConstants.CURRENTLY_INCAPABLE;
        }

        return addLink(url, chatId);
    }

    private String addLink(URI url, Long chatId) {
        AddLinkRequest addLinkRequest = new AddLinkRequest(url);
        LinkResponse linkResponse;
        try {
            linkResponse = scrapperClient.addLink(chatId, addLinkRequest);
        } catch (WebClientRequestException e) {
            return LinkHandlersConstants.REQUEST_ERROR;
        } catch (WebClientResponseException e) {
            ApiErrorResponse errorResponse = e.getResponseBodyAs(ApiErrorResponse.class);
            return errorResponse != null && errorResponse.description() != null
                ? errorResponse.description() : LinkHandlersConstants.UNKNOWN_RESPONSE_ERROR;
        }

        return LinkHandlersConstants.NOW_TRACKING.formatted(linkResponse.url());
    }
}
