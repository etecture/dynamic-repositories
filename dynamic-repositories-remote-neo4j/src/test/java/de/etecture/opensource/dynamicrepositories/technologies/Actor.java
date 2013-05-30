package de.etecture.opensource.dynamicrepositories.technologies;

import java.util.List;

/**
 * represents a sample entity for test purposes.
 *
 * @author rhk
 */
public interface Actor {

    String getName();

    List<String> getRoles();

    Movie getInitialMovie();

    List<Movie> getMovies();
}
