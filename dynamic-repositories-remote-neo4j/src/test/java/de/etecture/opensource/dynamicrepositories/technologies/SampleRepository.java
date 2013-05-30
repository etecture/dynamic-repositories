package de.etecture.opensource.dynamicrepositories.technologies;

import de.etecture.opensource.dynamicrepositories.api.ParamName;
import de.etecture.opensource.dynamicrepositories.api.Query;
import de.etecture.opensource.dynamicrepositories.api.Repository;
import de.etecture.opensource.dynamicrepositories.api.Retrieve;
import java.util.List;

/**
 * this is a sample repository for test purposes.
 *
 * @author rhk
 */
@Repository
public interface SampleRepository {

    @Retrieve
    @Query(value = "MATCH (n:Actor) \n"
            + "WHERE n.name = {actorname} \n"
            + "RETURN n.name as `name`",
            converter = ProxyConverter.class)
    Actor findPersonByName(@ParamName("actorname") String name);

    @Retrieve
    @Query(value = "MATCH (n:Actor)-[r:ACTS_IN]->(initialMovie:Movie) \n"
            + "WHERE n.name = {actorname} AND initialMovie.title = {initialmovietitle}\n"
            + "RETURN n.name as `name`, initialMovie.title, initialMovie.year",
            converter = ProxyConverter.class)
    Actor findPersonWithInitialMovieByName(@ParamName("actorname") String name, @ParamName("initialmovietitle") String title);

    @Retrieve
    @Query(value = "MATCH (n:Actor)-[r:ACTS_IN]->(m:Movie) \n"
            + "WHERE n.name = {actorname} \n"
            + "RETURN n.name as `name`, collect(m.title) as `movies.title`, collect(m.year) as `movies.year`, collect(DISTINCT r.role) AS `roles`",
            converter = ProxyConverter.class)
    Actor findPersonWithMoviesByName(@ParamName("actorname") String name);

    @Retrieve
    @Query(value = "MATCH (n:Actor)-[r:ACTS_IN]->(m:Movie) \n"
            + "WHERE n.name = {actorname} \n"
            + "RETURN m.title as `title`, m.year as `year`",
            converter = ProxyConverter.class)
    List<Movie> findMoviesWherePersonIsAnActor(@ParamName("actorname") String name);

    @Retrieve
    @Query(value = "MATCH (n:Actor)-[r:ACTS_IN]->(m:Movie) \n"
            + "WHERE m.title = {movietitle} \n"
            + "RETURN m.title as `title`, m.year as `year`, collect(n.name) AS `actors.name`, collect(r.role) AS `actors.role`",
            converter = ProxyConverter.class)
    Movie findMovieWithActors(@ParamName("movietitle") String title);
}
