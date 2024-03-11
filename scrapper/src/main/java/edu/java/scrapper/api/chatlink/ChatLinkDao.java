package edu.java.scrapper.api.chatlink;

import java.util.List;

public interface ChatLinkDao {
    List<ChatLink> findAll();

    ChatLink add(Long chatId, Long linkId);

    ChatLink remove(Long chatId, Long linkId);
}
