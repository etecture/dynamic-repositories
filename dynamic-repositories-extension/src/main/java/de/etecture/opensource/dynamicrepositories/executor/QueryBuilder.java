package de.etecture.opensource.dynamicrepositories.executor;

import java.lang.reflect.Method;

/**
 * a query builder creates a Query instance.
 *
 * @author rhk
 * @version
 * @since
 */
public interface QueryBuilder {

    /**
     * builds a query for the given method and the given method arguments.
     *
     * @param <R> the type of the query
     * @param method the method to build the query for
     * @param args the arguments that are given by invocation of this method
     * @return a fully defined query instance.
     */
    <R> Query<R> buildQuery(Method method, Object... args);
}
