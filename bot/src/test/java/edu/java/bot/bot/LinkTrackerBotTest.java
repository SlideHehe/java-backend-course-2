package edu.java.bot.bot;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.CommandService;
import edu.java.bot.configuration.ApplicationConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LinkTrackerBotTest {
    @Mock
    CommandService commandService;

    @Mock
    ApplicationConfig applicationConfig;

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
        when(commandService.process(any(Update.class))).thenReturn(sendMessage);
        List<Update> updateList = List.of(new Update());

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
        when(commandService.process(any(Update.class))).thenReturn(sendMessage);
        List<Update> updateList = List.of(new Update(), new Update(), new Update());

        // when
        linkTrackerBot.process(updateList);

        // then
        verify(commandService, times(3)).process(any(Update.class));
    }
}
