package edu.java.scrapper.api.links;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public interface LinkDao {
    List<Link> findAll();

    Optional<Link> findById(Long id);

    Optional<Link> findByUrl(URI uri);

    Optional<Link> add(URI url);

    Optional<Link> remove(Long id);
}
