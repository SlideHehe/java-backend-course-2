package edu.java.scrapper.domain.links.jpa;

import edu.java.scrapper.domain.exception.LinkAlreadyExistsException;
import edu.java.scrapper.domain.exception.ResourceNotFoundException;
import edu.java.scrapper.domain.links.LinkMapper;
import edu.java.scrapper.domain.links.LinkService;
import edu.java.scrapper.domain.links.Type;
import edu.java.scrapper.domain.links.dto.AddLinkRequest;
import edu.java.scrapper.domain.links.dto.LinkResponse;
import edu.java.scrapper.domain.links.dto.ListLinkResponse;
import edu.java.scrapper.domain.links.dto.RemoveLinkRequest;
import edu.java.scrapper.domain.tgchat.jpa.Chat;
import edu.java.scrapper.domain.tgchat.jpa.JpaChatRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaLinkService implements LinkService {
    private static final String UNREGISTERED = "Указанный чат не зарегистрирован";
    private final JpaLinkRepository jpaLinkRepository;
    private final JpaChatRepository jpaChatRepository;

    @Override
    public ListLinkResponse getFollowedLinks(Long tgChatId) {
        List<LinkResponse> linkResponses = jpaLinkRepository.findAllByChats_Id(tgChatId).stream()
            .map(LinkMapper::linkEntityToLinkResponse)
            .toList();

        return new ListLinkResponse(linkResponses, linkResponses.size());
    }

    @Override
    @Transactional
    public LinkResponse addLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        Chat chat = jpaChatRepository.findById(tgChatId)
            .orElseThrow(() -> new ResourceNotFoundException(UNREGISTERED));

        URI url = addLinkRequest.link();
        if (jpaLinkRepository.existsByChats_IdAndUrl(tgChatId, url)) {
            throw new LinkAlreadyExistsException("Переданная ссылка уже отслеживается");
        }

        Link link = jpaLinkRepository.findByUrl(addLinkRequest.link())
            .orElseGet(() -> {
                Type type = getHostType(url);
                Link linkToAdd = new Link();
                linkToAdd.setUrl(url);
                linkToAdd.setType(type);

                return jpaLinkRepository.save(linkToAdd);
            });

        chat.addLink(link);
        jpaChatRepository.save(chat);
        jpaLinkRepository.save(link);

        return LinkMapper.linkEntityToLinkResponse(link);
    }

    @Override
    @Transactional
    public LinkResponse removeLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        Chat chat = jpaChatRepository.findById(tgChatId)
            .orElseThrow(() -> new ResourceNotFoundException(UNREGISTERED));

        URI url = removeLinkRequest.link();
        Link link = jpaLinkRepository.findByUrl(url)
            .orElseThrow(() -> new ResourceNotFoundException("Указанная ссылка не существует в системе"));

        if (!jpaLinkRepository.existsByChats_IdAndUrl(tgChatId, url)) {
            throw new ResourceNotFoundException("Указанная ссылка не отслеживается");
        }

        chat.removeLink(link);
        jpaChatRepository.save(chat);
        jpaLinkRepository.save(link);
        jpaLinkRepository.deleteAllByChatsEmpty();

        return LinkMapper.linkEntityToLinkResponse(link);
    }
}
