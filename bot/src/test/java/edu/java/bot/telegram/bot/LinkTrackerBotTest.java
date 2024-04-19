package edu.java.bot.telegram.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.telegram.command.CommandService;
import io.micrometer.core.instrument.Counter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkTrackerBotTest {
    @Mock
    TelegramBot telegramBot;
    @Mock
    CommandService commandService;
    @Mock
    Counter counter;

    @InjectMocks
    LinkTrackerBot linkTrackerBot;

    @Test
    @DisplayName("Вызов метода process с пустым списком")
    void processMethodEmptyList() {
        // given
        List<Update> updateList = List.of();

        // when
        linkTrackerBot.process(updateList);

        // then
        verify(commandService, times(0)).process(any(Update.class));
    }

    @Test
    @DisplayName("Вызов метода process со списком из 1 элемента")
    void processMethodSingleElementList() {
        // given
        SendMessage sendMessage = new SendMessage(1L, "hello");
        Chat chat = mock(Chat.class);
        Message message = mock(Message.class);
        Update update = mock(Update.class);
        when(commandService.process(any(Update.class))).thenReturn(sendMessage);
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        when(message.text()).thenReturn("hello");
        when(update.message()).thenReturn(message);
        List<Update> updateList = List.of(update);

        // when
        linkTrackerBot.process(updateList);

        // then
        verify(commandService, times(1)).process(any(Update.class));
    }

    @Test
    @DisplayName("Вызов метода process со списком из 3 элементов")
    void processMethodMultipleElementsList() {
        // given
        SendMessage sendMessage = new SendMessage(1L, "hello");
        Chat chat = mock(Chat.class);
        Message message = mock(Message.class);
        Update update = mock(Update.class);
        when(chat.id()).thenReturn(1L);
        when(message.chat()).thenReturn(chat);
        when(message.text()).thenReturn("hello");
        when(update.message()).thenReturn(message);
        when(commandService.process(any(Update.class))).thenReturn(sendMessage);
        List<Update> updateList = List.of(update, update, update);

        // when
        linkTrackerBot.process(updateList);

        // then
        verify(commandService, times(3)).process(any(Update.class));
    }
}
