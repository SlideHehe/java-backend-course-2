package edu.java.scrapper.domain.chatlink.schemabased.jooq;

import edu.java.scrapper.domain.chatlink.schemabased.ChatLink;
import edu.java.scrapper.domain.chatlink.schemabased.ChatLinkDao;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.domain.jooqcodegen.Tables.CHAT_LINK;
import static edu.java.scrapper.domain.jooqcodegen.Tables.LINK;

@RequiredArgsConstructor
@Repository
public class JooqChatLinkDao implements ChatLinkDao {
    private final DSLContext context;

    @Override
    public List<ChatLink> findAll() {
        return context.selectFrom(CHAT_LINK)
            .fetchInto(ChatLink.class);
    }

    @Override
    public Optional<ChatLink> findById(Long chatId, Long linkId) {
        return context.selectFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(chatId))
            .and(CHAT_LINK.LINK_ID.eq(linkId))
            .fetchOptionalInto(ChatLink.class);
    }

    @Override
    public ChatLink add(Long chatId, Long linkId) {
        return context.insertInto(CHAT_LINK)
            .set(CHAT_LINK.CHAT_ID, chatId)
            .set(CHAT_LINK.LINK_ID, linkId)
            .returningResult(CHAT_LINK)
            .fetchSingleInto(ChatLink.class);
    }

    @Override
    public ChatLink remove(Long chatId, Long linkId) {
        return context.deleteFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(chatId))
            .and(CHAT_LINK.LINK_ID.eq(linkId))
            .returningResult(CHAT_LINK)
            .fetchSingleInto(ChatLink.class);
    }

    @Override
    public void removeDanglingLinks() {
        context.deleteFrom(LINK)
            .whereNotExists(
                context.select()
                    .from(CHAT_LINK)
                    .where(CHAT_LINK.LINK_ID.eq(LINK.ID))
            ).execute();
    }
}
