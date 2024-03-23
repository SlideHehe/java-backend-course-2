package edu.java.bot.telegram.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.telegram.command.Command;
import edu.java.bot.telegram.command.CommandService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LinkTrackerBot implements Bot {
    private final TelegramBot telegramBot;
    private final CommandService commandService;

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        telegramBot.execute(request);
    }

    @Override
    public int process(@NotNull List<Update> updates) {
        for (Update update : updates) {
            if (updateValid(update)) {
                SendMessage message = commandService.process(update);
                execute(message);
            }
        }

        return CONFIRMED_UPDATES_ALL;
    }

    private boolean updateValid(Update update) {
        return update != null
               && update.message() != null
               && update.message().text() != null
               && update.message().chat().id() != null;
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
