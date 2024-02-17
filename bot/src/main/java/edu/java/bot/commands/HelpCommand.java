package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Getter
@Component
public class HelpCommand implements Command {
    private final List<Command> commands;

    public HelpCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public String command() {
        return CommandConstants.HELP_COMMAND;
    }

    @Override
    public String description() {
        return CommandConstants.HELP_DESCRIPTION;
    }

    @Override
    public SendMessage handle(@NotNull Update update) {
        StringBuilder message = new StringBuilder(CommandConstants.HELP_RESPONSE);

        for (Command command : commands) {
            message.append(getCommandInfo(command)).append(System.lineSeparator());
        }

        message.append(getCommandInfo(this));

        return new SendMessage(update.message().chat().id(), message.toString());
    }

    private String getCommandInfo(Command command) {
        return CommandConstants.LISTS_MARKER + command.command() + " " + command.description();
    }
}
