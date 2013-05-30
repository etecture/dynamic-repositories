package de.etecture.opensource.dynamicrepositories.technologies;

/**
 * represents a sample entity for test purposes.
 *
 * @author rhk
 */
public interface Movie {

    String getTitle();

    String getYear();

    Actor getActor(String role);
}
