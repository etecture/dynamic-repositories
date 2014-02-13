package de.etecture.opensource.dynamicrepositories.executor;

import de.etecture.opensource.dynamicrepositories.metadata.QueryDefinition;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * represents a query.
 *
 * @param <R> the type of the result of this query
 * @author rhk
 * @version
 * @since
 */
public interface QueryExecutionContext<R> {

    /**
     * returns the query for this execution context.
     *
     * @return
     */
    QueryDefinition getQuery();

    /**
     * the expected type of the result for this query
     *
     * @return
     */
    Class<R> getResultType();

    /**
     * the expected type of the result for this query
     *
     * @return
     */
    Type getGenericResultType();

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
     * returns the value for a specific parameter
     *
     * @param name
     * @param defaultValue
     * @return
     */
    Object getParameterValue(String name, Object defaultValue);

    /**
     * returns the parameters as an unmodifiable map.
     *
     * @return
     */
    Map<String, Object> getParameters();

    /**
     * returns true, if the hint with the name is specified.
     *
     * @param name
     * @return
     */
    boolean hasQueryHint(String name);

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
     * returns the value for the specific query hint.
     *
     * @param name
     * @param defaultValue
     * @return
     */
    Object getQueryHintValue(String name, Object defaultValue);
}
