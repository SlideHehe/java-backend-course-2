package edu.java.bot.links;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    Optional<User> findById(Long id);

    User save(User user);
}
