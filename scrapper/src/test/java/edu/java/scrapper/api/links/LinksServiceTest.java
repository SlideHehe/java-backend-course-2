package edu.java.scrapper.api.links;

import edu.java.scrapper.api.exception.LinkAlreadyExistsException;
import edu.java.scrapper.api.exception.ResourceNotFoundException;
import edu.java.scrapper.api.links.dto.AddLinkRequest;
import edu.java.scrapper.api.links.dto.LinkResponse;
import edu.java.scrapper.api.links.dto.ListLinkResponse;
import edu.java.scrapper.api.links.dto.RemoveLinkRequest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LinksServiceTest {
    @Test
    @DisplayName("Получение отслеживаемых ссылок")
    void getFollowedLinks() {
        // given
        AddLinkRequest request1 = new AddLinkRequest(URI.create("https://aboba.com/1"));
        AddLinkRequest request2 = new AddLinkRequest(URI.create("https://aboba.com/2"));
        AddLinkRequest request3 = new AddLinkRequest(URI.create("https://aboba.com/3"));
        LinksService linksService = new LinksService();
        linksService.addLink(1L, request1);
        linksService.addLink(1L, request2);
        linksService.addLink(1L, request3);
        ListLinkResponse expectedResponse = new ListLinkResponse(
            List.of(
                new LinkResponse(1L, URI.create("https://aboba.com/1")),
                new LinkResponse(2L, URI.create("https://aboba.com/2")),
                new LinkResponse(3L, URI.create("https://aboba.com/3"))
            ),
            3
        );

        // when
        ListLinkResponse actualResponse = linksService.getFollowedLinks(1L);

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Получения отслеживаемых ссылок для несуществующего чата")
    void getFollowedLinksForNonExistingChat() {
        // given
        LinksService linksService = new LinksService();

        // when-then
        assertThatThrownBy(() -> linksService.getFollowedLinks(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанный чат не зарегестрирован");
    }

    @Test
    @DisplayName("Проверка добавления ссылки")
    void addLink() {
        // given
        LinksService linksService = new LinksService();
        LinkResponse expectedResponse = new LinkResponse(1L, URI.create("https://aboba.com"));

        // when
        LinkResponse actualResponse = linksService.addLink(1L, new AddLinkRequest(URI.create("https://aboba.com")));

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Проверка добавления уже существующей ссылки")
    void addLinkRepeated() {
        // given
        LinksService linksService = new LinksService();
        AddLinkRequest addLinkRequest = new AddLinkRequest(URI.create("https://aboba.com"));
        linksService.addLink(1L, addLinkRequest);

        // when-then
        assertThatThrownBy(() -> linksService.addLink(1L, addLinkRequest))
            .isInstanceOf(LinkAlreadyExistsException.class)
            .hasMessage("Переданная ссылка уже обрабатывается");
    }

    @Test
    @DisplayName("Проверка удаления ссылки")
    void removeLink() {
        // given
        LinksService linksService = new LinksService();
        AddLinkRequest addLinkRequest = new AddLinkRequest(URI.create("https://aboba.com"));
        linksService.addLink(1L, addLinkRequest);
        LinkResponse expectedResponse = new LinkResponse(1L, URI.create("https://aboba.com"));

        // when
        LinkResponse actualResponse =
            linksService.removeLink(1L, new RemoveLinkRequest(URI.create("https://aboba.com")));

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Проверка удаления неотслеживаемой ссылки")
    void removeNonExistingLink() {
        // given
        LinksService linksService = new LinksService();
        linksService.addLink(1L, new AddLinkRequest(URI.create("https://not-aboba.com")));

        // when-then
        assertThatThrownBy(() -> linksService.removeLink(1L, new RemoveLinkRequest(URI.create("https://aboba.com"))))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанная ссылка не отслеживается");
    }

    @Test
    @DisplayName("Проверка удаления ссылки из незарегистрированного чата")
    void removeLinkUnregisteredChat() {
        // given
        LinksService linksService = new LinksService();

        // when-then
        assertThatThrownBy(() -> linksService.removeLink(1L, new RemoveLinkRequest(URI.create("https://aboba.com"))))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Указанный чат не зарегестрирован");
    }
}
