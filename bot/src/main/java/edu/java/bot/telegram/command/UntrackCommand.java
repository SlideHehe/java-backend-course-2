package edu.java.bot.telegram.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.telegram.service.LinkHandlerService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UntrackCommand implements Command {
    private final LinkHandlerService linkHandlerService;

    @Override
    public String command() {
        return CommandConstants.UNTRACK_COMMAND;
    }

    @Override
    public String description() {
        return CommandConstants.UNTRACK_DESCRIPTION;
    }

    @Override
    public SendMessage handle(@NotNull Update update) {
        Long id = update.message().chat().id();

        String message = update.message().text();
        String[] commandsWithUrl = message.split("\\s+");

        // [0] - /track
        // [1] - url

        if (commandsWithUrl.length != 2 || commandsWithUrl[1].isBlank()) {
            return new SendMessage(id, CommandConstants.UNTRACK_WRONG_COMMAND_FORMAT);
        }

        URI url = URI.create(commandsWithUrl[1]);
        String response = linkHandlerService.untrackLink(url, id);

        return new SendMessage(id, response);
    }
}
