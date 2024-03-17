package edu.java.scrapper.domain.tgchat.jooq;

import edu.java.scrapper.domain.chatlink.jooq.JooqChatLinkDao;
import edu.java.scrapper.domain.tgchat.SchemaTgChatService;
import org.springframework.stereotype.Service;

@Service
public class JooqTgChatService extends SchemaTgChatService {
    public JooqTgChatService(JooqTgChatDao jooqTgChatDao, JooqChatLinkDao jooqChatLinkDao) {
        super(jooqTgChatDao, jooqChatLinkDao);
    }
}
