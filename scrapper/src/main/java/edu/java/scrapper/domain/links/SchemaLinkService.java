package edu.java.scrapper.domain.links;

import edu.java.scrapper.domain.chatlink.ChatLinkDao;
import edu.java.scrapper.domain.exception.LinkAlreadyExistsException;
import edu.java.scrapper.domain.exception.ResourceNotFoundException;
import edu.java.scrapper.domain.links.dto.AddLinkRequest;
import edu.java.scrapper.domain.links.dto.LinkResponse;
import edu.java.scrapper.domain.links.dto.ListLinkResponse;
import edu.java.scrapper.domain.links.dto.RemoveLinkRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class SchemaLinkService implements LinkService {
    private final LinkDao linkDao;
    private final ChatLinkDao chatLinkDao;

    @Override
    public ListLinkResponse getFollowedLinks(Long tgChatId) {
        List<LinkResponse> linkResponses = linkDao.findAllByChatId(tgChatId).stream()
            .map(LinkMapper::linkToLinkResponse)
            .toList();

        return new ListLinkResponse(
            linkResponses,
            linkResponses.size()
        );
    }

    @Override
    @Transactional
    public LinkResponse addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        Type type = getHostType(addLinkRequest.link());
        Link link = linkDao.findByUrl(addLinkRequest.link())
            .orElseGet(() -> linkDao.add(addLinkRequest.link(), type));

        chatLinkDao.findById(tgChatId, link.id()).ifPresent(ignored -> {
            throw new LinkAlreadyExistsException("Переданная ссылка уже отслеживается");
        });

        try {
            chatLinkDao.add(tgChatId, link.id());
        } catch (DataIntegrityViolationException e) {
            throw new ResourceNotFoundException("Указанный чат не зарегистрирован");
        }

        return LinkMapper.linkToLinkResponse(link);
    }

    @Override
    @Transactional
    public LinkResponse removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        Link link = linkDao.findByUrl(removeLinkRequest.link())
            .orElseThrow(() -> new ResourceNotFoundException("Указанная не существует в системе"));

        chatLinkDao.findById(tgChatId, link.id())
            .orElseThrow(() -> new ResourceNotFoundException("Указанная ссылка не отслеживается"));

        chatLinkDao.remove(tgChatId, link.id());
        chatLinkDao.removeDanglingLinks();

        return LinkMapper.linkToLinkResponse(link);
    }
}
