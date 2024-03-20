package edu.java.bot.telegram.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.bot.client.scrapper.dto.LinkResponse;
import edu.java.bot.client.scrapper.dto.ListLinkResponse;
import edu.java.bot.telegram.service.LinkHandlerService;
import java.net.URI;
import java.util.List;
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
class CommandsTest {
    @Mock
    Update update;

    @Mock
    Message message;

    @Mock
    Chat chat;

    @Mock
    ScrapperClient scrapperClient;

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
        when(scrapperClient.getFollowedLinks(1L)).thenReturn(new ListLinkResponse(List.of(new LinkResponse(
            1L,
            URI.create("https://aboba.com/repository")
        )), 1));
        Command command = new ListCommand(scrapperClient);
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
        when(scrapperClient.getFollowedLinks(1L)).thenReturn(new ListLinkResponse(List.of(), 0));
        Command command = new ListCommand(scrapperClient);
        SendMessage expectedMessage = new SendMessage(1L, CommandConstants.LIST_EMPTY_RESPONSE);

        // when
        SendMessage actualMessage = command.handle(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Команда /start")
    void startCommand() {
        // given
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        Command command = new StartCommand(scrapperClient);
        SendMessage expectedMessage = new SendMessage(1L, CommandConstants.START_NEW_USER_MESSAGE);

        // when
        SendMessage actualMessage = command.handle(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("Команда /unregister")
    void unregisterCommand() {
        // given
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        Command command = new UnregisterCommand(scrapperClient);
        SendMessage expectedMessage = new SendMessage(1L, CommandConstants.UNREGISTER_RESPONSE);

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
        when(linkHandlerService.untrackLink(URI.create(url), 1L)).thenReturn("success");
        Command command = new UntrackCommand(linkHandlerService);
        SendMessage expectedMessage = new SendMessage(1L, "success");

        // when
        SendMessage actualMessage = command.handle(update);

        // then
        assertThat(actualMessage).usingRecursiveComparison().isEqualTo(expectedMessage);
    }
}
