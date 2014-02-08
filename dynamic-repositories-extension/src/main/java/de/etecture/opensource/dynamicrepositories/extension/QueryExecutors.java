package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.executor.Query;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionException;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.executor.TechnologyLiteral;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 * delegates to an executor for the given technology.
 *
 * @author rhk
 * @version
 * @since
 */
@Default
public class QueryExecutors {

    @Inject
    @Any
    Instance<QueryExecutor> anyExecutor;

    public Object execute(
            Query<?> query) throws QueryExecutionException {
        return resolve(query.getTechnology()).execute(query);
    }

    private QueryExecutor resolve(
            Instance<QueryExecutor> executorsByTechnology,
            String technology) throws RuntimeException {
        if (executorsByTechnology.isAmbiguous()) {
            throw new RuntimeException(
                    "More then one executors defined for technology: "
                    + technology);
        } else if (executorsByTechnology.isUnsatisfied()) {
            throw new RuntimeException(
                    "No executor defined for technology: " + technology);
        } else {
            return executorsByTechnology.get();
        }
    }

    private QueryExecutor resolve(final String technology) {
        if (anyExecutor.isUnsatisfied()) {
            throw new RuntimeException(
                    "please provide QueryExecutor-Implementations!");
        } else if ("default".equalsIgnoreCase(technology)) {
            if (anyExecutor.isAmbiguous()) {
                final Instance<QueryExecutor> defaultExecutors =
                        anyExecutor.select(new AnnotationLiteral<Default>() {
                    private static final long serialVersionUID = 1L;
                });
                return resolve(defaultExecutors, technology);
            } else {
                return anyExecutor.get();
            }
        } else {
            final Instance<QueryExecutor> executorsByTechnology =
                    anyExecutor.select(new TechnologyLiteral(technology));
            return resolve(executorsByTechnology, technology);
        }
    }
}
