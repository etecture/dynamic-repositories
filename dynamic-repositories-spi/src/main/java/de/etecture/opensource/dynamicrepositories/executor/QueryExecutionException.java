package de.etecture.opensource.dynamicrepositories.executor;

/**
 * the base exception for all context executors.
 *
 * @author rhk
 * @version
 * @since
 */
public class QueryExecutionException extends Exception {

    private static final long serialVersionUID = 1L;
    private final QueryExecutionContext context;

    /**
     *
     * @param context
     * @param message
     */
    public QueryExecutionException(QueryExecutionContext context, String message) {
        super(message);
        this.context = context;
    }

    /**
     *
     * @param context
     * @param message
     * @param cause
     */
    public QueryExecutionException(QueryExecutionContext context, String message,
            Throwable cause) {
        super(message, cause);
        this.context = context;
    }

    /**
     *
     * @param context
     * @param cause
     */
    public QueryExecutionException(QueryExecutionContext context,
            Throwable cause) {
        super(cause);
        this.context = context;
    }

    /**
     * returns the context, that raises this exception.
     *
     * @return
     */
    public QueryExecutionContext getQueryExecutionContext() {
        return context;
    }
}
