package de.etecture.opensource.dynamicrepositories.neo4j;

import de.etecture.opensource.dynamicrepositories.api.ParamName;
import de.etecture.opensource.dynamicrepositories.api.Query;
import de.etecture.opensource.dynamicrepositories.api.Repository;
import de.etecture.opensource.dynamicrepositories.api.Retrieve;

/**
 * this is a sample repository for test purposes.
 *
 * @author rhk
 */
@Repository
public interface SampleRepository {

    @Retrieve
    @Query(value = "START person=node:persons(name={name}) MATCH person-[:lives_at]->address RETURN "
            + "person.firstName AS firstName, person.lastName AS lastName, person.age AS age, collect(address) AS addresses")
    Person findPersonByName(@ParamName("name") String name);
}
