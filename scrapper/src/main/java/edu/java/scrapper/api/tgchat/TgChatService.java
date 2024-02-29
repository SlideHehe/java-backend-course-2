package edu.java.scrapper.api.tgchat;

import edu.java.scrapper.api.exception.ChatAlreadyExistsException;
import edu.java.scrapper.api.exception.ResourceNotFoundException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class TgChatService {
    private final Set<Long> ids = new HashSet<>(); // TODO remove when data layer is done

    public void registerChat(Long id) {
        if (!ids.add(id)) {
            throw new ChatAlreadyExistsException("Указанный чат уже зарегестрирован");
        }
    }

    public void deleteChat(Long id) {
        if (!ids.remove(id)) {
            throw new ResourceNotFoundException("Указанный чат не существует");
        }
    }
}
