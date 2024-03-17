package edu.java.scrapper.scheduler.jooq;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.links.jooq.JooqLinkDao;
import edu.java.scrapper.domain.tgchat.jooq.JooqTgChatDao;
import edu.java.scrapper.scheduler.SchemaLinkUpdater;
import edu.java.scrapper.scheduler.resourceupdater.ResourceUpdater;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class JooqLinkUpdater extends SchemaLinkUpdater {
    public JooqLinkUpdater(
        JooqTgChatDao jooqTgChatDao,
        JooqLinkDao jooqLinkDao,
        BotClient botClient,
        ApplicationConfig applicationConfig,
        List<ResourceUpdater> resourceUpdaters
    ) {
        super(jooqTgChatDao, jooqLinkDao, botClient, applicationConfig, resourceUpdaters);
    }
}
