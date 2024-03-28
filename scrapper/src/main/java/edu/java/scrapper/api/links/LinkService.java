package edu.java.scrapper.api.links;

import edu.java.scrapper.api.links.dto.AddLinkRequest;
import edu.java.scrapper.api.links.dto.LinkResponse;
import edu.java.scrapper.api.links.dto.ListLinkResponse;
import edu.java.scrapper.api.links.dto.RemoveLinkRequest;

public interface LinkService {
    ListLinkResponse getFollowedLinks(Long tgChatId);

    LinkResponse addLink(Long tgChatId, AddLinkRequest addLinkRequest);

    LinkResponse removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest);
}
