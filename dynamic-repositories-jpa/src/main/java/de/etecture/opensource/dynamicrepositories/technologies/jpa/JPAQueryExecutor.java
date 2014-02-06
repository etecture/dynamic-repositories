package de.etecture.opensource.dynamicrepositories.technologies.jpa;

import de.etecture.opensource.dynamicrepositories.executor.Query;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionException;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.Technology;

/**
 *
 * @author rhk
 * @version
 * @since

 */
@Technology("JPA")
public class JPAQueryExecutor implements QueryExecutor {

    @Override
    public <R> R execute(
            Query<R> query) throws QueryExecutionException {
        switch (query.getQueryHintValue(JPAQueryHints.QUERY_KIND).toString()) {
            case "CREATE":
                return create(query);
            case "UPDATE":
                return update(query);
            case "DELETE":
                return delete(query);
            case "RETRIEVE":
            default:
                return retrieve(query);
        }
    }

    protected <R> R create(Query<R> query) throws QueryExecutionException {
        return null;
    }

    protected <R> R update(Query<R> query) throws QueryExecutionException {
        return null;
    }

    protected <R> R delete(Query<R> query) throws QueryExecutionException {
        return null;
    }

    protected <R> R retrieve(Query<R> query) throws QueryExecutionException {
        return null;
    }
}
