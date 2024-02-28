package edu.java.bot.telegram.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.telegram.link.User;
import edu.java.bot.telegram.link.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
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
public class CommandServiceTest {
    @Mock
    UserRepository userRepository;

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

    @BeforeEach
    void initUpdateObject() {
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);
    }

    @Test
    @DisplayName("Команда обработки для незарегстрированного пользователя")
    void processUnknownUser() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(message.text()).thenReturn("hello");
        SendMessage expectedMessage = new SendMessage(1L, CommandConstants.UNREGISTERED_USER);

        // when
        SendMessage actualMessage = commandService.process(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Команда обработки для незарегстрированного пользователя")
    void processUnknownCommand() {
        // given
        User user = new User(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
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
        User user = new User(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
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
