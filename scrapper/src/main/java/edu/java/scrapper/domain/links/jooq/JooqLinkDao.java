package edu.java.scrapper.domain.links.jooq;

import edu.java.scrapper.domain.links.Link;
import edu.java.scrapper.domain.links.LinkDao;
import edu.java.scrapper.domain.links.Type;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.domain.jooq.Tables.CHAT_LINK;
import static edu.java.scrapper.domain.jooq.Tables.LINK;

@RequiredArgsConstructor
@Repository
public class JooqLinkDao implements LinkDao {
    private final DSLContext context;

    @Override
    public List<Link> findAll() {
        return context.selectFrom(LINK)
            .fetchInto(Link.class);
    }

    @Override
    public List<Link> findCheckedMoreThanSomeSecondsAgo(Long secondsAgo) {
        return context.selectFrom(LINK)
            .where("extract(epoch from (current_timestamp - link.checked_at)) > {0}", secondsAgo)
            .fetchInto(Link.class);
    }

    @Override
    public Optional<Link> findById(Long id) {
        return context.selectFrom(LINK)
            .where(LINK.ID.eq(id))
            .fetchOptionalInto(Link.class);
    }

    @Override
    public Optional<Link> findByUrl(URI uri) {
        return context.selectFrom(LINK)
            .where(LINK.URL.eq(uri.toString()))
            .fetchOptionalInto(Link.class);
    }

    @Override
    public Link add(URI url, Type type) {
        return context.insertInto(LINK)
            .set(LINK.URL, url.toString())
            .set(LINK.TYPE, type.toString())
            .returningResult(LINK)
            .fetchSingleInto(Link.class);
    }

    @Override
    public Link updateUpdatedTimestamp(Long id, OffsetDateTime updatedAt) {
        return context.update(LINK)
            .set(LINK.UPDATED_AT, updatedAt)
            .where(LINK.ID.eq(id))
            .returningResult(LINK)
            .fetchSingleInto(Link.class);
    }

    @Override
    public List<Link> updateCheckedTimestamp(List<Long> ids) {
        return context.update(LINK)
            .set(LINK.CHECKED_AT, OffsetDateTime.now())
            .where(LINK.ID.in(ids))
            .returningResult(LINK)
            .fetchInto(Link.class);
    }

    @Override
    public Link updateCounters(
        Long id,
        Integer answerCount,
        Integer commentCount,
        Integer pullRequestCount,
        Integer commitCount
    ) {
        return context.update(LINK)
            .set(LINK.ANSWER_COUNT, answerCount)
            .set(LINK.COMMENT_COUNT, commentCount)
            .set(LINK.PULL_REQUEST_COUNT, pullRequestCount)
            .set(LINK.COMMIT_COUNT, commentCount)
            .where(LINK.ID.eq(id))
            .returningResult(LINK)
            .fetchSingleInto(Link.class);
    }

    @Override
    public Link remove(Long id) {
        return context.deleteFrom(LINK)
            .where(LINK.ID.eq(id))
            .returningResult(LINK)
            .fetchSingleInto(Link.class);
    }

    @Override
    public List<Link> findAllByChatId(Long chatId) {
        return context.select(LINK)
            .from(LINK.join(CHAT_LINK).on(CHAT_LINK.LINK_ID.eq(LINK.ID)))
            .where(CHAT_LINK.CHAT_ID.eq(chatId))
            .fetchInto(Link.class);
    }
}
