package edu.java.bot.api.updates;

import edu.java.bot.api.updates.dto.LinkUpdateRequest;
import edu.java.bot.telegram.bot.Bot;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BotUpdatesServiceTest {
    @Mock
    Bot bot;

    @Test
    @DisplayName("Успешная отправка обновления")
    void successfulUpdate() {
        // given
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(
            URI.create("https://aboba.com"),
            "description",
            List.of(1L, 2L, 3L)
        );
        BotUpdatesService updatesService = new BotUpdatesService(bot);

        // when
        updatesService.createUpdate(linkUpdateRequest);

        // then
        verify(bot, times(3)).execute(any());
    }
}
