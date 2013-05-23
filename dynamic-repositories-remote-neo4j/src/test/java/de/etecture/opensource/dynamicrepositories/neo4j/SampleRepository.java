package de.etecture.opensource.dynamicrepositories.neo4j;

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
    @Query()

}
