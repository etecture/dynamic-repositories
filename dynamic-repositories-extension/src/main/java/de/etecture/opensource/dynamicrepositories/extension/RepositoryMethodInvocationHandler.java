package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.api.annotations.EntityAlreadyExists;
import de.etecture.opensource.dynamicrepositories.api.annotations.EntityNotFound;
import de.etecture.opensource.dynamicrepositories.executor.NoResultException;
import de.etecture.opensource.dynamicrepositories.executor.NonUniqueResultException;
import de.etecture.opensource.dynamicrepositories.executor.Query;
import de.etecture.opensource.dynamicrepositories.executor.QueryBuilder;
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
    QueryBuilder builder;
    @Inject
    QueryExecutors executors;
    private String technology;

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public Object invoke(Object proxy, Method method, Object[] args) throws
            Throwable {
        try {
            if (builder.isRepositoryMethod(method)) {
                Query<?> query = builder.buildQuery(technology, method, args);
                return executors.execute(query);
            } else {
                throw new UnsupportedOperationException(
                        "no implementation for method: " + method.getName());
            }
        } catch (Exception exception) {
            for (int i = 0; i < method.getExceptionTypes().length; i++) {
                Class<? extends Throwable> declaredException =
                        (Class<? extends Throwable>) method
                        .getExceptionTypes()[i];
                if (declaredException.isAssignableFrom(exception
                        .getClass())) {
                    throw exception;
                }
                if (declaredException.isAnnotationPresent(
                        EntityAlreadyExists.class)
                        && exception instanceof NonUniqueResultException) {
                    throw declaredException.getConstructor(Throwable.class)
                            .newInstance(exception);
                }
                if (declaredException.isAnnotationPresent(
                        EntityNotFound.class)
                        && exception instanceof NoResultException) {
                    throw declaredException.getConstructor(Throwable.class)
                            .newInstance(exception);
                }
            }
            throw new UnsupportedOperationException(String.format(
                    "The repository method: %s#%s does not declare to throw: %s - but this exception is raised in execution of this method!%nMessage of raised Exception is: %s",
                    method.getDeclaringClass().getSimpleName(), method.getName(),
                    exception.getClass().getSimpleName(), exception.getMessage()),
                    exception);
        }
    }
}
