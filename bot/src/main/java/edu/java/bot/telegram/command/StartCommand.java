package edu.java.bot.telegram.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.telegram.link.User;
import edu.java.bot.telegram.link.UserRepository;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StartCommand implements Command {
    private final UserRepository userRepository;

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
