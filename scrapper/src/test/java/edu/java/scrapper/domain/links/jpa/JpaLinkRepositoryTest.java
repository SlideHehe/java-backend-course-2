package edu.java.scrapper.domain.links.jpa;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.links.Type;
import edu.java.scrapper.domain.tgchat.jpa.Chat;
import edu.java.scrapper.domain.tgchat.jpa.JpaChatRepository;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JpaLinkRepositoryTest extends IntegrationTest {
    @Autowired
    JpaLinkRepository linkRepository;
    @Autowired
    JpaChatRepository chatRepository;
    @Autowired
    JdbcClient jdbcClient;

    @BeforeEach
    public void resetSequence() {
        jdbcClient.sql("truncate link cascade").update();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Проверка удаления ссылок, не привязанных к чату")
    void deleteAllByChatsEmpty() {
        // given
        Link linkWithChat = new Link();
        linkWithChat.setUrl(URI.create("http://localhost"));
        linkWithChat.setType(Type.STACKOVERFLOW);
        linkWithChat = linkRepository.save(linkWithChat);
        Chat chat = new Chat();
        chat.setId(1L);
        chat = chatRepository.save(chat);
        chat.addLink(linkWithChat);
        chatRepository.save(chat);
        linkWithChat = linkRepository.save(linkWithChat);
        Link linkWithoutChat = new Link();
        linkWithoutChat.setType(Type.STACKOVERFLOW);
        linkWithoutChat.setUrl(URI.create("http://localhost2"));
        linkWithoutChat = linkRepository.save(linkWithoutChat);

        // when
        linkRepository.deleteAllByChatsEmpty();

        // then
        assertThat(linkRepository.existsById(linkWithChat.getId())).isTrue();
        assertThat(linkRepository.existsById(linkWithoutChat.getId())).isFalse();

    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск ссылок привязанных к определенному чату")
    void findAllByChats_Id() {
        // given
        Link linkWithChat = new Link();
        linkWithChat.setUrl(URI.create("http://localhost"));
        linkWithChat.setType(Type.STACKOVERFLOW);
        linkWithChat = linkRepository.save(linkWithChat);
        Chat chat = new Chat();
        chat.setId(1L);
        chat = chatRepository.save(chat);
        chat.addLink(linkWithChat);
        chatRepository.save(chat);
        linkWithChat = linkRepository.save(linkWithChat);
        Link linkWithoutChat = new Link();
        linkWithoutChat.setType(Type.STACKOVERFLOW);
        linkWithoutChat.setUrl(URI.create("http://localhost2"));
        linkRepository.save(linkWithoutChat);

        // when
        var actualList = linkRepository.findAllByChats_Id(1L);

        // then
        assertThat(actualList).hasSize(1);
        assertThat(actualList.getFirst().getId()).isEqualTo(linkWithChat.getId());
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск ссылок по URI")
    void findByUrl() {
        // given
        URI uri = URI.create("http://localhost");
        Link expectedLink = new Link();
        expectedLink.setUrl(uri);
        expectedLink.setType(Type.STACKOVERFLOW);
        expectedLink = linkRepository.save(expectedLink);

        // when
        var actualLink = linkRepository.findByUrl(uri);

        // then
        assertThat(actualLink.get().getId()).isEqualTo(expectedLink.getId());
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Проверка существования ссылки по URI, привязанной к определенному чату")
    void existsByChats_IdAndUrl() {
        // given
        URI uri = URI.create("http://localhost");
        Link linkWithChat = new Link();
        linkWithChat.setUrl(uri);
        linkWithChat.setType(Type.STACKOVERFLOW);
        linkWithChat = linkRepository.save(linkWithChat);
        Chat chat = new Chat();
        chat.setId(1L);
        chat = chatRepository.save(chat);
        chat.addLink(linkWithChat);
        chatRepository.save(chat);
        linkRepository.save(linkWithChat);

        // when
        boolean exists = linkRepository.existsByChats_IdAndUrl(1L, uri);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск ссылок, которые давно не проверялись")
    void findByCheckedMoreThanSomeSecondsAgo() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        jdbcClient.sql("insert into link (id, url, type, checked_at) values "
                       + "(1, 'localhost1', 'GITHUB', ?), "
                       + "(2, 'localhost2', 'GITHUB', ?);")
            .params(time.minusMinutes(10L), time)
            .update(); // я не выдержал возвращение кеширующих значений от хибернейта без дат, которые бд генерирует

        // when
        var actualList = linkRepository.findByCheckedMoreThanSomeSecondsAgo(100L);

        // then
        assertThat(actualList).hasSize(1);
        assertThat(actualList.getFirst().getId()).isEqualTo(1L);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Обновление даты проверки")
    void updateCheckedAtByIdIn() {
        // given
        OffsetDateTime time = OffsetDateTime.now().minusMinutes(10L);
        jdbcClient.sql("insert into link (id, url, type, checked_at) values "
                       + "(1, 'localhost1', 'GITHUB', ?), "
                       + "(2, 'localhost2', 'GITHUB', ?);")
            .params(time, time)
            .update();

        // when
        linkRepository.updateCheckedAtByIdIn(List.of(1L, 2L));
        Link link1 = linkRepository.findById(1L).get();
        Link link2 = linkRepository.findById(2L).get();

        // then
        assertThat(link1.getCheckedAt().isAfter(time)).isTrue();
        assertThat(link2.getCheckedAt().isAfter(time)).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Проверка обновления поля updateAt")
    void updateUpdatedAtById() {
        // given
        OffsetDateTime time = OffsetDateTime.now().minusMinutes(10L);
        jdbcClient.sql("insert into link (id, url, type, updated_at) values (1, 'localhost1', 'GITHUB', ?);")
            .param(time)
            .update();

        // when
        linkRepository.updateUpdatedAtById(time.plusMinutes(10L), 1L);
        Link link = linkRepository.findById(1L).get();

        // then
        assertThat(link.getUpdatedAt().isAfter(time)).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Проверка обновления счетиков")
    void updateCountersById() {
        // given
        OffsetDateTime time = OffsetDateTime.now().minusMinutes(10L);
        jdbcClient.sql("insert into link (id, url, type) values (1, 'localhost1', 'GITHUB');")
            .update();

        // when
        linkRepository.updateCountersById(null, null, 5, 5, 1L);
        Link link = linkRepository.findById(1L).get();

        // then
        assertThat(link.getAnswerCount()).isNull();
        assertThat(link.getCommentCount()).isNull();
        assertThat(link.getPullRequestCount()).isEqualTo(5);
        assertThat(link.getCommitCount()).isEqualTo(5);
    }
}
