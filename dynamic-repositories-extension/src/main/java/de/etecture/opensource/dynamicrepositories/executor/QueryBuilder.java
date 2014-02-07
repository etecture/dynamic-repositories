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
     * returns true, when the given method is a repository method. This means,
     * that this query builder can build a query object for any invocation of
     * this method.
     *
     * @param method
     * @return
     */
    boolean isRepositoryMethod(Method method);

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
