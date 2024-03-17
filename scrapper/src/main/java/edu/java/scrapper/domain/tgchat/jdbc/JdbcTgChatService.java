package edu.java.scrapper.domain.tgchat.jdbc;

import edu.java.scrapper.domain.chatlink.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.tgchat.SchemaTgChatService;
import org.springframework.stereotype.Service;

@Service
public class JdbcTgChatService extends SchemaTgChatService {
    public JdbcTgChatService(JdbcTgChatDao jdbcTgChatDao, JdbcChatLinkDao jdbcChatLinkDao) {
        super(jdbcTgChatDao, jdbcChatLinkDao);
    }
}
