package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.links.User;
import edu.java.bot.links.UserRepository;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements Command {
    private final UserRepository userRepository;

    public StartCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String command() {
        return CommandConstants.START_COMMAND;
    }

    @Override
    public String description() {
        return CommandConstants.START_DESCRIPTION;
    }

    @Override
    public SendMessage handle(@NotNull Update update) {
        Long id = update.message().chat().id();
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            User user = new User(id);
            userRepository.save(user);

            return new SendMessage(update.message().chat().id(), CommandConstants.START_NEW_USER_MESSAGE);
        }

        return new SendMessage(update.message().chat().id(), CommandConstants.START_EXISTING_USER_MESSAGE);
    }
}
