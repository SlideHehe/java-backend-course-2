package edu.java.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.linkhandlers.LinkHandlerService;
import edu.java.bot.links.Link;
import edu.java.bot.links.User;
import edu.java.bot.links.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandsTest {
    @Mock
    Update update;

    @Mock
    Message message;

    @Mock
    Chat chat;

    @BeforeEach
    void initUpdateObject() {
        when(update.message()).thenReturn(message);
    }

    @Test
    @DisplayName("Проверка поддержки существующей команды")
    void supports() {
        // given
        Command command = new HelpCommand(List.of());
        when(message.text()).thenReturn(CommandConstants.HELP_COMMAND);

        // when
        boolean actual = command.supports(update);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Проверка поддержки несуществующей команды")
    void notSupports() {
        // given
        Command command = new HelpCommand(List.of());
        when(message.text()).thenReturn("/random");

        // when
        boolean actual = command.supports(update);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Команда /help")
    void helpCommandHandle() {
        // given
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        Command command = new HelpCommand(List.of());
        SendMessage expectedMessage = new SendMessage(
            1L,
            CommandConstants.HELP_RESPONSE + CommandConstants.LISTS_MARKER
                + command.command() + " " + command.description()
        );

        // when
        SendMessage actualMessage = command.handle(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Команда /list со списком содержащим отслеживаемые ресурсы")
    void listCommandNotEmpty() {
        // given
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        UserRepository userRepository = mock(UserRepository.class);
        User user = new User(1L);
        Link link = new Link("aboba.com", "https://aboba.com/repository");
        user.addLink(link);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Command command = new ListCommand(userRepository);
        SendMessage expectedMessage = new SendMessage(
            1L,
            CommandConstants.LIST_RESPONSE + CommandConstants.LISTS_MARKER
                + "https://aboba.com/repository" + System.lineSeparator()
        );

        // when
        SendMessage actualMessage = command.handle(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Команда /list с пустым списком отслеживаемых ресурсов")
    void listCommandEmpty() {
        // given
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        UserRepository userRepository = mock(UserRepository.class);
        User user = new User(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Command command = new ListCommand(userRepository);
        SendMessage expectedMessage = new SendMessage(1L, CommandConstants.LIST_EMPTY_RESPONSE);

        // when
        SendMessage actualMessage = command.handle(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Команда /start для нового пользователя")
    void startCommandNewUser() {
        // given
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Command command = new StartCommand(userRepository);
        SendMessage expectedMessage = new SendMessage(1L, CommandConstants.START_NEW_USER_MESSAGE);

        // when
        SendMessage actualMessage = command.handle(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Команда /start для зарегестрированного пользователя")
    void startCommandExistingUser() {
        // given
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        UserRepository userRepository = mock(UserRepository.class);
        User user = new User(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Command command = new StartCommand(userRepository);
        SendMessage expectedMessage = new SendMessage(1L, CommandConstants.START_EXISTING_USER_MESSAGE);

        // when
        SendMessage actualMessage = command.handle(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Команда /track без указания ссылки")
    void trackCommandWrongFormat() {
        // given
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        when(message.text()).thenReturn(CommandConstants.TRACK_COMMAND);
        LinkHandlerService linkHandlerService = mock(LinkHandlerService.class);
        Command command = new TrackCommand(linkHandlerService);
        SendMessage expectedMessage = new SendMessage(1L, CommandConstants.TRACK_WRONG_COMMAND_FORMAT);

        // when
        SendMessage actualMessage = command.handle(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Команда /track с указанием ссылки")
    void trackCommandCorrectFormat() {
        // given
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        String url = "https://aboba.com/repository";
        when(message.text()).thenReturn(CommandConstants.TRACK_COMMAND + " " + url);
        LinkHandlerService linkHandlerService = mock(LinkHandlerService.class);
        when(linkHandlerService.trackLink(url, 1L)).thenReturn("success");
        Command command = new TrackCommand(linkHandlerService);
        SendMessage expectedMessage = new SendMessage(1L, "success");

        // when
        SendMessage actualMessage = command.handle(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Команда /untrack без указания ссылки")
    void untrackCommandWrongFormat() {
        // given
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        when(message.text()).thenReturn(CommandConstants.UNTRACK_COMMAND);
        LinkHandlerService linkHandlerService = mock(LinkHandlerService.class);
        Command command = new UntrackCommand(linkHandlerService);
        SendMessage expectedMessage = new SendMessage(1L, CommandConstants.UNTRACK_WRONG_COMMAND_FORMAT);

        // when
        SendMessage actualMessage = command.handle(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Команда /untrack с указанием ссылки")
    void untrackCommandCorrectFormat() {
        // given
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        String url = "https://aboba.com/repository";
        when(message.text()).thenReturn(CommandConstants.UNTRACK_COMMAND + " " + url);
        LinkHandlerService linkHandlerService = mock(LinkHandlerService.class);
        when(linkHandlerService.untrackLink(url, 1L)).thenReturn("success");
        Command command = new UntrackCommand(linkHandlerService);
        SendMessage expectedMessage = new SendMessage(1L, "success");

        // when
        SendMessage actualMessage = command.handle(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }
}
