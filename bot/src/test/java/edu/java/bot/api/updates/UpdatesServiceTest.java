package edu.java.bot.api.updates;

import edu.java.bot.api.exception.UpdateAlreadyExistsException;
import edu.java.bot.api.updates.dto.LinkUpdateRequest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UpdatesServiceTest {
    @Test
    @DisplayName("Успешное создание обновления")
    void successfulUpdate() {
        // given
        UpdatesService updatesService = new UpdatesService();

        // when-then
        assertThatNoException().isThrownBy(() -> updatesService.createUpdate(new LinkUpdateRequest(
            1L,
            URI.create("https://aboba.com"),
            "description",
            List.of(1L, 2L, 3L)
        )));
    }

    @Test
    @DisplayName("Создание обновления с уже существующим id")
    void alreadyExistingUpdate() {
        // given
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(
            1L,
            URI.create("https://aboba.com"),
            "description",
            List.of(1L, 2L, 3L)
        );
        UpdatesService updatesService = new UpdatesService();
        updatesService.createUpdate(linkUpdateRequest);

        // when-then
        assertThatThrownBy(() -> updatesService.createUpdate(linkUpdateRequest))
            .isInstanceOf(UpdateAlreadyExistsException.class)
            .hasMessage("Update с переданным id уже обработан");
    }
}
