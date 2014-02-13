package de.etecture.opensource.dynamicrepositories.metadata;

import java.util.Set;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public interface QueryDefinition {

    /**
     * the technology this query is defined for.
     *
     * @return
     */
    String getTechnology();

    /**
     * the connection name this query is defined for.
     *
     * @return
     */
    String getConnection();

    /**
     * the statement that this query represents.
     *
     * @return
     */
    String getStatement();

    /**
     * the name of a custom converter to be used to convert the result set.
     *
     * @return
     */
    String getConverter();

    /**
     * returns a set of hints defined for this query.
     *
     * @return
     */
    Set<QueryHintDefinition> getHints();
}
