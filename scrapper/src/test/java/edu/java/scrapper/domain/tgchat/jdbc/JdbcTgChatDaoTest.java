package edu.java.scrapper.domain.tgchat.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.tgchat.TgChat;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JdbcTgChatDaoTest extends IntegrationTest {
    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JdbcTgChatDao jdbcTgChatDao;

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск всех записей")
    void findAll() {
        // given
        OffsetDateTime time = OffsetDateTime.parse("2024-03-13T18:27:34.389Z");
        jdbcClient.sql("insert into chat (id, created_at) values "
            + "(1, timestamp with time zone '2024-03-13T18:27:34.389Z'),"
            + "(2, timestamp with time zone '2024-03-13T18:27:34.389Z'),"
            + "(3, timestamp with time zone '2024-03-13T18:27:34.389Z')"
        ).update();
        List<TgChat> expectedList = List.of(
            new TgChat(1L, time),
            new TgChat(2L, time),
            new TgChat(3L, time)
        );

        // when
        List<TgChat> actualList = jdbcTgChatDao.findAll();

        // then
        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Выборка списка из пустой таблицы")
    void findAllEmptyTable() {
        // when-then
        assertThat(jdbcTgChatDao.findAll()).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по первичному ключу")
    void findById() {
        // given
        OffsetDateTime time = OffsetDateTime.parse("2024-03-13T18:27:34.389Z");
        jdbcClient.sql(
            "insert into chat (id, created_at) values (1, timestamp with time zone '2024-03-13T18:27:34.389Z')"
        ).update();
        Optional<TgChat> expectedChat = Optional.of(new TgChat(1L, time));

        // when
        Optional<TgChat> actualChat = jdbcTgChatDao.findById(1L);

        // then
        assertThat(actualChat).isEqualTo(expectedChat);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по несуществующему ключу")
    void findByNonexistentId() {
        // when-then
        assertThat(jdbcTgChatDao.findById(1L)).isNotPresent();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Добавление элемента")
    void add() {
        // when
        jdbcTgChatDao.add(1L);
        Boolean result =
            jdbcClient.sql("select exists(select 1 from chat where chat.id = 1)").query(Boolean.class).single();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Повторное добавление записи")
    void addExistingRecord() {
        // given
        jdbcTgChatDao.add(1L);

        // when-then
        assertThatThrownBy(() -> jdbcTgChatDao.add(1L)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление записи")
    void remove() {
        // given
        jdbcClient.sql("insert into chat (id) values (1)").update();

        // when
        jdbcTgChatDao.remove(1L);
        Boolean result =
            jdbcClient.sql("select exists(select 1 from chat where chat.id = 1)").query(Boolean.class).single();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление несуществующей записи")
    void removeNonexistent() {
        // when-then
        assertThatThrownBy(() -> jdbcTgChatDao.remove(1L)).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Получение ссылок, отслеживаемых пользователем")
    void findAllByLinkId() {
        // given
        OffsetDateTime time = OffsetDateTime.parse("2024-03-13T18:27:34.389Z");
        jdbcClient.sql("insert into link (id, url, type) values "
                       + "(1, 'https://aboba1.com', 'GITHUB'), (2, 'https://aboba2.com', 'GITHUB')"
        ).update();
        jdbcClient.sql("insert into chat (id, created_at) values "
            + "(1, timestamp with time zone '2024-03-13T18:27:34.389Z'), "
            + "(2, timestamp with time zone '2024-03-13T18:27:34.389Z');").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (1, 1);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (1, 2);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (2, 2);").update();
        List<TgChat> expectedList = List.of(
            new TgChat(1L, time)
        );

        // when
        List<TgChat> actualList = jdbcTgChatDao.findAllByLinkId(1L);

        // then
        assertThat(actualList).isEqualTo(expectedList);
    }
}
