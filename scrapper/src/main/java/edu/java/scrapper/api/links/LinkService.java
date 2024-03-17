package edu.java.scrapper.api.links;

import edu.java.scrapper.api.links.dto.AddLinkRequest;
import edu.java.scrapper.api.links.dto.LinkResponse;
import edu.java.scrapper.api.links.dto.ListLinkResponse;
import edu.java.scrapper.api.links.dto.RemoveLinkRequest;
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
