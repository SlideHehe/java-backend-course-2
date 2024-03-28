package edu.java.scrapper.domain.chatlink.schemabased;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.chatlink.schemabased.ChatLink;
import edu.java.scrapper.domain.chatlink.schemabased.ChatLinkDao;
import edu.java.scrapper.domain.chatlink.schemabased.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.chatlink.schemabased.jooq.JooqChatLinkDao;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.jooq.exception.NoDataFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@SpringBootTest
@TestInstance(PER_CLASS)
class ChatLinkDaoTest extends IntegrationTest {
    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JooqChatLinkDao jooqChatLinkDao;
    @Autowired
    JdbcChatLinkDao jdbcChatLinkDao;

    Stream<Arguments> chatLinkDaoProvider() {
        return Stream.of(
            Arguments.of(jooqChatLinkDao),
            Arguments.of(jdbcChatLinkDao)
        );
    }

    @BeforeEach
    public void resetSequence() {
        jdbcClient.sql("truncate link cascade").update();
    }

    @ParameterizedTest
    @MethodSource("chatLinkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Поиск всех записей")
    void findAll(ChatLinkDao chatLinkDao) {
        // given
        jdbcClient.sql("insert into link (id, url, type) values "
                       + "(1, 'https://aboba1.com', 'GITHUB'),"
                       + "(2, 'https://aboba2.com', 'GITHUB'),"
                       + "(3, 'https://aboba3.com', 'GITHUB')"
        ).update();
        jdbcClient.sql("insert into chat (id) values (1), (2), (3);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (1, 1), (2, 2), (3, 3);").update();
        List<ChatLink> expectedList = List.of(
            new ChatLink(1L, 1L),
            new ChatLink(2L, 2L),
            new ChatLink(3L, 3L)
        );

        // when
        List<ChatLink> actualList = chatLinkDao.findAll();

        // then
        assertThat(actualList).isEqualTo(expectedList);
    }

    @ParameterizedTest
    @MethodSource("chatLinkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Выборка списка из пустой таблицы")
    void findAllEmptyTable(ChatLinkDao chatLinkDao) {
        // when-then
        assertThat(chatLinkDao.findAll()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("chatLinkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по первичному ключу")
    void findById(ChatLinkDao chatLinkDao) {
        // given
        jdbcClient.sql("insert into link (id, url, type) values "
                       + "(1, 'https://aboba1.com', 'GITHUB'),"
                       + "(2, 'https://aboba2.com', 'GITHUB')"
        ).update();
        jdbcClient.sql("insert into chat (id) values (1), (2);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (1, 1), (2, 2);").update();
        Optional<ChatLink> expectedChatLink = Optional.of(new ChatLink(1L, 1L));

        // when
        Optional<ChatLink> actualChatLink = chatLinkDao.findById(1L, 1L);

        // then
        assertThat(actualChatLink).isEqualTo(expectedChatLink);
    }

    @ParameterizedTest
    @MethodSource("chatLinkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по несуществующему ключу")
    void findByNonexistentId(ChatLinkDao chatLinkDao) {
        // when-then
        assertThat(chatLinkDao.findById(1L, 1L)).isNotPresent();
    }

    @ParameterizedTest
    @MethodSource("chatLinkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Добавление элемента")
    void add(ChatLinkDao chatLinkDao) {
        // given
        jdbcClient.sql("insert into link (id, url, type) values (1, 'https://aboba1.com', 'GITHUB')").update();
        jdbcClient.sql("insert into chat (id) values (1);").update();

        // when
        chatLinkDao.add(1L, 1L);
        Boolean result = jdbcClient.sql("select exists(select 1 from chat_link where link_id = 1 and chat_id = 1)")
            .query(Boolean.class).single();

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @MethodSource("chatLinkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Повторное добавление записи")
    void addExistingRecord(ChatLinkDao chatLinkDao) {
        // given
        jdbcClient.sql("insert into link (id, url, type) values (1, 'https://aboba1.com', 'GITHUB')").update();
        jdbcClient.sql("insert into chat (id) values (1);").update();
        chatLinkDao.add(1L, 1L);

        // when
        assertThatThrownBy(() -> chatLinkDao.add(1L, 1L)).isInstanceOf(DuplicateKeyException.class);
    }

    @ParameterizedTest
    @MethodSource("chatLinkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Удаление записи")
    void remove(ChatLinkDao chatLinkDao) {
        // given
        jdbcClient.sql("insert into link (id, url, type) values (1, 'https://aboba1.com', 'GITHUB')").update();
        jdbcClient.sql("insert into chat (id) values (1);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (1, 1);").update();

        // when
        chatLinkDao.remove(1L, 1L);

        // then
        Boolean result = jdbcClient.sql("select exists(select 1 from chat_link where link_id = 1 and chat_id = 1)")
            .query(Boolean.class).single();
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление несуществующей записи jdbc")
    void removeNonexistentJdbc() {
        // when-then
        assertThatThrownBy(() -> jdbcChatLinkDao.remove(1L, 1L)).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление несуществующей записи jooq")
    void removeNonexistentJooq() {
        // when-then
        assertThatThrownBy(() -> jooqChatLinkDao.remove(1L, 1L)).isInstanceOf(NoDataFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("chatLinkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Удаление неотслеживаемых никем ссылок")
    void removeDanglingLinks(ChatLinkDao chatLinkDao) {
        // given
        jdbcClient.sql("insert into link (id, url, type) values (1, 'https://aboba1.com', 'GITHUB')").update();

        // when
        chatLinkDao.removeDanglingLinks();

        // then
        Boolean result = jdbcClient.sql("select exists(select 1 from link)")
            .query(Boolean.class).single();
        assertThat(result).isFalse();
    }
}
