package edu.java.bot.domain.updates;

import edu.java.bot.domain.updates.dto.LinkUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/updates")
public class UpdatesController {
    private final UpdatesService updatesService;

    @PostMapping
    public void createUpdate(@RequestBody @Valid LinkUpdateRequest linkUpdateRequest) {
        updatesService.createUpdate(linkUpdateRequest);
    }
}
