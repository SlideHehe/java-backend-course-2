package edu.java.scrapper.domain.links.jpa;

import edu.java.scrapper.domain.exception.LinkAlreadyExistsException;
import edu.java.scrapper.domain.exception.ResourceNotFoundException;
import edu.java.scrapper.domain.links.dto.AddLinkRequest;
import edu.java.scrapper.domain.links.dto.LinkResponse;
import edu.java.scrapper.domain.links.dto.ListLinkResponse;
import edu.java.scrapper.domain.links.dto.RemoveLinkRequest;
import edu.java.scrapper.domain.tgchat.jpa.Chat;
import edu.java.scrapper.domain.tgchat.jpa.JpaChatRepository;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaLinkServiceTest {
    @Mock
    JpaChatRepository chatRepository;
    @Mock
    JpaLinkRepository linkRepository;
    @InjectMocks
    JpaLinkService linkService;

    @Test
    @DisplayName("Получение отслеживаемых ссылок")
    void getFollowedLinks() {
        // given
        Link link1 = new Link();
        link1.setId(1L);
        link1.setUrl(URI.create("https://github.com/1"));
        Link link2 = new Link();
        link2.setId(2L);
        link2.setUrl(URI.create("https://github.com/2"));
        List<Link> links = List.of(link1, link2);
        when(linkRepository.findAllByChats_Id(1L)).thenReturn(links);
        ListLinkResponse expectedResponse = new ListLinkResponse(
            List.of(
                new LinkResponse(1L, URI.create("https://github.com/1")),
                new LinkResponse(2L, URI.create("https://github.com/2"))
            ),
            2
        );

        // when
        ListLinkResponse actualResponse = linkService.getFollowedLinks(1L);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Получения отслеживаемых ссылок для несуществующего чата")
    void getFollowedLinksForNonExistingChat() {
        // given
        when(linkRepository.findAllByChats_Id(1L)).thenReturn(List.of());
        ListLinkResponse expectedList = new ListLinkResponse(List.of(), 0);

        // when
        ListLinkResponse actualList = linkService.getFollowedLinks(1L);

        // then
        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    @DisplayName("Проверка добавления ссылки")
    void addLink() {
        // given
        URI uri = URI.create("https://github.com");
        Link link = new Link();
        link.setId(1L);
        link.setUrl(uri);
        Chat chat = new Chat();
        chat.setId(1L);
        chat.addLink(link);
        when(linkRepository.findByUrl(uri)).thenReturn(Optional.empty());
        when(linkRepository.save(any(Link.class))).thenReturn(link);
        when(linkRepository.existsByChats_IdAndUrl(1L, uri)).thenReturn(false);
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        LinkResponse expectedResponse = new LinkResponse(1L, uri);

        // when
        LinkResponse actualResponse = linkService.addLink(1L, new AddLinkRequest(uri));

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Проверка добавления уже существующей ссылки")
    void addLinkRepeated() {
        // given
        URI uri = URI.create("https://github.com");
        when(chatRepository.findById(1L)).thenReturn(Optional.of(new Chat()));
        when(linkRepository.existsByChats_IdAndUrl(1L, uri)).thenReturn(true);
        AddLinkRequest addLinkRequest = new AddLinkRequest(uri);

        // when-then
        assertThatThrownBy(() -> linkService.addLink(1L, addLinkRequest))
            .isInstanceOf(LinkAlreadyExistsException.class)
            .hasMessage("Переданная ссылка уже отслеживается");
    }

    @Test
    @DisplayName("Проверка добавления ссылки для незарегистрированного чата")
    void addLinkUnregistered() {
        // given
        URI uri = URI.create("https://github.com");
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());
        AddLinkRequest addLinkRequest = new AddLinkRequest(uri);

        // when-then
        assertThatThrownBy(() -> linkService.addLink(1L, addLinkRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанный чат не зарегистрирован");
    }

    @Test
    @DisplayName("Проверка удаления ссылки для незарегистрированного чата")
    void removeLinkUnregistered() {
        // given
        URI uri = URI.create("https://github.com");
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(uri);

        // when-then
        assertThatThrownBy(() -> linkService.removeLink(1L, removeLinkRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанный чат не зарегистрирован");
    }

    @Test
    @DisplayName("Проверка удаления ссылки")
    void removeLink() {
        // given
        URI uri = URI.create("https://github.com");
        Link link = new Link();
        link.setId(1L);
        link.setUrl(uri);
        Chat chat = new Chat();
        chat.setId(1L);
        chat.addLink(link);
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(linkRepository.findByUrl(uri)).thenReturn(Optional.of(link));
        when(linkRepository.existsByChats_IdAndUrl(1L, uri)).thenReturn(true);
        LinkResponse expectedResponse = new LinkResponse(1L, uri);

        // when
        LinkResponse actualResponse = linkService.removeLink(1L, new RemoveLinkRequest(uri));

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Проверка удаления несуществующей ссылки")
    void removeNonExistingLink() {
        // given
        URI uri = URI.create("https://github.com");
        when(chatRepository.findById(1L)).thenReturn(Optional.of(new Chat()));
        when(linkRepository.findByUrl(uri)).thenReturn(Optional.empty());
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(uri);

        // when-then
        assertThatThrownBy(() -> linkService.removeLink(1L, removeLinkRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанная ссылка не существует в системе");
    }

    @Test
    @DisplayName("Проверка удаления неотслеживаемой ссылки")
    void removeNonTrackedLink() {
        // given
        URI uri = URI.create("https://github.com");
        Link link = new Link();
        link.setId(1L);
        link.setUrl(uri);
        Chat chat = new Chat();
        chat.setId(1L);
        chat.addLink(link);
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(linkRepository.findByUrl(uri)).thenReturn(Optional.of(link));
        when(linkRepository.existsByChats_IdAndUrl(1L, uri)).thenReturn(false);
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(uri);

        // when-then
        assertThatThrownBy(() -> linkService.removeLink(1L, removeLinkRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанная ссылка не отслеживается");
    }
}
