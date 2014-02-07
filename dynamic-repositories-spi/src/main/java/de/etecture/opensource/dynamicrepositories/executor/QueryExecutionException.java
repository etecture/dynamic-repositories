package de.etecture.opensource.dynamicrepositories.executor;

/**
 * the base exception for all query executors.
 *
 * @author rhk
 * @version
 * @since
 */
public class QueryExecutionException extends Exception {

    private static final long serialVersionUID = 1L;
    private final Query query;

    /**
     *
     * @param query
     * @param message
     */
    public QueryExecutionException(Query query, String message) {
        super(message);
        this.query = query;
    }

    /**
     *
     * @param query
     * @param message
     * @param cause
     */
    public QueryExecutionException(Query query, String message, Throwable cause) {
        super(message, cause);
        this.query = query;
    }

    /**
     *
     * @param query
     * @param cause
     */
    public QueryExecutionException(Query query, Throwable cause) {
        super(cause);
        this.query = query;
    }

    /**
     * returns the query, that raises this exception.
     *
     * @return
     */
    public Query getQuery() {
        return query;
    }
}
