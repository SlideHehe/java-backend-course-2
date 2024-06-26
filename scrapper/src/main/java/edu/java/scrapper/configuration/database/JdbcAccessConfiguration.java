package edu.java.scrapper.configuration.database;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.domain.chatlink.schemabased.jdbc.JdbcChatLinkDao;
import edu.java.scrapper.domain.links.LinkService;
import edu.java.scrapper.domain.links.schemabased.jdbc.JdbcLinkDao;
import edu.java.scrapper.domain.links.schemabased.jdbc.JdbcSchemaBasedLinkService;
import edu.java.scrapper.domain.tgchat.TgChatService;
import edu.java.scrapper.domain.tgchat.schemabased.jdbc.JdbcSchemaBasedTgChatService;
import edu.java.scrapper.domain.tgchat.schemabased.jdbc.JdbcTgChatDao;
import edu.java.scrapper.scheduler.linkupdater.LinkUpdater;
import edu.java.scrapper.scheduler.linkupdater.resourceupdater.ResourceUpdater;
import edu.java.scrapper.scheduler.linkupdater.schemabased.jdbc.JdbcSchemaBasedLinkUpdater;
import edu.java.scrapper.scheduler.updateproducer.UpdateProducer;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    public LinkService linkService(
        JdbcLinkDao jdbcLinkDao,
        JdbcChatLinkDao jdbcChatLinkDao
    ) {
        return new JdbcSchemaBasedLinkService(jdbcLinkDao, jdbcChatLinkDao);
    }

    @Bean
    public TgChatService tgChatService(
        JdbcTgChatDao jdbcTgChatDao,
        JdbcChatLinkDao jdbcChatLinkDao
    ) {
        return new JdbcSchemaBasedTgChatService(jdbcTgChatDao, jdbcChatLinkDao);
    }

    @Bean
    public LinkUpdater linkUpdater(
        JdbcTgChatDao jdbcTgChatDao,
        JdbcLinkDao jdbcLinkDao,
        UpdateProducer updateProducer,
        ApplicationConfig applicationConfig,
        List<ResourceUpdater> resourceUpdaters
    ) {
        return new JdbcSchemaBasedLinkUpdater(
            jdbcTgChatDao,
            jdbcLinkDao,
            updateProducer,
            applicationConfig,
            resourceUpdaters
        );
    }
}
