package de.etecture.opensource.dynamicrepositories.executor;

/**
 * a queryexecutor that technically executes the query and deliveres a result of
 * this query.
 *
 * @author rhk
 * @version
 * @since
 */
public interface QueryExecutor {

    /**
     * executes the query.
     *
     * @param query the query to
     * @return the result for this query
     * @throws QueryExecutionException
     */
    Object execute(Query<?> query) throws QueryExecutionException;
}
