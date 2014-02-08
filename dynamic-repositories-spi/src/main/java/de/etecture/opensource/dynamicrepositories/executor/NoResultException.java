package de.etecture.opensource.dynamicrepositories.executor;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@SuppressWarnings("serial")
public class NoResultException extends QueryExecutionException {

    public NoResultException(Query query, String message) {
        super(query, message);
    }

    public NoResultException(Query query, String message, Throwable cause) {
        super(query, message, cause);
    }

    public NoResultException(Query query, Throwable cause) {
        super(query, cause);
    }
}
