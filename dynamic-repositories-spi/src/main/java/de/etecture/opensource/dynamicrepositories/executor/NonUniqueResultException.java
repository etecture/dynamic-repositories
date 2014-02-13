package de.etecture.opensource.dynamicrepositories.executor;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@SuppressWarnings("serial")
public class NonUniqueResultException extends QueryExecutionException {

    public NonUniqueResultException(QueryExecutionContext query, String message) {
        super(query, message);
    }

    public NonUniqueResultException(QueryExecutionContext query, String message, Throwable cause) {
        super(query, message, cause);
    }

    public NonUniqueResultException(QueryExecutionContext query, Throwable cause) {
        super(query, cause);
    }
}
