package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.CommandService;
import edu.java.bot.configuration.ApplicationConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class LinkTrackerBot implements Bot {
    private final TelegramBot telegramBot;
    private final CommandService commandService;

    public LinkTrackerBot(ApplicationConfig applicationConfig, CommandService commandService) {
        this.commandService = commandService;

        this.telegramBot = new TelegramBot(applicationConfig.telegramToken());
    }

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        telegramBot.execute(request);
    }

    @Override
    public int process(@NotNull List<Update> updates) {
        for (Update update : updates) {
            SendMessage message = commandService.process(update);
            execute(message);
        }

        return CONFIRMED_UPDATES_ALL;
    }

    @Override
    @PostConstruct
    public void start() {
        addCommandsMenu();
        telegramBot.setUpdatesListener(this);

    }

    @Override
    @PreDestroy
    public void close() {
        telegramBot.removeGetUpdatesListener();
    }

    private void addCommandsMenu() {
        BotCommand[] botCommands = commandService.getCommands().stream()
            .map(Command::toApiCommand)
            .toArray(BotCommand[]::new);

        execute(new SetMyCommands(botCommands));
    }
}
