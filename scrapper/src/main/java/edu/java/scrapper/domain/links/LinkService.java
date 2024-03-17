package edu.java.scrapper.domain.links;

import edu.java.scrapper.domain.links.dto.AddLinkRequest;
import edu.java.scrapper.domain.links.dto.LinkResponse;
import edu.java.scrapper.domain.links.dto.ListLinkResponse;
import edu.java.scrapper.domain.links.dto.RemoveLinkRequest;
import java.net.URI;

public interface LinkService {
    ListLinkResponse getFollowedLinks(Long tgChatId);

    LinkResponse addLink(Long tgChatId, AddLinkRequest addLinkRequest);

    LinkResponse removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest);

    default Type getHostType(URI uri) {
        int domainZoneIndex = uri.getHost().lastIndexOf('.');
        String host = uri.getHost().substring(0, domainZoneIndex).toUpperCase();
        return Type.valueOf(host);
    }
}
