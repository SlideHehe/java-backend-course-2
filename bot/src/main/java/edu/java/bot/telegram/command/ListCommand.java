package edu.java.bot.telegram.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.bot.client.scrapper.dto.LinkResponse;
import edu.java.bot.client.scrapper.dto.ListLinkResponse;
import edu.java.bot.domain.exception.dto.ApiErrorResponse;
import edu.java.bot.telegram.service.LinkHandlersConstants;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RequiredArgsConstructor
@Component
public class ListCommand implements Command {
    private final ScrapperClient scrapperClient;

    @Override
    public String command() {
        return CommandConstants.LIST_COMMAND;
    }

    @Override
    public String description() {
        return CommandConstants.LIST_DESCRIPTION;
    }

    @Override
    public SendMessage handle(@NotNull Update update) {
        Long id = update.message().chat().id();
        ListLinkResponse listLinkResponse;
        try {
            listLinkResponse = scrapperClient.getFollowedLinks(id);
        } catch (WebClientRequestException e) {
            return new SendMessage(id, LinkHandlersConstants.REQUEST_ERROR);
        } catch (WebClientResponseException e) {
            ApiErrorResponse errorResponse = e.getResponseBodyAs(ApiErrorResponse.class);
            String message = errorResponse != null && errorResponse.exceptionMessage() != null
                ? errorResponse.exceptionMessage() : LinkHandlersConstants.UNKNOWN_RESPONSE_ERROR;
            return new SendMessage(id, message);
        }

        String response =
            listLinkResponse.size() == 0 ? CommandConstants.LIST_EMPTY_RESPONSE : getUserResources(listLinkResponse);
        return new SendMessage(id, response);
    }

    private String getUserResources(ListLinkResponse listLinkResponse) {
        StringBuilder stringBuilder = new StringBuilder(CommandConstants.LIST_RESPONSE);

        for (LinkResponse link : listLinkResponse.links()) {
            stringBuilder
                .append(CommandConstants.LISTS_MARKER)
                .append(link.url())
                .append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }
}
