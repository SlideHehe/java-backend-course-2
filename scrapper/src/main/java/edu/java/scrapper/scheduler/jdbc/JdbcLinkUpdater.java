package edu.java.scrapper.scheduler.jdbc;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.links.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.tgchat.jdbc.JdbcTgChatDao;
import edu.java.scrapper.scheduler.SchemaLinkUpdater;
import edu.java.scrapper.scheduler.resourceupdater.ResourceUpdater;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class JdbcLinkUpdater extends SchemaLinkUpdater {
    public JdbcLinkUpdater(
        JdbcTgChatDao jdbcTgChatDao,
        JdbcLinkDao jdbcLinkDao,
        BotClient botClient,
        ApplicationConfig applicationConfig,
        List<ResourceUpdater> resourceUpdaters
    ) {
        super(jdbcTgChatDao, jdbcLinkDao, botClient, applicationConfig, resourceUpdaters);
    }
}
