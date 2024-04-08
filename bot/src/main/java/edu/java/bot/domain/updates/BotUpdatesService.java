package edu.java.bot.domain.updates;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.domain.updates.dto.LinkUpdate;
import edu.java.bot.domain.updates.dto.LinkUpdateRequest;
import edu.java.bot.telegram.bot.Bot;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotUpdatesService implements UpdatesService {
    private final Bot bot;
    private final Counter processedMessagesCounter;

    @Override
    public void createUpdate(LinkUpdateRequest linkUpdateRequest) {
        LinkUpdate linkUpdate = LinkUpdateMapper.linkUpdateRequestToLinkUpdate(linkUpdateRequest);

        for (Long tgChatId : linkUpdateRequest.tgChatIds()) {
            SendMessage message = new SendMessage(tgChatId, linkUpdate.toString());
            bot.execute(message);
        }
        processedMessagesCounter.increment();
    }
}
