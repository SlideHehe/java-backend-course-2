package edu.java.bot.telegram.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.telegram.linkhandler.LinkHandlerService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TrackCommand implements Command {
    private final LinkHandlerService linkHandlerService;

    @Override
    public String command() {
        return CommandConstants.TRACK_COMMAND;
    }

    @Override
    public String description() {
        return CommandConstants.TRACK_DESCRIPTION;
    }

    @Override
    public SendMessage handle(@NotNull Update update) {
        Long id = update.message().chat().id();

        String message = update.message().text();
        String[] commandsWithUrl = message.split("\\s+");

        // [0] - /track
        // [1] - url

        if (commandsWithUrl.length != 2 || commandsWithUrl[1].isBlank()) {
            return new SendMessage(id, CommandConstants.TRACK_WRONG_COMMAND_FORMAT);
        }

        String response = linkHandlerService.trackLink(commandsWithUrl[1], id);

        return new SendMessage(id, response);
    }
}
