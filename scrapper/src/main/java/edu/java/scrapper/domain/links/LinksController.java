package edu.java.scrapper.domain.links;

import edu.java.scrapper.domain.links.dto.AddLinkRequest;
import edu.java.scrapper.domain.links.dto.LinkResponse;
import edu.java.scrapper.domain.links.dto.ListLinkResponse;
import edu.java.scrapper.domain.links.dto.RemoveLinkRequest;
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
    private final LinkService linksService;

    @GetMapping
    public ListLinkResponse getFollowedLinks(@RequestHeader("Tg-Chat-Id") @Min(1L) Long tgChatId) {
        return linksService.getFollowedLinks(tgChatId);
    }

    @PostMapping
    public LinkResponse addLink(
        @RequestHeader("Tg-Chat-Id") @Min(1L) Long tgChatId,
        @RequestBody @Valid AddLinkRequest addLinkRequest
    ) {
        return linksService.addLink(tgChatId, addLinkRequest);
    }

    @DeleteMapping
    public LinkResponse removeLink(
        @RequestHeader("Tg-Chat-Id") @Min(1L) Long tgChatId,
        @RequestBody @Valid RemoveLinkRequest removeLinkRequest
    ) {
        return linksService.removeLink(tgChatId, removeLinkRequest);
    }
}
