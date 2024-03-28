package edu.java.scrapper.api.chatlink.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.api.chatlink.ChatLink;
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
class JdbcChatLinkDaoTest extends IntegrationTest {
    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JdbcChatLinkDao jdbcChatLinkDao;

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск всех записей")
    void findAll() {
        // given
        jdbcClient.sql("insert into link (id, url) values "
            + "(1, 'https://aboba1.com'),"
            + "(2, 'https://aboba2.com'),"
            + "(3, 'https://aboba3.com')"
        ).update();
        jdbcClient.sql("insert into chat (id) values (1), (2), (3);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (1, 1), (2, 2), (3, 3);").update();
        List<ChatLink> expectedList = List.of(
            new ChatLink(1L, 1L),
            new ChatLink(2L, 2L),
            new ChatLink(3L, 3L)
        );

        // when
        List<ChatLink> actualList = jdbcChatLinkDao.findAll();

        // then
        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Выборка списка из пустой таблицы")
    void findAllEmptyTable() {
        // when-then
        assertThat(jdbcChatLinkDao.findAll()).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по первичному ключу")
    void findById() {
        // given
        jdbcClient.sql("insert into link (id, url) values "
            + "(1, 'https://aboba1.com'),"
            + "(2, 'https://aboba2.com')"
        ).update();
        jdbcClient.sql("insert into chat (id) values (1), (2);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (1, 1), (2, 2);").update();
        Optional<ChatLink> expectedChatLink = Optional.of(new ChatLink(1L, 1L));

        // when
        Optional<ChatLink> actualChatLink = jdbcChatLinkDao.findById(1L, 1L);

        // then
        assertThat(actualChatLink).isEqualTo(expectedChatLink);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по несуществующему ключу")
    void findByNonexistentId() {
        // when-then
        assertThat(jdbcChatLinkDao.findById(1L, 1L)).isNotPresent();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Добавление элемента")
    void add() {
        // given
        jdbcClient.sql("insert into link (id, url) values (1, 'https://aboba1.com')").update();
        jdbcClient.sql("insert into chat (id) values (1);").update();

        // when
        jdbcChatLinkDao.add(1L, 1L);
        Boolean result = jdbcClient.sql("select exists(select 1 from chat_link where link_id = 1 and chat_id = 1)")
            .query(Boolean.class).single();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Повторное добавление записи")
    void addExistingRecord() {
        // given
        jdbcClient.sql("insert into link (id, url) values (1, 'https://aboba1.com')").update();
        jdbcClient.sql("insert into chat (id) values (1);").update();
        jdbcChatLinkDao.add(1L, 1L);

        // when
        assertThatThrownBy(() -> jdbcChatLinkDao.add(1L, 1L)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление записи")
    void remove() {
        // given
        jdbcClient.sql("insert into link (id, url) values (1, 'https://aboba1.com')").update();
        jdbcClient.sql("insert into chat (id) values (1);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (1, 1);").update();

        // when
        jdbcChatLinkDao.remove(1L, 1L);

        // then
        Boolean result = jdbcClient.sql("select exists(select 1 from chat_link where link_id = 1 and chat_id = 1)")
            .query(Boolean.class).single();
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление несуществующей записи")
    void removeNonexistent() {
        // when-then
        assertThatThrownBy(() -> jdbcChatLinkDao.remove(1L, 1L)).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление неотслеживаемых никем ссылок")
    void removeDanglingLinks() {
        // given
        jdbcClient.sql("insert into link (id, url) values (1, 'https://aboba1.com')").update();

        // when
        jdbcChatLinkDao.removeDanglingLinks();

        // then
        Boolean result = jdbcClient.sql("select exists(select 1 from link)")
            .query(Boolean.class).single();
        assertThat(result).isFalse();
    }
}
