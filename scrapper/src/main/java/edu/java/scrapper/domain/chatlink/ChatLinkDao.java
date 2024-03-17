package edu.java.scrapper.domain.chatlink;

import java.util.List;
import java.util.Optional;

public interface ChatLinkDao {
    List<ChatLink> findAll();

    Optional<ChatLink> findById(Long chatId, Long linkId);

    ChatLink add(Long chatId, Long linkId);

    ChatLink remove(Long chatId, Long linkId);

    void removeDanglingLinks();
}
