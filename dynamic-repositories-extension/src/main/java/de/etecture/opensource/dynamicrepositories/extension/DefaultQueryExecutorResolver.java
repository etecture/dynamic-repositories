package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.spi.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.QueryExecutorResolver;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 * resolves the QueryExecutor by using the qualifier annotation.
 *
 * @author rhk
 * @version
 * @since
 */
@Default
public class DefaultQueryExecutorResolver implements QueryExecutorResolver {

    @Inject
    @Any
    Instance<QueryExecutor> executorInstances;

    @Override
    public QueryExecutor getQueryExecutorForTechnology(String technology) {
        return executorInstances.select(new TechnologyLiteral(technology)).get();
    }

    @Override
    public QueryExecutor getDefaultExecutor() {
        return executorInstances.select(new AnnotationLiteral<Default>() {
            private static final long serialVersionUID = 1L;
        }).get();
    }
}
