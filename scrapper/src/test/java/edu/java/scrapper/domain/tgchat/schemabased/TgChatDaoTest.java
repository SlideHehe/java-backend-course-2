package edu.java.scrapper.domain.tgchat.schemabased;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.tgchat.schemabased.jdbc.JdbcTgChatDao;
import edu.java.scrapper.domain.tgchat.schemabased.jooq.JooqTgChatDao;
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
class TgChatDaoTest extends IntegrationTest {
    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JdbcTgChatDao jdbcTgChatDao;
    @Autowired
    JooqTgChatDao jooqTgChatDao;

    @BeforeEach
    public void resetSequence() {
        jdbcClient.sql("truncate link cascade").update();
    }

    Stream<Arguments> tgChatDaoProvider() {
        return Stream.of(
            Arguments.of(jdbcTgChatDao),
            Arguments.of(jooqTgChatDao)
        );
    }

    @ParameterizedTest
    @MethodSource("tgChatDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Поиск всех записей")
    void findAll(TgChatDao tgChatDao) {
        // given
        jdbcClient.sql("insert into chat (id, created_at) values "
                       + "(1, timestamp with time zone '2024-03-13T18:27:34.389Z'),"
                       + "(2, timestamp with time zone '2024-03-13T18:27:34.389Z'),"
                       + "(3, timestamp with time zone '2024-03-13T18:27:34.389Z')"
        ).update();

        // when
        List<TgChat> actualList = tgChatDao.findAll();

        // then
        assertThat(actualList.size()).isEqualTo(3);
    }

    @ParameterizedTest
    @MethodSource("tgChatDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Выборка списка из пустой таблицы")
    void findAllEmptyTable(TgChatDao tgChatDao) {
        // when-then
        assertThat(tgChatDao.findAll()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("tgChatDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по первичному ключу")
    void findById(TgChatDao tgChatDao) {
        // given
        jdbcClient.sql(
            "insert into chat (id, created_at) values (1, timestamp with time zone '2024-03-13T18:27:34.389Z')"
        ).update();

        // when
        Optional<TgChat> actualChat = tgChatDao.findById(1L);

        // then
        assertThat(actualChat.get().id()).isEqualTo(1L);
    }

    @ParameterizedTest
    @MethodSource("tgChatDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по несуществующему ключу")
    void findByNonexistentId(TgChatDao tgChatDao) {
        // when-then
        assertThat(tgChatDao.findById(1L)).isNotPresent();
    }

    @ParameterizedTest
    @MethodSource("tgChatDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Добавление элемента")
    void add(TgChatDao tgChatDao) {
        // when
        tgChatDao.add(1L);
        Boolean result =
            jdbcClient.sql("select exists(select 1 from chat where chat.id = 1)").query(Boolean.class).single();

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @MethodSource("tgChatDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Повторное добавление записи")
    void addExistingRecord(TgChatDao tgChatDao) {
        // given
        tgChatDao.add(1L);

        // when-then
        assertThatThrownBy(() -> tgChatDao.add(1L)).isInstanceOf(DuplicateKeyException.class);
    }

    @ParameterizedTest
    @MethodSource("tgChatDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Удаление записи")
    void remove(TgChatDao tgChatDao) {
        // given
        jdbcClient.sql("insert into chat (id) values (1)").update();

        // when
        tgChatDao.remove(1L);
        Boolean result =
            jdbcClient.sql("select exists(select 1 from chat where chat.id = 1)").query(Boolean.class).single();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление несуществующей записи jdbc")
    void removeNonexistentJdbc() {
        // when-then
        assertThatThrownBy(() -> jdbcTgChatDao.remove(1L)).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление несуществующей записи jooq")
    void removeNonexistentJooq() {
        // when-then
        assertThatThrownBy(() -> jooqTgChatDao.remove(1L)).isInstanceOf(NoDataFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("tgChatDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Получение ссылок, отслеживаемых пользователем")
    void findAllByLinkId(TgChatDao tgChatDao) {
        // given
        jdbcClient.sql("insert into link (id, url, type) values "
                       + "(1, 'https://aboba1.com', 'GITHUB'), (2, 'https://aboba2.com', 'GITHUB')"
        ).update();
        jdbcClient.sql("insert into chat (id, created_at) values "
                       + "(1, timestamp with time zone '2024-03-13T18:27:34.389Z'), "
                       + "(2, timestamp with time zone '2024-03-13T18:27:34.389Z');").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (1, 1);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (1, 2);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (2, 2);").update();

        // when
        List<TgChat> actualList = tgChatDao.findAllByLinkId(1L);

        // then
        assertThat(actualList.getFirst().id()).isEqualTo(1L);
    }
}
