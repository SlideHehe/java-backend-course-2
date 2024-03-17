package edu.java.scrapper.api.links.jdbc;

import edu.java.scrapper.api.links.Link;
import edu.java.scrapper.api.links.LinkDao;
import edu.java.scrapper.api.links.Type;
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
        return jdbcClient.sql("select link.id, link.url, link.updated_at, link.checked_at, link.type, "
                              + "link.answer_count, link.comment_count, link.pull_request_count, link.commit_count "
                              + "from link")
            .query(Link.class)
            .list();
    }

    @Override
    public List<Link> findCheckedMoreThanSomeSecondsAgo(Long secondsAgo) {
        return jdbcClient.sql("select link.id, link.url, link.updated_at, link.checked_at, link.type, "
                              + "link.answer_count, link.comment_count, link.pull_request_count, link.commit_count "
                              + "from link where extract(epoch from (current_timestamp - link.checked_at)) > ?")
            .param(secondsAgo)
            .query(Link.class)
            .list();
    }

    @Override
    public Optional<Link> findById(Long id) {
        return jdbcClient.sql("select link.id, link.url, link.updated_at, link.checked_at, link.type, "
                              + "link.answer_count, link.comment_count, link.pull_request_count, link.commit_count "
                              + "from link where id = ?")
            .param(id)
            .query(Link.class)
            .optional();
    }

    @Override
    public Optional<Link> findByUrl(URI url) {
        return jdbcClient.sql("select link.id, link.url, link.updated_at, link.checked_at, link.type, "
                              + "link.answer_count, link.comment_count, link.pull_request_count, link.commit_count"
                              + " from link where url = ?")
            .param(url.toString())
            .query(Link.class)
            .optional();
    }

    @Override
    public Link add(URI url, Type type) {
        return jdbcClient.sql("insert into link (url, type) values (?, ?) "
                              + "returning link.id, link.url, link.updated_at, link.checked_at, link.type, "
                              + "link.answer_count, link.comment_count, link.pull_request_count, link.commit_count")
            .params(url.toString(), type.toString())
            .query(Link.class)
            .single();
    }

    @Override
    public Link updateUpdatedTimestamp(Long id, OffsetDateTime updatedAt) {
        return jdbcClient.sql(
                "update link set updated_at = ? "
                + "where link.id = ? returning link.id, link.url, link.updated_at, link.checked_at, link.type, "
                + "link.answer_count, link.comment_count, link.pull_request_count, link.commit_count")
            .params(updatedAt, id)
            .query(Link.class)
            .single();
    }

    @Override
    public List<Link> updateCheckedTimestamp(List<Long> ids) {
        return jdbcClient.sql("update link set checked_at = default where link.id in (:ids) "
                              + "returning link.id, link.url, link.updated_at, link.checked_at, link.type, "
                              + "link.answer_count, link.comment_count, link.pull_request_count, link.commit_count")
            .param("ids", ids)
            .query(Link.class)
            .list();
    }

    @Override
    public Link updateStackoverflowLink(Long id, Integer answerCount, Integer commentCount) {
        return jdbcClient.sql("update link set answer_count = ?, comment_count = ? "
                              + "where link.id = ? returning link.id, link.url, link.updated_at, link.checked_at, "
                              + "link.type, link.answer_count, link.comment_count, link.pull_request_count, "
                              + "link.commit_count")
            .params(answerCount, commentCount, id)
            .query(Link.class)
            .single();
    }

    @Override
    public Link updateGithubLink(Long id, Integer pullRequestCount, Integer commitCount) {
        return jdbcClient.sql("update link set pull_request_count = ?, commit_count = ? "
                              + "where link.id = ? returning link.id, link.url, link.updated_at, link.checked_at, "
                              + "link.type, link.answer_count, link.comment_count, link.pull_request_count, "
                              + "link.commit_count")
            .params(pullRequestCount, commitCount, id)
            .query(Link.class)
            .single();
    }

    @Override
    public Link remove(Long id) {
        return jdbcClient.sql(
                "delete from link where id = ? returning link.id, link.url, link.updated_at, link.checked_at, "
                + "link.type, link.answer_count, link.comment_count, link.pull_request_count, link.commit_count")
            .param(id)
            .query(Link.class)
            .single();
    }

    @Override
    public List<Link> findAllByChatId(Long chatId) {
        return jdbcClient.sql("select link.id, link.url, link.updated_at, link.checked_at, link.type, "
                              + "link.answer_count, link.comment_count, link.pull_request_count, link.commit_count "
                              + "from link join chat_link on chat_link.link_id = link.id where chat_link.chat_id = ?")
            .param(chatId)
            .query(Link.class)
            .list();
    }
}
