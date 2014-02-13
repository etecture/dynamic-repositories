package de.etecture.opensource.dynamicrepositories.metadata;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public interface QueryHintDefinition<V> {

    /**
     * returns the query hint name
     *
     * @return
     */
    String getName();

    /**
     * returns the value of this hint.
     *
     * @return
     */
    V getValue();

    /**
     * returns the type of the hint value
     *
     * @return
     */
    Class<V> getValueType();
}
