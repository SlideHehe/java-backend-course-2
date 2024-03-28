package edu.java.scrapper.api.links.jdbc;

import edu.java.scrapper.api.links.Link;
import edu.java.scrapper.api.links.LinkDao;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class JdbcLinkDao implements LinkDao {
    private final JdbcClient jdbcClient;

    @Override
    public List<Link> findAll() {
        return jdbcClient.sql("select link.id, link.url, link.updated_at, link.checked_at from link")
            .query(Link.class)
            .list();
    }

    @Override
    public List<Link> findCheckedMoreThanSomeSecondsAgo(Long secondsAgo) {
        return jdbcClient.sql("select link.id, link.url, link.updated_at, link.checked_at from link "
                + "where extract(epoch from (current_timestamp - link.checked_at)) > ?")
            .param(secondsAgo)
            .query(Link.class)
            .list();
    }

    @Override
    public Optional<Link> findById(Long id) {
        return jdbcClient.sql("select link.id, link.url, link.updated_at, link.checked_at from link where id = ?")
            .param(id)
            .query(Link.class)
            .optional();
    }

    @Override
    public Optional<Link> findByUrl(URI url) {
        return jdbcClient.sql("select link.id, link.url, link.updated_at, link.checked_at from link where url = ?")
            .param(url.toString())
            .query(Link.class)
            .optional();
    }

    @Override
    public Link add(URI url) {
        return jdbcClient.sql(
                "insert into link (url) values (?) returning link.id, link.url, link.updated_at, link.checked_at")
            .param(url.toString())
            .query(Link.class)
            .single();
    }

    @Override
    public Link updateUpdatedTimestamp(Long id, OffsetDateTime updatedAt) {
        return jdbcClient.sql(
                "update link set updated_at = ?, checked_at = default "
                    + "where link.id = ? returning link.id, link.url, link.updated_at, link.checked_at"
            ).param(updatedAt)
            .param(id)
            .query(Link.class)
            .single();
    }

    @Override
    public List<Link> updateCheckedTimestamp(List<Long> ids) {
        return jdbcClient.sql("update link set checked_at = default where link.id in (:ids) "
                + "returning link.id, link.url, link.updated_at, link.checked_at")
            .param("ids", ids)
            .query(Link.class)
            .list();
    }

    @Override
    public Link remove(Long id) {
        return jdbcClient.sql(
                "delete from link where id = ? returning link.id, link.url, link.updated_at, link.checked_at")
            .param(id)
            .query(Link.class)
            .single();
    }

    @Override
    public List<Link> findAllByChatId(Long chatId) {
        return jdbcClient.sql("select link.id, link.url, link.updated_at, link.checked_at "
                + "from link join chat_link on chat_link.link_id = link.id where chat_link.chat_id = ?")
            .param(chatId)
            .query(Link.class)
            .list();
    }
}
