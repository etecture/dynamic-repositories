package de.etecture.opensource.dynamicrepositories.executor;

/**
 * some default Query-Hints
 *
 * @author rhk
 * @version
 * @since
 */
public interface QueryHints {

    /**
     * the expected limit/maximum results to be returned.
     */
    String LIMIT = "RESULT_LIMIT";
    /**
     * the expected start position in a paged result.
     */
    String SKIP = "RESULT_SKIP";
}
