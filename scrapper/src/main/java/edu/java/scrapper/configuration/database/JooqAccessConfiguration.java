package edu.java.scrapper.configuration.database;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.chatlink.schemabased.jooq.JooqChatLinkDao;
import edu.java.scrapper.domain.links.LinkService;
import edu.java.scrapper.domain.links.schemabased.jooq.JooqLinkDao;
import edu.java.scrapper.domain.links.schemabased.jooq.JooqSchemaBasedLinkService;
import edu.java.scrapper.domain.tgchat.TgChatService;
import edu.java.scrapper.domain.tgchat.schemabased.jooq.JooqSchemaBasedTgChatService;
import edu.java.scrapper.domain.tgchat.schemabased.jooq.JooqTgChatDao;
import edu.java.scrapper.scheduler.linkupdater.LinkUpdater;
import edu.java.scrapper.scheduler.linkupdater.resourceupdater.ResourceUpdater;
import edu.java.scrapper.scheduler.linkupdater.schemabased.jooq.JooqSchemaBasedLinkUpdater;
import edu.java.scrapper.scheduler.updateproducer.UpdateProducer;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {
    @Bean
    public LinkService linkService(
        JooqLinkDao jooqLinkDao,
        JooqChatLinkDao jooqChatLinkDao
    ) {
        return new JooqSchemaBasedLinkService(jooqLinkDao, jooqChatLinkDao);
    }

    @Bean
    public TgChatService tgChatService(
        JooqTgChatDao jooqTgChatDao,
        JooqChatLinkDao jooqChatLinkDao
    ) {
        return new JooqSchemaBasedTgChatService(jooqTgChatDao, jooqChatLinkDao);
    }

    @Bean
    public LinkUpdater linkUpdater(
        JooqTgChatDao jooqTgChatDao,
        JooqLinkDao jooqLinkDao,
        UpdateProducer updateProducer,
        ApplicationConfig applicationConfig,
        List<ResourceUpdater> resourceUpdaters
    ) {
        return new JooqSchemaBasedLinkUpdater(
            jooqTgChatDao,
            jooqLinkDao,
                updateProducer,
            applicationConfig,
            resourceUpdaters
        );
    }
}
