package edu.java.bot.telegram.linkhandler;

import edu.java.bot.telegram.link.Link;
import edu.java.bot.telegram.link.User;
import edu.java.bot.telegram.link.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LinkHandlerServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    List<LinkHandler> linkHandlers;

    @InjectMocks
    LinkHandlerService linkHandlerService;

    @Test
    @DisplayName("Проверка успешного выполнения антрека")
    void untrackLinkSuccess() {
        // given
        User user = new User(1L);
        Link link = new Link("aboba.com", "https://aboba.com/repository");
        user.addLink(link);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        String actualResponse = linkHandlerService.untrackLink("https://aboba.com/repository", 1L);

        // then
        assertThat(actualResponse).isEqualTo(LinkHandlersConstants.UNTRACKED);
    }

    @Test
    @DisplayName("Проверка антрека для ссылки, которую пользователь не отслеживает")
    void untrackLinkUnknownLink() {
        // given
        User user = new User(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        String actualResponse = linkHandlerService.untrackLink("https://aboba.com/repository", 1L);

        // then
        assertThat(actualResponse).isEqualTo(LinkHandlersConstants.NOT_TRACKING_YET);
    }

    @Test
    @DisplayName("Проверка успешного выполнения трека")
    void trackLinkSuccess() {
        // given
        User user = new User(1L);
        when(linkHandlers.stream()).thenReturn(Stream.of(new StackoverflowLinkHandler()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        String actualResponse = linkHandlerService.trackLink(
            "https://stackoverflow.com/questions/46502450/interfaces-are-annotated-with-component-annotation-in-spring-ioc-di-what-could",
            1L
        );

        // then
        assertThat(actualResponse).isEqualTo(LinkHandlersConstants.NOW_TRACKING);
    }

    @Test
    @DisplayName("Проверка трека для уже отслеживаемой ссылки")
    void trackLinkAlreadyTracking() {
        // given
        User user = new User(1L);
        Link link = new Link(
            "stackoverflow.com",
            "https://stackoverflow.com/questions/46502450/interfaces-are-annotated-with-component-annotation-in-spring-ioc-di-what-could"
        );
        user.addLink(link);
        when(linkHandlers.stream()).thenReturn(Stream.of(new StackoverflowLinkHandler()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        String actualResponse = linkHandlerService.trackLink(
            "https://stackoverflow.com/questions/46502450/interfaces-are-annotated-with-component-annotation-in-spring-ioc-di-what-could",
            1L
        );

        // then
        assertThat(actualResponse).isEqualTo(LinkHandlersConstants.ALREADY_TRACKING);
    }

    @Test
    @DisplayName("Проверка трека для непподерживаемого ресурса")
    void trackLinkIncapable() {
        // given
        User user = new User(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

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
