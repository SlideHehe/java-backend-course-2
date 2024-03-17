package edu.java.scrapper.domain.links.jdbc;

import edu.java.scrapper.domain.chatlink.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.exception.LinkAlreadyExistsException;
import edu.java.scrapper.domain.exception.ResourceNotFoundException;
import edu.java.scrapper.domain.links.Link;
import edu.java.scrapper.domain.links.LinkMapper;
import edu.java.scrapper.domain.links.LinkService;
import edu.java.scrapper.domain.links.Type;
import edu.java.scrapper.domain.links.dto.AddLinkRequest;
import edu.java.scrapper.domain.links.dto.LinkResponse;
import edu.java.scrapper.domain.links.dto.ListLinkResponse;
import edu.java.scrapper.domain.links.dto.RemoveLinkRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final JdbcLinkDao jdbcLinkDao;
    private final JdbcChatLinkDao jdbcChatLinkDao;

    @Override
    public ListLinkResponse getFollowedLinks(Long tgChatId) {
        List<LinkResponse> linkResponses = jdbcLinkDao.findAllByChatId(tgChatId).stream()
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
        Link link = jdbcLinkDao.findByUrl(addLinkRequest.link())
            .orElseGet(() -> jdbcLinkDao.add(addLinkRequest.link(), type));

        jdbcChatLinkDao.findById(tgChatId, link.id()).ifPresent(ignored -> {
            throw new LinkAlreadyExistsException("Переданная ссылка уже отслеживается");
        });

        try {
            jdbcChatLinkDao.add(tgChatId, link.id());
        } catch (DataIntegrityViolationException e) {
            throw new ResourceNotFoundException("Указанный чат не зарегистрирован");
        }

        return LinkMapper.linkToLinkResponse(link);
    }

    @Override
    @Transactional
    public LinkResponse removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        Link link = jdbcLinkDao.findByUrl(removeLinkRequest.link())
            .orElseThrow(() -> new ResourceNotFoundException("Указанная не существует в системе"));

        jdbcChatLinkDao.findById(tgChatId, link.id())
            .orElseThrow(() -> new ResourceNotFoundException("Указанная ссылка не отслеживается"));

        jdbcChatLinkDao.remove(tgChatId, link.id());
        jdbcChatLinkDao.removeDanglingLinks();

        return LinkMapper.linkToLinkResponse(link);
    }
}
