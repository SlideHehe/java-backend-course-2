package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.linkhandlers.LinkHandlerService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand implements Command {
    private final LinkHandlerService linkHandlerService;

    public UntrackCommand(@NotNull LinkHandlerService linkHandlerService) {
        this.linkHandlerService = linkHandlerService;
    }

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
        String[] commandsWithUrl = message.strip().split(" ");

        // [0] - /track
        // [1] - url

        if (commandsWithUrl.length != 2 || commandsWithUrl[1].isBlank()) {
            return new SendMessage(id, CommandConstants.UNTRACK_WRONG_COMMAND_FORMAT);
        }

        String response = linkHandlerService.untrackLink(commandsWithUrl[1], id);

        return new SendMessage(id, response);
    }
}
