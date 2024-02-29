package edu.java.scrapper.api.links;

import edu.java.scrapper.api.links.dto.AddLinkRequest;
import edu.java.scrapper.api.links.dto.LinkResponse;
import edu.java.scrapper.api.links.dto.ListLinkResponse;
import edu.java.scrapper.api.links.dto.RemoveLinkRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/links")
public class LinksController {
    private final LinksService linksService;

    @GetMapping
    ListLinkResponse getFollowedLinks(@RequestHeader("Tg-Chat-Id") @Min(1L) Long tgChatId) {
        return linksService.getFollowedLinks(tgChatId);
    }

    @PostMapping
    LinkResponse addLink(
        @RequestHeader("Tg-Chat-Id") @Min(1L) Long tgChatId,
        @RequestBody @Valid AddLinkRequest addLinkRequest
    ) {
        return linksService.addLink(tgChatId, addLinkRequest);
    }

    @DeleteMapping
    LinkResponse removeLink(
        @RequestHeader("Tg-Chat-Id") @Min(1L) Long tgChatId,
        @RequestBody @Valid RemoveLinkRequest removeLinkRequest
    ) {
        return linksService.removeLink(tgChatId, removeLinkRequest);
    }
}
