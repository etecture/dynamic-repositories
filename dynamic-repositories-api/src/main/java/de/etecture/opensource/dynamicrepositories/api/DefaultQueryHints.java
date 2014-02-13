package de.etecture.opensource.dynamicrepositories.api;

/**
 * some default Query-Hints
 *
 * @author rhk
 * @version
 * @since
 */
public interface DefaultQueryHints {

    /**
     * the expected limit/maximum results to be returned.
     */
    String LIMIT = "RESULT_LIMIT";
    /**
     * the expected start position in a paged result.
     */
    String SKIP = "RESULT_SKIP";
}
