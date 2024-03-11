package edu.java.scrapper.api.links;

import java.net.URI;
import java.util.List;

public interface LinkDao {
    List<Link> findAll();

    Link add(URI uri);

    Link remove(Long id);
}
