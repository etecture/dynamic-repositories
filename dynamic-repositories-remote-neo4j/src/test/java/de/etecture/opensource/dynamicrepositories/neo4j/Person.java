package de.etecture.opensource.dynamicrepositories.neo4j;

import java.util.Collection;

/**
 * represents a sample entity for test purposes.
 *
 * @author rhk
 */
@DynamicNode
public interface Person {

    String getLastName();

    String getFirstName();

    int getAge();

    Collection<Address> getAddresses();
}
