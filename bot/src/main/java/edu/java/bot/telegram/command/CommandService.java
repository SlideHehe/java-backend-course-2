package edu.java.bot.telegram.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Getter
@Service
public class CommandService {
    private final List<Command> commands;

    public SendMessage process(@NotNull Update update) {
        Optional<Command> optionalCommand = commands.stream()
            .filter(command -> command.supports(update))
            .findAny();

        if (optionalCommand.isPresent()) {
            return optionalCommand.get().handle(update);
        }

        return new SendMessage(update.message().chat().id(), CommandConstants.UNKNOWN_COMMAND);
    }
}
