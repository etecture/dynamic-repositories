package de.etecture.opensource.dynamicrepositories.spi;

/**
 * resolves the QueryExecutor for a specific Technology.
 *
 * @author rhk
 * @version
 * @since
 */
public interface QueryExecutorResolver {

    /**
     * returns the QueryExecutor for the given Technology or null.
     *
     * @param technology
     * @return
     */
    QueryExecutor getQueryExecutorForTechnology(String technology);

    /**
     * returns the queryExecutor for the default technology or null, if no such
     * executor can be found.
     *
     * @return
     */
    QueryExecutor getDefaultExecutor();
}
