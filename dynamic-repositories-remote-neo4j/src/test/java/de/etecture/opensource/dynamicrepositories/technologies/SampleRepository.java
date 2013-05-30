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
    @Query(value = "MATCH (n:Actor)-[r:ACTS_IN]->(m:Movie) \n"
            + "WHERE n.name = {actorname} \n"
            + "RETURN m.title as `title`, m.year as `year`",
            converter = ProxyConverter.class)
    List<Movie> findMoviesWherePersonIsAnActor(@ParamName("actorname") String name);
}
