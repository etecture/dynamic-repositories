package de.etecture.opensource.dynamicrepositories.executor;

import java.util.Set;

/**
 * represents a query.
 *
 * @param <R> the type of the result of this query
 * @author rhk
 * @version
 * @since
 */
public interface Query<R> {

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
     * returns true, if the parameter with the name is specified.
     *
     * @param name
     * @return
     */
    boolean hasParameter(String name);

    /**
     * returns a set of parameter names, defined in this query.
     *
     * @return
     */
    Set<String> getParameterNames();

    /**
     * returns the value for a specific parameter
     *
     * @param name
     * @return
     */
    Object getParameterValue(String name);

    /**
     * returns true, if the hint with the name is specified.
     *
     * @param name
     * @return
     */
    boolean hasHint(String name);

    /**
     * returns a set of query hint names.
     *
     * @return
     */
    Set<String> getQueryHints();

    /**
     * returns the value for the specific query hint.
     *
     * @param name
     * @return
     */
    Object getQueryHintValue(String name);

    /**
     * the expected type of the result for this query
     *
     * @return
     */
    Class<R> getResultType();
}
