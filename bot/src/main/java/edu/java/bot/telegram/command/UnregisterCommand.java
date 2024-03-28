package edu.java.bot.telegram.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.bot.domain.exception.dto.ApiErrorResponse;
import edu.java.bot.telegram.service.LinkHandlersConstants;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RequiredArgsConstructor
@Component
public class UnregisterCommand implements Command {
    private final ScrapperClient scrapperClient;

    @Override
    public String command() {
        return CommandConstants.UNREGISTER_COMMAND;
    }

    @Override
    public String description() {
        return CommandConstants.UNREGISTER_DESCRIPTION;
    }

    @Override
    public SendMessage handle(@NotNull Update update) {
        Long id = update.message().chat().id();

        try {
            scrapperClient.deleteChat(id);
        } catch (WebClientRequestException e) {
            return new SendMessage(id, LinkHandlersConstants.REQUEST_ERROR);
        } catch (WebClientResponseException e) {
            ApiErrorResponse errorResponse = e.getResponseBodyAs(ApiErrorResponse.class);
            String message = errorResponse != null && errorResponse.exceptionMessage() != null
                ? errorResponse.exceptionMessage() : LinkHandlersConstants.UNKNOWN_RESPONSE_ERROR;
            return new SendMessage(id, message);
        }

        return new SendMessage(update.message().chat().id(), CommandConstants.UNREGISTER_RESPONSE);
    }
}
