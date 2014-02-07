package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.executor.Query;
import de.etecture.opensource.dynamicrepositories.executor.QueryBuilders;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutors;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import javax.inject.Inject;

/**
 * this {@link InvocationHandler} handles invocations of dynamic repository
 * methods.
 *
 * @author rhk
 * @version
 * @since
 */
public class RepositoryMethodInvocationHandler implements InvocationHandler {

    @Inject
    QueryBuilders builders;
    @Inject
    QueryExecutors executors;
    private String technology;

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws
            Throwable {
        if (builders.isRepositoryMethod(technology, method)) {
            Query<?> buildQuery = builders.buildQuery(technology, method,
                    args);
            return executors.execute(buildQuery);
        } else {
            throw new UnsupportedOperationException(
                    "no implementation for method: " + method.getName());
        }
    }
}
