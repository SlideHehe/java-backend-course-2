package edu.java.scrapper.scheduler.resourceupdater;

import edu.java.scrapper.domain.links.schemabased.Link;
import edu.java.scrapper.domain.links.Type;
import edu.java.scrapper.client.stackoverflow.StackoverflowClient;
import edu.java.scrapper.client.stackoverflow.dto.StackoverflowAnswers;
import edu.java.scrapper.client.stackoverflow.dto.StackoverflowComments;
import edu.java.scrapper.client.stackoverflow.dto.StackoverflowQuestion;
import edu.java.scrapper.scheduler.UpdateInfo;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StackoverflowResourceUpdaterTest {
    @Mock
    StackoverflowClient stackoverflowClient;
    @InjectMocks
    StackoverflowResourceUpdater stackoverflowResourceUpdater;

    @Test
    @DisplayName("Проверка метода supports для подходящей ссылки")
    void supportsTrue() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        Link link = new Link(
            1L,
            URI.create(
                "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records"),
            time,
            time,
            Type.STACKOVERFLOW,
            null,
            null,
            null,
            null
        );

        // when-then
        assertThat(stackoverflowResourceUpdater.supports(link)).isTrue();
    }

    @Test
    @DisplayName("Проверка метода supports для неподходящей ссылки")
    void supportsFalse() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        Link link = new Link(
            1L,
            URI.create("https://stackoverflow.com/users"),
            time,
            time,
            Type.STACKOVERFLOW,
            null,
            null,
            null,
            null
        );

        // when-then
        assertThat(stackoverflowResourceUpdater.supports(link)).isFalse();
    }

    @Test
    @DisplayName("Проверка получения необрабатываемого обновления")
    void getUpdatesUnknown() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        URI uri = URI.create(
            "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records");
        Link link = new Link(1L, uri, time.minusMinutes(10), time, Type.STACKOVERFLOW, null, null, null, null);
        StackoverflowQuestion question = new StackoverflowQuestion(List.of(new StackoverflowQuestion.Item(
            "aboba",
            "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records",
            time.minusMinutes(5)
        )));
        when(stackoverflowClient.getQuestion(61719589L)).thenReturn(question);
        when(stackoverflowClient.getAnswers(61719589L)).thenReturn(new StackoverflowAnswers(List.of()));
        when(stackoverflowClient.getComments(61719589L)).thenReturn(new StackoverflowComments(List.of()));
        Optional<UpdateInfo> expectedUpdateInfo =
            Optional.of(new UpdateInfo(
                new Link(1L, uri, time.minusMinutes(5), time, Type.STACKOVERFLOW, 0, 0, null, null),
                ResourceUpdaterConstants.STACKOVERFLOW_UPDATE_RESPONSE.formatted(
                    "aboba")
            ));

        // when
        Optional<UpdateInfo> actualUpdateInfo = stackoverflowResourceUpdater.updateResource(link);

        // then
        assertThat(actualUpdateInfo).isEqualTo(expectedUpdateInfo);
    }

    @Test
    @DisplayName("Проверка получения нового ответа")
    void getUpdatesNewAnswer() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        URI uri = URI.create(
            "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records");
        Link link = new Link(1L, uri, time.minusMinutes(10), time, Type.STACKOVERFLOW, null, null, null, null);
        StackoverflowQuestion question = new StackoverflowQuestion(List.of(new StackoverflowQuestion.Item(
            "aboba",
            "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records",
            time.minusMinutes(5)
        )));
        when(stackoverflowClient.getQuestion(61719589L)).thenReturn(question);
        when(stackoverflowClient.getAnswers(61719589L)).thenReturn(new StackoverflowAnswers(List.of(
            new StackoverflowAnswers.Item(new StackoverflowAnswers.Item.Owner("aaa"), time)
        )));
        when(stackoverflowClient.getComments(61719589L)).thenReturn(new StackoverflowComments(List.of()));
        Optional<UpdateInfo> expectedUpdateInfo =
            Optional.of(new UpdateInfo(
                new Link(1L, uri, time.minusMinutes(5), time, Type.STACKOVERFLOW, 1, 0, null, null),
                ResourceUpdaterConstants.STACKOVERFLOW_UPDATE_RESPONSE.formatted(
                    "aboba")
                + ResourceUpdaterConstants.STACKOVERFLOW_NEW_ANSWER.formatted("aaa")
            ));

        // when
        Optional<UpdateInfo> actualUpdateInfo = stackoverflowResourceUpdater.updateResource(link);

        // then
        assertThat(actualUpdateInfo).isEqualTo(expectedUpdateInfo);
    }

    @Test
    @DisplayName("Проверка получения удаленного ответа")
    void getUpdatesDeletedAnswer() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        URI uri = URI.create(
            "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records");
        Link link = new Link(1L, uri, time.minusMinutes(10), time, Type.STACKOVERFLOW, 1, null, null, null);
        StackoverflowQuestion question = new StackoverflowQuestion(List.of(new StackoverflowQuestion.Item(
            "aboba",
            "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records",
            time.minusMinutes(5)
        )));
        when(stackoverflowClient.getQuestion(61719589L)).thenReturn(question);
        when(stackoverflowClient.getAnswers(61719589L)).thenReturn(new StackoverflowAnswers(List.of()));
        when(stackoverflowClient.getComments(61719589L)).thenReturn(new StackoverflowComments(List.of()));
        Optional<UpdateInfo> expectedUpdateInfo =
            Optional.of(new UpdateInfo(
                new Link(1L, uri, time.minusMinutes(5), time, Type.STACKOVERFLOW, 0, 0, null, null),
                ResourceUpdaterConstants.STACKOVERFLOW_UPDATE_RESPONSE.formatted(
                    "aboba")
                + ResourceUpdaterConstants.STACKOVERFLOW_ANSWER_DELETED
            ));

        // when
        Optional<UpdateInfo> actualUpdateInfo = stackoverflowResourceUpdater.updateResource(link);

        // then
        assertThat(actualUpdateInfo).isEqualTo(expectedUpdateInfo);
    }

    @Test
    @DisplayName("Проверка получения нового комментария")
    void getUpdatesNewComment() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        URI uri = URI.create(
            "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records");
        Link link = new Link(1L, uri, time.minusMinutes(10), time, Type.STACKOVERFLOW, null, null, null, null);
        StackoverflowQuestion question = new StackoverflowQuestion(List.of(new StackoverflowQuestion.Item(
            "aboba",
            "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records",
            time.minusMinutes(5)
        )));
        when(stackoverflowClient.getQuestion(61719589L)).thenReturn(question);
        when(stackoverflowClient.getAnswers(61719589L)).thenReturn(new StackoverflowAnswers(List.of()));
        when(stackoverflowClient.getComments(61719589L)).thenReturn(new StackoverflowComments(List.of(
            new StackoverflowComments.Item(new StackoverflowComments.Item.Owner("aaa"), time)
        )));
        Optional<UpdateInfo> expectedUpdateInfo =
            Optional.of(new UpdateInfo(
                new Link(1L, uri, time.minusMinutes(5), time, Type.STACKOVERFLOW, 0, 1, null, null),
                ResourceUpdaterConstants.STACKOVERFLOW_UPDATE_RESPONSE.formatted(
                    "aboba")
                + ResourceUpdaterConstants.STACKOVERFLOW_NEW_COMMENT.formatted("aaa")
            ));

        // when
        Optional<UpdateInfo> actualUpdateInfo = stackoverflowResourceUpdater.updateResource(link);

        // then
        assertThat(actualUpdateInfo).isEqualTo(expectedUpdateInfo);
    }

    @Test
    @DisplayName("Проверка получения удаленного ответа")
    void getUpdatesDeletedComment() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        URI uri = URI.create(
            "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records");
        Link link = new Link(1L, uri, time.minusMinutes(10), time, Type.STACKOVERFLOW, null, 1, null, null);
        StackoverflowQuestion question = new StackoverflowQuestion(List.of(new StackoverflowQuestion.Item(
            "aboba",
            "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records",
            time.minusMinutes(5)
        )));
        when(stackoverflowClient.getQuestion(61719589L)).thenReturn(question);
        when(stackoverflowClient.getAnswers(61719589L)).thenReturn(new StackoverflowAnswers(List.of()));
        when(stackoverflowClient.getComments(61719589L)).thenReturn(new StackoverflowComments(List.of()));
        Optional<UpdateInfo> expectedUpdateInfo =
            Optional.of(new UpdateInfo(
                new Link(1L, uri, time.minusMinutes(5), time, Type.STACKOVERFLOW, 0, 0, null, null),
                ResourceUpdaterConstants.STACKOVERFLOW_UPDATE_RESPONSE.formatted(
                    "aboba")
                + ResourceUpdaterConstants.STACKOVERFLOW_COMMENT_DELETED
            ));

        // when
        Optional<UpdateInfo> actualUpdateInfo = stackoverflowResourceUpdater.updateResource(link);

        // then
        assertThat(actualUpdateInfo).isEqualTo(expectedUpdateInfo);
    }

    @Test
    @DisplayName("Отсутствия обновления для ссылки")
    void noUpdatesForLink() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        URI uri = URI.create(
            "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records");
        Link link = new Link(1L, uri, time, time, Type.STACKOVERFLOW, null, null, null, null);
        Optional<UpdateInfo> expectedUpdateInfo = Optional.empty();
        StackoverflowQuestion question = new StackoverflowQuestion(List.of(new StackoverflowQuestion.Item(
            "aboba",
            "https://stackoverflow.com/questions/61719589/do-you-need-to-override-hashcode-and-equals-for-records",
            time
        )));
        when(stackoverflowClient.getQuestion(61719589L)).thenReturn(question);

        // when
        Optional<UpdateInfo> actualUpdateInfo = stackoverflowResourceUpdater.updateResource(link);

        // then
        assertThat(actualUpdateInfo).isEqualTo(expectedUpdateInfo);
    }
}
