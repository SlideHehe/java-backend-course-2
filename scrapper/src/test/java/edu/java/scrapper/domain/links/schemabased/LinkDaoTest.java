package edu.java.scrapper.domain.links.schemabased;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.links.Type;
import edu.java.scrapper.domain.links.schemabased.Link;
import edu.java.scrapper.domain.links.schemabased.LinkDao;
import edu.java.scrapper.domain.links.schemabased.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.links.schemabased.jooq.JooqLinkDao;
import java.net.URI;
import java.time.OffsetDateTime;
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
class LinkDaoTest extends IntegrationTest {
    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JdbcLinkDao jdbcLinkDao;
    @Autowired
    JooqLinkDao jooqLinkDao;

    Stream<Arguments> linkDaoProvider() {
        return Stream.of(
            Arguments.of(jdbcLinkDao),
            Arguments.of(jooqLinkDao)
        );
    }

    @BeforeEach
    public void resetSequence() {
        jdbcClient.sql("truncate link cascade").update();
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Поиск всех записей")
    void findAll(LinkDao linkDao) {
        // given
        jdbcClient.sql("insert into link (id, url, updated_at, checked_at, type) values "
                       +
                       "(1, 'https://aboba1.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z', 'GITHUB'),"
                       +
                       "(2, 'https://aboba2.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z', 'GITHUB'),"
                       +
                       "(3, 'https://aboba3.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z', 'GITHUB')"
        ).update();

        // when
        List<Link> actualList = linkDao.findAll();

        // then
        assertThat(actualList.size()).isEqualTo(3);
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Выборка списка из пустой таблицы")
    void findAllEmptyList(LinkDao linkDao) {
        // when-then
        assertThat(linkDao.findAll()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по первичному ключу")
    void findById(LinkDao linkDao) {
        // given
        jdbcClient.sql("insert into link (id, url, updated_at, checked_at, type) values "
                       +
                       "(1, 'https://aboba1.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z', 'GITHUB')"
        ).update();

        // when
        Optional<Link> actualLink = linkDao.findById(1L);

        // then
        assertThat(actualLink).isPresent();
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по несуществующему ключу")
    void findByNonExistentId(LinkDao linkDao) {
        // when-then
        assertThat(linkDao.findById(1L)).isNotPresent();
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Rollback
    @DisplayName("Поиск записи по ссылке")
    void findByUrl(LinkDao linkDao) {
        // given
        URI uri = URI.create("https://aboba1.com");
        jdbcClient.sql("insert into link (id, url, updated_at, checked_at, type) values "
                       +
                       "(1, 'https://aboba1.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z', 'GITHUB')"
        ).update();

        // when
        Optional<Link> actualLink = linkDao.findByUrl(uri);

        // then
        assertThat(actualLink).isPresent();
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по несуществующей ссылке")
    void findByNonExistentUrl(LinkDao linkDao) {
        // when-then
        assertThat(linkDao.findByUrl(URI.create("https://aboba1.com"))).isEqualTo(Optional.empty());
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Добавление элемента")
    void add(LinkDao linkDao) {
        // when
        linkDao.add(URI.create("https://aboba1.com"), Type.GITHUB);
        Boolean result = jdbcClient.sql("select exists(select 1 from link where link.url = 'https://aboba1.com')")
            .query(Boolean.class).single();

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Добавление элемента с уже существующей ссылкой")
    void addExistingUrl(LinkDao linkDao) {
        // given
        URI uri = URI.create("https://aboba1.com");
        linkDao.add(uri, Type.GITHUB);

        // when-then
        assertThatThrownBy(() -> linkDao.add(uri, Type.GITHUB)).isInstanceOf(DuplicateKeyException.class);
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Удаление записи")
    void remove(LinkDao linkDao) {
        // given
        jdbcClient.sql("insert into link (id, url, type) values (1, 'https://aboba1.com', 'GITHUB')").update();

        // when
        linkDao.remove(1L);
        Boolean result = jdbcClient.sql("select exists(select 1 from link where link.id = 1)")
            .query(Boolean.class).single();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление несуществующей записи jdbc")
    void removeNonExistentJdbc() {
        // when-then
        assertThatThrownBy(() -> jdbcLinkDao.remove(1L)).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление несуществующей записи jdbc")
    void removeNonExistentJooq() {
        // when-then
        assertThatThrownBy(() -> jooqLinkDao.remove(1L)).isInstanceOf(NoDataFoundException.class);
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Получение ссылок, отслеживаемых пользователем")
    void findAllByChatId(LinkDao linkDao) {
        // given
        jdbcClient.sql("insert into link (id, url, updated_at, checked_at, type) values "
                       +
                       "(1, 'https://aboba1.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z', 'GITHUB'),"
                       +
                       "(2, 'https://aboba2.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z', 'GITHUB')"
        ).update();
        jdbcClient.sql("insert into chat (id) values (1), (2);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (1, 1);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (2, 1);").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (2, 2);").update();

        // when
        List<Link> actualList = linkDao.findAllByChatId(1L);

        // then
        assertThat(actualList.size()).isEqualTo(1);
        assertThat(actualList.getFirst().url()).isEqualTo(URI.create("https://aboba1.com"));
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Проверка обновления времени")
    void updateUpdatedTimestamp(LinkDao linkDao) {
        // given
        jdbcClient.sql(
                "insert into link (id, url, checked_at, type) values (1, 'https://aboba.com', '2024-03-13T18:27:34.389Z', 'GITHUB')")
            .update();

        // when
        Link link = linkDao.updateUpdatedTimestamp(
            1L,
            OffsetDateTime.parse("2024-03-15T18:27:34.389Z")
        );

        // then
        assertThat(link.updatedAt()).isEqualTo(OffsetDateTime.parse("2024-03-15T18:27:34.389Z"));
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Проверка получения ссылок, которые проверялись последний раз N секунд назад")
    void findCheckedMoreThanSomeSecondsAgo(LinkDao linkDao) {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        jdbcClient.sql("insert into link (id, url, updated_at, checked_at, type) values "
                       + "(1, 'https://aboba1.com', ?, ?, 'GITHUB'),"
                       + "(2, 'https://aboba2.com', ?, ?, 'GITHUB')"
        ).params(time, time.minusMinutes(10), time, time).update();

        // when
        List<Link> actualList = linkDao.findCheckedMoreThanSomeSecondsAgo(500L);

        // then
        assertThat(actualList.getFirst().url()).isEqualTo(URI.create("https://aboba1.com"));
        assertThat(actualList.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Проверка обновления поля checked_at")
    void updateCheckedTimestamp(LinkDao linkDao) {
        // given
        OffsetDateTime time = OffsetDateTime.now().minusDays(1);
        jdbcClient.sql("insert into link (id, url, checked_at, type) values "
                       + "(1, 'https://aboba1.com', ?, 'GITHUB'),"
                       + "(2, 'https://aboba2.com', ?, 'GITHUB')"
        ).params(time, time).update();

        // when
        List<Link> actualList = linkDao.updateCheckedTimestamp(List.of(1L, 2L));

        // then
        assertThat(actualList.getFirst().url()).isEqualTo(URI.create("https://aboba1.com"));
        assertThat(actualList.getFirst().updatedAt()).isNotEqualTo(time);
        assertThat(actualList.get(1).url()).isEqualTo(URI.create("https://aboba2.com"));
        assertThat(actualList.get(1).updatedAt()).isNotEqualTo(time);
    }

    @ParameterizedTest
    @MethodSource("linkDaoProvider")
    @Transactional
    @Rollback
    @DisplayName("Проверка обновления счетчиков")
    void updateCounters(LinkDao linkDao) {
        // given
        jdbcClient.sql("insert into link (id, url, type) "
                       + "values (1, 'https://aboba1.com', 'GITHUB');").update();

        // when
        Link link = linkDao.updateCounters(1L, 1, 1, 1, 1);

        // then
        assertThat(link.answerCount()).isEqualTo(1);
        assertThat(link.commentCount()).isEqualTo(1);
        assertThat(link.pullRequestCount()).isEqualTo(1);
        assertThat(link.commitCount()).isEqualTo(1);
    }
}
