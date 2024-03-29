package edu.java.scrapper.domain.tgchat.schemabased;

import java.util.List;
import java.util.Optional;

public interface TgChatDao {
    List<TgChat> findAll();

    List<TgChat> findAllByLinkId(Long linkId);

    Optional<TgChat> findById(Long id);

    TgChat add(Long id);

    TgChat remove(Long id);
}
