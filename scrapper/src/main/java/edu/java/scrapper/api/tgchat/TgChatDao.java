package edu.java.scrapper.api.tgchat;

import java.util.List;
import java.util.Optional;

public interface TgChatDao {
    List<TgChat> findAll();

    Optional<TgChat> findById(Long id);

    Optional<TgChat> add(Long id);

    Optional<TgChat> remove(Long id);
}
