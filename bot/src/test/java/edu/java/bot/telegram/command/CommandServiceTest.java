package edu.java.bot.telegram.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandServiceTest {
    @Mock
    List<Command> commands;

    @InjectMocks
    CommandService commandService;

    @Mock
    Update update;

    @Mock
    Message message;

    @Mock
    Chat chat;

    @Test
    @DisplayName("Команда обработки для неизвестной команды")
    void processUnknownCommand() {
        // given
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
        SendMessage expectedMessage = new SendMessage(1L, CommandConstants.UNKNOWN_COMMAND);

        // when
        SendMessage actualMessage = commandService.process(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Команда обработки для известной команды")
    void processKnownCommand() {
        // given
        Command command = mock(Command.class);
        when(command.supports(update)).thenReturn(true);
        when(command.handle(update)).thenReturn(new SendMessage(1L, "hello"));
        when(commands.stream()).thenReturn(Stream.of(command));
        SendMessage expectedMessage = new SendMessage(1L, "hello");

        // when
        SendMessage actualMessage = commandService.process(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }
}
