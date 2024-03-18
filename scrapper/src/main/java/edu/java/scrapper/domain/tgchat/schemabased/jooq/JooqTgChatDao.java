package edu.java.scrapper.domain.tgchat.schemabased.jooq;

import edu.java.scrapper.domain.tgchat.schemabased.TgChat;
import edu.java.scrapper.domain.tgchat.schemabased.TgChatDao;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.domain.jooqcodegen.Tables.CHAT_LINK;
import static edu.java.scrapper.domain.jooqcodegen.tables.Chat.CHAT;

@RequiredArgsConstructor
@Repository
public class JooqTgChatDao implements TgChatDao {
    private final DSLContext context;

    @Override
    public List<TgChat> findAll() {
        return context.selectFrom(CHAT)
            .fetchInto(TgChat.class);
    }

    @Override
    public List<TgChat> findAllByLinkId(Long linkId) {
        return context.select(CHAT)
            .from(CHAT.join(CHAT_LINK).on(CHAT.ID.eq(CHAT_LINK.CHAT_ID)))
            .fetchInto(TgChat.class);
    }

    @Override
    public Optional<TgChat> findById(Long id) {
        return context.selectFrom(CHAT)
            .where(CHAT.ID.eq(id))
            .fetchOptionalInto(TgChat.class);
    }

    @Override
    public TgChat add(Long id) {
        return context.insertInto(CHAT)
            .set(CHAT.ID, id)
            .returningResult(CHAT)
            .fetchSingleInto(TgChat.class);
    }

    @Override
    public TgChat remove(Long id) {
        return context.deleteFrom(CHAT)
            .where(CHAT.ID.eq(id))
            .returningResult(CHAT)
            .fetchSingleInto(TgChat.class);
    }
}
