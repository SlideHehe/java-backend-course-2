package edu.java.scrapper.configuration;

import edu.java.scrapper.client.bot.BotClient;
import edu.java.scrapper.domain.links.LinkService;
import edu.java.scrapper.domain.links.jpa.JpaLinkRepository;
import edu.java.scrapper.domain.links.jpa.JpaLinkService;
import edu.java.scrapper.domain.tgchat.TgChatService;
import edu.java.scrapper.domain.tgchat.jpa.JpaChatRepository;
import edu.java.scrapper.domain.tgchat.jpa.JpaTgChatService;
import edu.java.scrapper.scheduler.LinkUpdater;
import edu.java.scrapper.scheduler.jpa.JpaLinkUpdater;
import edu.java.scrapper.scheduler.resourceupdater.ResourceUpdater;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Bean
    public LinkService linkService(
        JpaLinkRepository jpaLinkRepository,
        JpaChatRepository jpaChatRepository
    ) {
        return new JpaLinkService(jpaLinkRepository, jpaChatRepository);
    }

    @Bean
    TgChatService tgChatService(
        JpaLinkRepository jpaLinkRepository,
        JpaChatRepository jpaChatRepository
    ) {
        return new JpaTgChatService(jpaChatRepository, jpaLinkRepository);
    }

    @Bean
    LinkUpdater linkUpdater(
        JpaLinkRepository jpaLinkRepository,
        JpaChatRepository jpaChatRepository,
        BotClient botClient,
        ApplicationConfig applicationConfig,
        List<ResourceUpdater> resourceUpdaters
    ) {
        return new JpaLinkUpdater(
            jpaLinkRepository,
            jpaChatRepository,
            botClient,
            applicationConfig,
            resourceUpdaters
        );
    }
}
