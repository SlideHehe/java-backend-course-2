package edu.java.scrapper.domain.links.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.links.Link;
import edu.java.scrapper.domain.links.Type;
import java.net.URI;
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
class JdbcLinkDaoTest extends IntegrationTest {
    @Autowired
    JdbcClient jdbcClient;
    @Autowired
    JdbcLinkDao jdbcLinkDao;

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск всех записей")
    void findAll() {
        // given
        OffsetDateTime time = OffsetDateTime.parse("2024-03-13T18:27:34.389Z");
        jdbcClient.sql("insert into link (id, url, updated_at, checked_at, type) values "
                       +
                       "(1, 'https://aboba1.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z', 'GITHUB'),"
                       +
                       "(2, 'https://aboba2.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z', 'GITHUB'),"
                       +
                       "(3, 'https://aboba3.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z', 'GITHUB')"
        ).update();
        List<Link> expectedList = List.of(
            new Link(1L, URI.create("https://aboba1.com"), time, time, Type.GITHUB, null, null, null, null),
            new Link(2L, URI.create("https://aboba2.com"), time, time, Type.GITHUB, null, null, null, null),
            new Link(3L, URI.create("https://aboba3.com"), time, time, Type.GITHUB, null, null, null, null)
        );

        // when
        List<Link> actualList = jdbcLinkDao.findAll();

        // then
        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Выборка списка из пустой таблицы")
    void findAllEmptyList() {
        // when-then
        assertThat(jdbcLinkDao.findAll()).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по первичному ключу")
    void findById() {
        // given
        OffsetDateTime time = OffsetDateTime.parse("2024-03-13T18:27:34.389Z");
        jdbcClient.sql("insert into link (id, url, updated_at, checked_at, type) values "
                       +
                       "(1, 'https://aboba1.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z', 'GITHUB')"
        ).update();
        Optional<Link> expectedLink = Optional.of(new Link(
            1L,
            URI.create("https://aboba1.com"),
            time,
            time,
            Type.GITHUB,
            null,
            null,
            null,
            null
        ));

        // when
        Optional<Link> actualLink = jdbcLinkDao.findById(1L);

        // then
        assertThat(actualLink).isEqualTo(expectedLink);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по несуществующему ключу")
    void findByNonExistentId() {
        // when-then
        assertThat(jdbcLinkDao.findById(1L)).isNotPresent();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по ссылке")
    void findByUrl() {
        // given
        OffsetDateTime time = OffsetDateTime.parse("2024-03-13T18:27:34.389Z");
        URI uri = URI.create("https://aboba1.com");
        jdbcClient.sql("insert into link (id, url, updated_at, checked_at, type) values "
                       +
                       "(1, 'https://aboba1.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z', 'GITHUB')"
        ).update();
        Optional<Link> expectedLink = Optional.of(new Link(1L, uri, time, time, Type.GITHUB, null, null, null, null));

        // when
        Optional<Link> actualLink = jdbcLinkDao.findByUrl(uri);

        // then
        assertThat(actualLink).isEqualTo(expectedLink);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск записи по несуществующей ссылке")
    void findByNonExistentUrl() {
        // when-then
        assertThat(jdbcLinkDao.findByUrl(URI.create("https://aboba1.com"))).isEqualTo(Optional.empty());
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Добавление элемента")
    void add() {
        // when
        jdbcLinkDao.add(URI.create("https://aboba1.com"), Type.GITHUB);
        Boolean result = jdbcClient.sql("select exists(select 1 from link where link.url = 'https://aboba1.com')")
            .query(Boolean.class).single();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Добавление элемента с уже существующей ссылкой")
    void addExistingUrl() {
        // given
        URI uri = URI.create("https://aboba1.com");
        jdbcLinkDao.add(uri, Type.GITHUB);

        // when-then
        assertThatThrownBy(() -> jdbcLinkDao.add(uri, Type.GITHUB)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление записи")
    void remove() {
        // given
        jdbcClient.sql("insert into link (id, url, type) values (1, 'https://aboba1.com', 'GITHUB')").update();

        // when
        jdbcLinkDao.remove(1L);
        Boolean result = jdbcClient.sql("select exists(select 1 from link where link.id = 1)")
            .query(Boolean.class).single();

        // then
        assertThat(result).isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление несуществующей записи")
    void removeNonExistent() {
        // when-then
        assertThatThrownBy(() -> jdbcLinkDao.remove(1L)).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Получение ссылок, отслеживаемых пользователем")
    void findAllByChatId() {
        // given
        OffsetDateTime time = OffsetDateTime.parse("2024-03-13T18:27:34.389Z");
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
        List<Link> expectedList = List.of(
            new Link(1L, URI.create("https://aboba1.com"), time, time, Type.GITHUB, null, null, null, null)
        );

        // when
        List<Link> actualList = jdbcLinkDao.findAllByChatId(1L);

        // then
        assertThat(actualList).isEqualTo(expectedList);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Проверка обновления времени")
    void updateUpdatedTimestamp() {
        // given
        jdbcClient.sql(
                "insert into link (id, url, checked_at, type) values (1, 'https://aboba.com', '2024-03-13T18:27:34.389Z', 'GITHUB')")
            .update();

        // when
        Link link = jdbcLinkDao.updateUpdatedTimestamp(
            1L,
            OffsetDateTime.parse("2024-03-15T18:27:34.389Z")
        );

        // then
        assertThat(link.updatedAt()).isEqualTo(OffsetDateTime.parse("2024-03-15T18:27:34.389Z"));
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Проверка получения ссылок, которые проверялись последний раз N секунд назад")
    void findCheckedMoreThanSomeSecondsAgo() {
        // given
        OffsetDateTime time = OffsetDateTime.now();
        jdbcClient.sql("insert into link (id, url, updated_at, checked_at, type) values "
                       + "(1, 'https://aboba1.com', ?, ?, 'GITHUB'),"
                       + "(2, 'https://aboba2.com', ?, ?, 'GITHUB')"
        ).params(time, time.minusMinutes(10), time, time).update();

        // when
        List<Link> actualList = jdbcLinkDao.findCheckedMoreThanSomeSecondsAgo(500L);

        // then
        assertThat(actualList.getFirst().url()).isEqualTo(URI.create("https://aboba1.com"));
        assertThat(actualList.size()).isEqualTo(1);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Проверка обновления поля checked_at")
    void updateCheckedTimestamp() {
        // given
        OffsetDateTime time = OffsetDateTime.now().minusDays(1);
        jdbcClient.sql("insert into link (id, url, checked_at, type) values "
                       + "(1, 'https://aboba1.com', ?, 'GITHUB'),"
                       + "(2, 'https://aboba2.com', ?, 'GITHUB')"
        ).params(time, time).update();

        // when
        List<Link> actualList = jdbcLinkDao.updateCheckedTimestamp(List.of(1L, 2L));

        // then
        assertThat(actualList.getFirst().url()).isEqualTo(URI.create("https://aboba1.com"));
        assertThat(actualList.getFirst().updatedAt()).isNotEqualTo(time);
        assertThat(actualList.get(1).url()).isEqualTo(URI.create("https://aboba2.com"));
        assertThat(actualList.get(1).updatedAt()).isNotEqualTo(time);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Проверка обновления счетчиков")
    void updateCounters() {
        // given
        jdbcClient.sql("insert into link (id, url, type) "
                       + "values (1, 'https://aboba1.com', 'GITHUB');").update();

        // when
        Link link = jdbcLinkDao.updateCounters(1L, 1, 1, 1, 1);

        // then
        assertThat(link.answerCount()).isEqualTo(1);
        assertThat(link.commentCount()).isEqualTo(1);
        assertThat(link.pullRequestCount()).isEqualTo(1);
        assertThat(link.commitCount()).isEqualTo(1);
    }
}
