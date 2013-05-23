package de.etecture.opensource.dynamicrepositories.neo4j;

/**
 * represents a sample entity for test purposes.
 *
 * @author rhk
 */
@DynamicNode
public interface Address {

    String getStreet();

    String getCity();

    String getZipCode();
}
