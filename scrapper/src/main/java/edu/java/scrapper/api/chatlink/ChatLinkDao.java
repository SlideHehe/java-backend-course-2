package edu.java.scrapper.api.chatlink;

import java.util.List;
import java.util.Optional;

public interface ChatLinkDao {
    List<ChatLink> findAll();

    Optional<ChatLink> findById(Long chatId, Long linkId);

    Optional<ChatLink> add(Long chatId, Long linkId);

    Optional<ChatLink> remove(Long chatId, Long linkId);
}
