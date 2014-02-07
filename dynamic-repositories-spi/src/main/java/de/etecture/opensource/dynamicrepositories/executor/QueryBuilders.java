package de.etecture.opensource.dynamicrepositories.executor;

import java.lang.reflect.Method;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 * default implementation of a query builder.
 *
 * @author rhk
 * @version
 * @since
 */
public class QueryBuilders {

    @Inject
    @Any
    Instance<QueryBuilder> anyQueryBuilders;

    public boolean isRepositoryMethod(String technology, Method method) {
        return resolve(technology).isRepositoryMethod(method);
    }

    public <R> Query<R> buildQuery(String technology, Method method,
            Object... args) {
        return resolve(technology).buildQuery(method, args);
    }

    private QueryBuilder resolve(
            Instance<QueryBuilder> queryBuildersByTechnology,
            String technology) throws RuntimeException {
        if (queryBuildersByTechnology.isAmbiguous()) {
            throw new RuntimeException(
                    "More then one query builder defined for technology: "
                    + technology);
        } else if (queryBuildersByTechnology.isUnsatisfied()) {
            throw new RuntimeException(
                    "No query builder defined for technology: " + technology);
        } else {
            return queryBuildersByTechnology.get();
        }
    }

    private QueryBuilder resolve(final String technology) {
        if (anyQueryBuilders.isUnsatisfied()) {
            throw new RuntimeException(
                    "please provide QueryBuilder-Implementations!");
        } else if ("default".equalsIgnoreCase(technology)) {
            if (anyQueryBuilders.isAmbiguous()) {
                final Instance<QueryBuilder> defaultQueryBuilders =
                        anyQueryBuilders.select(
                        new AnnotationLiteral<Default>() {
                    private static final long serialVersionUID = 1L;
                });
                return resolve(defaultQueryBuilders, technology);
            } else {
                return anyQueryBuilders.get();
            }
        } else {
            final Instance<QueryBuilder> executorsByTechnology =
                    anyQueryBuilders.select(new TechnologyLiteral(technology));
            return resolve(executorsByTechnology, technology);
        }
    }
}
