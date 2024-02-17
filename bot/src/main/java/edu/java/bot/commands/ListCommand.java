package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.links.Link;
import edu.java.bot.links.User;
import edu.java.bot.links.UserRepository;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ListCommand implements Command {
    private final UserRepository userRepository;

    public ListCommand(@NotNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

        User user = userRepository.findById(id).get();
        Set<Link> links = user.getLinks();

        String response = links.isEmpty() ? CommandConstants.LIST_EMPTY_RESPONSE : getUserResources(links);
        return new SendMessage(id, response);
    }

    private String getUserResources(Set<Link> links) {
        StringBuilder stringBuilder = new StringBuilder(CommandConstants.LIST_RESPONSE);

        for (Link link : links) {
            stringBuilder
                .append(CommandConstants.LISTS_MARKER)
                .append(link.getUrl())
                .append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }
}
