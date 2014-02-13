package de.etecture.opensource.dynamicrepositories.executor;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@SuppressWarnings("serial")
public class NoResultException extends QueryExecutionException {

    public NoResultException(QueryExecutionContext query, String message) {
        super(query, message);
    }

    public NoResultException(QueryExecutionContext query, String message, Throwable cause) {
        super(query, message, cause);
    }

    public NoResultException(QueryExecutionContext query, Throwable cause) {
        super(query, cause);
    }
}
