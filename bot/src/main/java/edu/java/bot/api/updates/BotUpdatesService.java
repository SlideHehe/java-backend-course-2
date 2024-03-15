package edu.java.bot.api.updates;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.updates.dto.LinkUpdate;
import edu.java.bot.api.updates.dto.LinkUpdateRequest;
import edu.java.bot.telegram.bot.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotUpdatesService implements UpdatesService {
    private final Bot bot;

    @Override
    public void createUpdate(LinkUpdateRequest linkUpdateRequest) {
        LinkUpdate linkUpdate = LinkUpdateMapper.linkUpdateRequestToLinkUpdate(linkUpdateRequest);

        for (Long tgChatId : linkUpdateRequest.tgChatIds()) {
            SendMessage message = new SendMessage(tgChatId, linkUpdate.toString());
            bot.execute(message);
        }
    }
}
