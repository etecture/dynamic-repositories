package de.etecture.opensource.dynamicrepositories.executor;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@SuppressWarnings("serial")
public class NonUniqueResultException extends QueryExecutionException {

    public NonUniqueResultException(Query query, String message) {
        super(query, message);
    }

    public NonUniqueResultException(Query query, String message, Throwable cause) {
        super(query, message, cause);
    }

    public NonUniqueResultException(Query query, Throwable cause) {
        super(query, cause);
    }
}
