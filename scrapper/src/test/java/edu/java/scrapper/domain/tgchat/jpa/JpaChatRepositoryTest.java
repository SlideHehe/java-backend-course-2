package edu.java.scrapper.domain.tgchat.jpa;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.links.jpa.JpaLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JpaChatRepositoryTest extends IntegrationTest {
    @Autowired
    JpaLinkRepository linkRepository;
    @Autowired
    JpaChatRepository chatRepository;
    @Autowired
    JdbcClient jdbcClient;

    @BeforeEach
    public void resetSequence() {
        jdbcClient.sql("truncate link cascade").update();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("Поиск чатов, отслеживающих определенные ссылки")
    void findByLinks_Id() {
        // given
        jdbcClient.sql("insert into chat (id) values (1), (2)").update();
        jdbcClient.sql("insert into link (id, url, type) values (1, 'localhost', 'GITHUB')").update();
        jdbcClient.sql("insert into chat_link (chat_id, link_id) values (1, 1)").update();

        // when
        var actualList = chatRepository.findByLinks_Id(1L);

        // then
        assertThat(actualList).hasSize(1);
        assertThat(actualList.getFirst().getId()).isEqualTo(1);
    }
}
