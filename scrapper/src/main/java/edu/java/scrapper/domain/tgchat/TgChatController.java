package edu.java.scrapper.domain.tgchat;

import edu.java.scrapper.domain.tgchat.schemabased.jdbc.JdbcSchemaBasedTgChatService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/tg-chat")
public class TgChatController {
    private final JdbcSchemaBasedTgChatService tgChatService;

    @PostMapping("/{id}")
    public void registerChat(@PathVariable @Min(1) Long id) {
        tgChatService.registerChat(id);
    }

    @DeleteMapping("/{id}")
    public void deleteChat(@PathVariable @Min(1) Long id) {
        tgChatService.deleteChat(id);
    }
}
