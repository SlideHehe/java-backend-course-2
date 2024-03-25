package edu.java.scrapper.domain.links.schemabased.jdbc;

import edu.java.scrapper.domain.links.Type;
import edu.java.scrapper.domain.links.schemabased.Link;
import edu.java.scrapper.domain.links.schemabased.LinkDao;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@SuppressWarnings("MultipleStringLiterals")
@RequiredArgsConstructor
@Repository
public class JdbcLinkDao implements LinkDao {
    private static final String LINK_FIELDS = "link.id, link.url, link.updated_at, link.checked_at, link.type, "
                                              + "link.answer_count, link.comment_count, "
                                              + "link.pull_request_count, link.commit_count";
    private static final String SELECT_LINK_FIELDS = "select " + LINK_FIELDS;
    private static final String RETURNING_LINK_FIELDS = "returning " + LINK_FIELDS;
    private final JdbcClient jdbcClient;

    @Override
    public List<Link> findAll() {
        return findAllBy("", Map.of());
    }

    @Override
    public List<Link> findCheckedMoreThanSomeSecondsAgo(Long secondsAgo) {
        return findAllBy(
            "where extract(epoch from (current_timestamp - link.checked_at)) > :secondsAgo",
            Map.of("secondsAgo", secondsAgo)
        );
    }

    @Override
    public Optional<Link> findById(Long id) {
        return findBy(
            "where id = :id",
            Map.of("id", id)
        );
    }

    @Override
    public Optional<Link> findByUrl(URI url) {
        return findBy(
            "where url = :url",
            Map.of("url", url.toString())
        );
    }

    @Override
    public Link add(URI url, Type type) {
        return jdbcClient.sql("insert into link (url, type) values (:url, :type) " + RETURNING_LINK_FIELDS)
            .param("url", url.toString())
            .param("type", type.toString())
            .query(Link.class)
            .single();
    }

    @Override
    public Link updateUpdatedTimestamp(Long id, OffsetDateTime updatedAt) {
        return jdbcClient.sql(
                "update link set updated_at = :updatedAt where link.id = :id " + RETURNING_LINK_FIELDS)
            .param("updatedAt", updatedAt)
            .param("id", id)
            .query(Link.class)
            .single();
    }

    @Override
    public List<Link> updateCheckedTimestamp(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        return jdbcClient.sql("update link set checked_at = default where link.id in (:ids) " + RETURNING_LINK_FIELDS)
            .param("ids", ids)
            .query(Link.class)
            .list();
    }

    @Override
    public Link updateCounters(
        Long id,
        Integer answerCount,
        Integer commentCount,
        Integer pullRequestCount,
        Integer commitCount
    ) {
        return jdbcClient.sql("update link set "
                              + "answer_count = :answerCount, "
                              + "comment_count = :commentCount, "
                              + "pull_request_count = :pullRequestCount, "
                              + "commit_count = :commitCount "
                              + "where link.id = :id " + RETURNING_LINK_FIELDS)
            .param("answerCount", answerCount)
            .param("commentCount", commentCount)
            .param("pullRequestCount", pullRequestCount)
            .param("commitCount", commitCount)
            .param("id", id)
            .query(Link.class)
            .single();
    }

    @Override
    public Link remove(Long id) {
        return jdbcClient.sql(
                "delete from link where id = :id " + RETURNING_LINK_FIELDS)
            .param("id", id)
            .query(Link.class)
            .single();
    }

    @Override
    public List<Link> findAllByChatId(Long chatId) {
        return findAllBy(
            "join chat_link on chat_link.link_id = link.id where chat_link.chat_id = :chatId",
            Map.of("chatId", chatId)
        );
    }

    private List<Link> findAllBy(String criteria, Map<String, ?> params) {
        return jdbcClient.sql(SELECT_LINK_FIELDS + " from link " + criteria)
            .params(params)
            .query(Link.class)
            .list();
    }

    private Optional<Link> findBy(String criteria, Map<String, ?> params) {
        return jdbcClient.sql(SELECT_LINK_FIELDS + " from link " + criteria)
            .params(params)
            .query(Link.class)
            .optional();
    }
}
