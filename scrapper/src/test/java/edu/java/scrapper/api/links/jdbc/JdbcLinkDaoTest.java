package edu.java.scrapper.api.links.jdbc;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.api.links.Link;
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
        jdbcClient.sql("insert into link (id, url, updated_at, checked_at) values "
            +
            "(1, 'https://aboba1.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z'),"
            +
            "(2, 'https://aboba2.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z'),"
            +
            "(3, 'https://aboba3.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z')"
        ).update();
        List<Link> expectedList = List.of(
            new Link(1L, URI.create("https://aboba1.com"), time, time),
            new Link(2L, URI.create("https://aboba2.com"), time, time),
            new Link(3L, URI.create("https://aboba3.com"), time, time)
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
        jdbcClient.sql("insert into link (id, url, updated_at, checked_at) values "
            +
            "(1, 'https://aboba1.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z')"
        ).update();
        Optional<Link> expectedLink = Optional.of(new Link(1L, URI.create("https://aboba1.com"), time, time));

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
        jdbcClient.sql("insert into link (id, url, updated_at, checked_at) values "
            +
            "(1, 'https://aboba1.com', timestamp with time zone '2024-03-13T18:27:34.389Z', timestamp with time zone '2024-03-13T18:27:34.389Z')"
        ).update();
        Optional<Link> expectedLink = Optional.of(new Link(1L, uri, time, time));

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
        jdbcLinkDao.add(URI.create("https://aboba1.com"));
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
        jdbcLinkDao.add(uri);

        // when-then
        assertThatThrownBy(() -> jdbcLinkDao.add(uri)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Удаление записи")
    void remove() {
        // given
        jdbcClient.sql("insert into link (id, url) values (1, 'https://aboba1.com')").update();

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
}
