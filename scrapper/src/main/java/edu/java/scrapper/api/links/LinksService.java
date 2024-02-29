package edu.java.scrapper.api.links;

import edu.java.scrapper.api.exception.LinkAlreadyExistsException;
import edu.java.scrapper.api.exception.ResourceNotFoundException;
import edu.java.scrapper.api.links.dto.AddLinkRequest;
import edu.java.scrapper.api.links.dto.LinkResponse;
import edu.java.scrapper.api.links.dto.ListLinkResponse;
import edu.java.scrapper.api.links.dto.RemoveLinkRequest;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class LinksService {
    private final Map<Long, Set<LinkResponse>> trackedLinks = new LinkedHashMap<>();
    private long linkIdentity = 1; // TODO remove when data layer is done

    public ListLinkResponse getFollowedLinks(Long tgChatId) {
        checkIfChatExists(tgChatId);

        return new ListLinkResponse(
            trackedLinks.get(tgChatId).stream().toList(),
            trackedLinks.size()
        );
    }

    public LinkResponse addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        trackedLinks.computeIfAbsent(tgChatId, id -> new LinkedHashSet<>());

        if (trackedLinks.get(tgChatId).stream()
            .anyMatch(linkResponse -> linkResponse.url().equals(addLinkRequest.link()))) {
            throw new LinkAlreadyExistsException("Переданная ссылка уже обрабатывается");
        }

        LinkResponse linkResponse = new LinkResponse(linkIdentity++, addLinkRequest.link());

        trackedLinks.get(tgChatId).add(linkResponse);
        return linkResponse;

    }

    public LinkResponse removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        checkIfChatExists(tgChatId);

        return trackedLinks.get(tgChatId).stream()
            .filter(linkResponse -> linkResponse.url().equals(removeLinkRequest.link()))
            .findAny()
            .orElseThrow(() -> new ResourceNotFoundException("Указанная ссылка не отслеживается"));
    }

    private void checkIfChatExists(Long tgChatId) {
        if (!trackedLinks.containsKey(tgChatId)) {
            throw new ResourceNotFoundException("Указанный чат не зарегестрирован");
        }
    }
}
