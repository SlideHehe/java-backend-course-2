package edu.java.bot.telegram.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.telegram.link.User;
import edu.java.bot.telegram.link.UserRepository;
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
    private final UserRepository userRepository;

    public SendMessage process(@NotNull Update update) {
        Long id = update.message().chat().id();

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty() && !update.message().text().equals(CommandConstants.START_COMMAND)) {
            return new SendMessage(id, CommandConstants.UNREGISTERED_USER);
        }

        Optional<Command> optionalCommand = commands.stream()
            .filter(command -> command.supports(update))
            .findAny();

        if (optionalCommand.isPresent()) {
            return optionalCommand.get().handle(update);
        }

        return new SendMessage(update.message().chat().id(), CommandConstants.UNKNOWN_COMMAND);
    }
}