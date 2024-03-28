package edu.java.bot.telegram.service;

import edu.java.bot.client.scrapper.ScrapperClient;
import edu.java.bot.client.scrapper.dto.AddLinkRequest;
import edu.java.bot.client.scrapper.dto.LinkResponse;
import edu.java.bot.client.scrapper.dto.RemoveLinkRequest;
import edu.java.bot.telegram.service.linkvalidator.LinkValidator;
import edu.java.bot.telegram.service.linkvalidator.StackoverflowLinkValidator;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkValidatorServiceTest {
    @Mock
    ScrapperClient scrapperClient;

    @Mock
    List<LinkValidator> linkValidators;

    @InjectMocks
    LinkHandlerService linkHandlerService;

    @Test
    @DisplayName("Проверка успешного выполнения антрека")
    void untrackLinkSuccess() {
        // given
        URI uri = URI.create("https://aboba.com/repository");
        when(scrapperClient.removeLink(1L, new RemoveLinkRequest(uri))).thenReturn(new LinkResponse(1L, uri));

        // when
        String actualResponse = linkHandlerService.untrackLink(uri, 1L);

        // then
        assertThat(actualResponse).isEqualTo(LinkHandlersConstants.UNTRACKED.formatted(uri));
    }

    @Test
    @DisplayName("Проверка успешного выполнения трека")
    void trackLinkSuccess() {
        // given
        URI uri = URI.create(
            "https://stackoverflow.com/questions/46502450/interfaces-are-annotated-with-component-annotation-in-spring-ioc-di-what-could");
        StackoverflowLinkValidator stackoverflowLinkValidator = new StackoverflowLinkValidator();
        when(linkValidators.stream()).thenReturn(Stream.of(stackoverflowLinkValidator));
        when(scrapperClient.addLink(
            1L,
            new AddLinkRequest(uri)
        )).thenReturn(new LinkResponse(1L, uri));

        // when
        String actualResponse = linkHandlerService.trackLink(
            "https://stackoverflow.com/questions/46502450/interfaces-are-annotated-with-component-annotation-in-spring-ioc-di-what-could",
            1L
        );

        // then
        assertThat(actualResponse).isEqualTo(LinkHandlersConstants.NOW_TRACKING.formatted(
            "https://stackoverflow.com/questions/46502450/interfaces-are-annotated-with-component-annotation-in-spring-ioc-di-what-could"));
    }

    @Test
    @DisplayName("Проверка трека для непподерживаемого ресурса")
    void trackLinkIncapable() {
        // when
        String actualResponse = linkHandlerService.trackLink("https://aboba.com/repository", 1L);

        // then
        assertThat(actualResponse).isEqualTo(LinkHandlersConstants.CURRENTLY_INCAPABLE);
    }

    @Test
    @DisplayName("Проверка для неверного формата ссылки")
    void trackLinkWrongUrlFormat() {
        // when
        String actualResponse = linkHandlerService.trackLink("hello world", 1L);

        // then
        assertThat(actualResponse).isEqualTo(LinkHandlersConstants.WRONG_URL_FORMAT);
    }

    @Test
    @DisplayName("Проверка трека для не http ссылки")
    void trackLinkNotHttpSource() {
        // when
        String actualResponse =
            linkHandlerService.trackLink("ftp://public.ftp-servers.example.com/mydirectory/myfile.txt", 1L);

        // then
        assertThat(actualResponse).isEqualTo(LinkHandlersConstants.NOT_HTTP_RESOURCE);
    }
}
