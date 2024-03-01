package edu.java.bot.client.scrapper;

import edu.java.bot.client.scrapper.dto.AddLinkRequest;
import edu.java.bot.client.scrapper.dto.LinkResponse;
import edu.java.bot.client.scrapper.dto.ListLinkResponse;
import edu.java.bot.client.scrapper.dto.RemoveLinkRequest;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface ScrapperClient {
    @PostExchange("/tg-chat/{id}")
    void registerChat(@PathVariable @Min(1) Long id);

    @DeleteExchange("tg-chat/{id}")
    void deleteChat(@PathVariable @Min(1) Long id);

    @GetExchange("/links")
    ListLinkResponse getFollowedLinks(@RequestHeader("Tg-Chat-Id") @Min(1L) Long tgChatId);

    @PostExchange("/links")
    LinkResponse addLink(
        @RequestHeader("Tg-Chat-Id") @Min(1L) Long tgChatId,
        @RequestBody AddLinkRequest addLinkRequest
    );

    @DeleteExchange("/links")
    LinkResponse removeLink(
        @RequestHeader("Tg-Chat-Id") @Min(1L) Long tgChatId,
        @RequestBody RemoveLinkRequest removeLinkRequest
    );
}
