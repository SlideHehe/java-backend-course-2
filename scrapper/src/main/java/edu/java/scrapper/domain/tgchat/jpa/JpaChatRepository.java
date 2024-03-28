package edu.java.scrapper.domain.tgchat.jpa;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface JpaChatRepository extends CrudRepository<Chat, Long> {
    @SuppressWarnings("MethodName")
    List<Chat> findByLinks_Id(Long id);
}
