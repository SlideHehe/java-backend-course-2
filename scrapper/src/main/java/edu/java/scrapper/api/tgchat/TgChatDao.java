package edu.java.scrapper.api.tgchat;

import java.util.List;

public interface TgChatDao {
    List<TgChat> findAll();

    TgChat add(Long id);

    TgChat remove(Long id);
}
