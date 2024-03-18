package edu.java.scrapper.domain.links.schemabased;

import edu.java.scrapper.domain.chatlink.schemabased.ChatLink;
import edu.java.scrapper.domain.chatlink.schemabased.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.exception.LinkAlreadyExistsException;
import edu.java.scrapper.domain.exception.ResourceNotFoundException;
import edu.java.scrapper.domain.links.Type;
import edu.java.scrapper.domain.links.dto.AddLinkRequest;
import edu.java.scrapper.domain.links.dto.LinkResponse;
import edu.java.scrapper.domain.links.dto.ListLinkResponse;
import edu.java.scrapper.domain.links.dto.RemoveLinkRequest;
import edu.java.scrapper.domain.links.schemabased.Link;
import edu.java.scrapper.domain.links.schemabased.SchemaBasedLinkService;
import edu.java.scrapper.domain.links.schemabased.jdbc.JdbcLinkDao;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchemaBasedLinkServiceTest {
    @Mock
    JdbcLinkDao jdbcLinkDao;

    @Mock
    JdbcChatLinkDao jdbcChatLinkDao;

    @InjectMocks
    SchemaBasedLinkService linkService;

    @Test
    @DisplayName("Получение отслеживаемых ссылок")
    void getFollowedLinks() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        when(jdbcLinkDao.findAllByChatId(1L)).thenReturn(List.of(
            new Link(1L, URI.create("https://github.com/1"), time, time, Type.GITHUB, null, null, null, null),
            new Link(2L, URI.create("https://github.com/2"), time, time, Type.GITHUB, null, null, null, null),
            new Link(3L, URI.create("https://github.com/3"), time, time, Type.GITHUB, null, null, null, null)
        ));
        ListLinkResponse expectedResponse = new ListLinkResponse(
            List.of(
                new LinkResponse(1L, URI.create("https://github.com/1")),
                new LinkResponse(2L, URI.create("https://github.com/2")),
                new LinkResponse(3L, URI.create("https://github.com/3"))
            ),
            3
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
        when(jdbcLinkDao.findAllByChatId(1L)).thenReturn(List.of());
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
        when(jdbcLinkDao.findByUrl(uri)).thenReturn(Optional.empty());
        when(jdbcLinkDao.add(uri, Type.GITHUB)).thenReturn(new Link(
            1L,
            uri,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            Type.GITHUB,
            null,
            null,
            null,
            null
        ));
        when(jdbcChatLinkDao.findById(1L, 1L)).thenReturn(Optional.empty());
        when(jdbcChatLinkDao.add(1L, 1L)).thenReturn(new ChatLink(1L, 1L));
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
        when(jdbcLinkDao.findByUrl(uri)).thenReturn(Optional.empty());
        when(jdbcLinkDao.add(uri, Type.GITHUB)).thenReturn(new Link(
            1L,
            uri,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            Type.GITHUB,
            null,
            null,
            null,
            null
        ));
        when(jdbcChatLinkDao.findById(1L, 1L)).thenReturn(Optional.of(new ChatLink(1L, 1L)));
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
        when(jdbcLinkDao.findByUrl(uri)).thenReturn(Optional.empty());
        when(jdbcLinkDao.add(uri, Type.GITHUB)).thenReturn(new Link(
            1L,
            uri,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            Type.GITHUB,
            null,
            null,
            null,
            null
        ));
        when(jdbcChatLinkDao.findById(1L, 1L)).thenReturn(Optional.empty());
        when(jdbcChatLinkDao.add(1L, 1L)).thenThrow(DataIntegrityViolationException.class);
        AddLinkRequest addLinkRequest = new AddLinkRequest(uri);

        // when-then
        assertThatThrownBy(() -> linkService.addLink(1L, addLinkRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанный чат не зарегистрирован");
    }

    @Test
    @DisplayName("Проверка удаления ссылки")
    void removeLink() {
        // given
        URI uri = URI.create("https://github.com");
        when(jdbcLinkDao.findByUrl(uri)).thenReturn(Optional.of(new Link(
            1L,
            uri,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            Type.GITHUB,
            null,
            null,
            null,
            null
        )));
        when(jdbcChatLinkDao.findById(1L, 1L)).thenReturn(Optional.of(new ChatLink(1L, 1L)));
        when(jdbcChatLinkDao.remove(1L, 1L)).thenReturn(new ChatLink(1L, 1L));
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
        when(jdbcLinkDao.findByUrl(uri)).thenReturn(Optional.empty());
        // when-then
        assertThatThrownBy(() -> linkService.removeLink(1L, new RemoveLinkRequest(uri)))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанная не существует в системе");
    }

    @Test
    @DisplayName("Проверка удаления неотслеживаемой ссылки")
    void removeNonTrackedLink() {
        // given
        URI uri = URI.create("https://github.com");
        when(jdbcLinkDao.findByUrl(uri)).thenReturn(Optional.of(new Link(
            1L,
            uri,
            OffsetDateTime.now(),
            OffsetDateTime.now(),
            Type.GITHUB,
            null,
            null,
            null,
            null
        )));
        when(jdbcChatLinkDao.findById(1L, 1L)).thenReturn(Optional.empty());
        // when-then
        assertThatThrownBy(() -> linkService.removeLink(1L, new RemoveLinkRequest(uri)))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанная ссылка не отслеживается");
    }
}
