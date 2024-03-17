package edu.java.scrapper.domain.links.jooq;

import edu.java.scrapper.domain.chatlink.jooq.JooqChatLinkDao;
import edu.java.scrapper.domain.links.SchemaLinkService;
import org.springframework.stereotype.Service;

@Service
public class JooqLinkService extends SchemaLinkService {
    public JooqLinkService(JooqLinkDao jooqLinkDao, JooqChatLinkDao jooqChatLinkDao) {
        super(jooqLinkDao, jooqChatLinkDao);
    }
}
