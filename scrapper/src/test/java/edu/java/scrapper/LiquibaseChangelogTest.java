package edu.java.scrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatNoException;

class LiquibaseChangelogTest extends IntegrationTest {
    @Test
    @DisplayName("Проверка создания таблиц из changelog-а")
    void checkTablesCreation() {
        // given
        try (Connection connection = DriverManager.getConnection(
            POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(),
            POSTGRES.getPassword()
        ); Statement statement = connection.createStatement()) {

            // when-then
            assertThatNoException().isThrownBy(() -> statement.execute("select id, url, updated_at, checked_at from link"));
            assertThatNoException().isThrownBy(() -> statement.execute("select id, created_at from chat"));
            assertThatNoException().isThrownBy(() -> statement.execute("select chat_id, link_id from chat_link"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
