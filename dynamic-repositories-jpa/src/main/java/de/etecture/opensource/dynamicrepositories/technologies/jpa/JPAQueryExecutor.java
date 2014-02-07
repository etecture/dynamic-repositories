package de.etecture.opensource.dynamicrepositories.technologies.jpa;

import de.etecture.opensource.dynamicrepositories.executor.Query;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionException;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.executor.QueryHints;
import de.etecture.opensource.dynamicrepositories.executor.Technology;
import de.etecture.opensource.dynamicrepositories.technologies.jpa.utils.DefaultLiteral;
import de.etecture.opensource.dynamicrepositories.technologies.jpa.utils.NamedLiteral;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author rhk
 * @version
 * @since
 *
 */
@Technology("JPA")
public class JPAQueryExecutor implements QueryExecutor {

    @Inject
    @Any
    Instance<JPAConnection> jpaConnections;
    @Inject
    BeanManager beans;

    @Override
    public Object execute(
            Query<?> query) throws QueryExecutionException {
        JPAConnection connection = resolve(query.getConnection());
        switch (query.getQueryHintValue(JPAQueryHints.QUERY_KIND).toString()) {
            case "CREATE":
                return create(connection, query);
            case "UPDATE":
                return update(connection, query);
            case "DELETE":
                return delete(connection, query);
            case "RETRIEVE":
            default:
                return retrieve(connection, query);
        }
    }

    private JPAConnection resolve(String connection) {
        if (StringUtils.isBlank(connection) || "default".equalsIgnoreCase(
                connection)) {
            if (jpaConnections.isAmbiguous()) {
                return jpaConnections.select(new DefaultLiteral()).get();
            } else if (!jpaConnections.isUnsatisfied()) {
                return jpaConnections.get();
            } else {
                throw new RuntimeException(
                        "there is no jpa-connection registered.");
            }
        } else {
            return jpaConnections.select(new NamedLiteral(connection)).get();
        }
    }

    protected Object create(JPAConnection connection, Query<?> query) throws
            QueryExecutionException {
        throw new UnsupportedOperationException(
                "jpa create queries not yet supported!");
    }

    protected Object update(JPAConnection connection, Query<?> query) throws
            QueryExecutionException {
        throw new UnsupportedOperationException(
                "jpa update queries not yet supported!");
    }

    protected Object delete(JPAConnection connection, Query<?> query) throws
            QueryExecutionException {
        throw new UnsupportedOperationException(
                "jpa delete queries not yet supported!");
    }

    protected Object retrieve(JPAConnection connection, Query<?> query) throws
            QueryExecutionException {
        int limit = (int) query.getQueryHintValue(QueryHints.LIMIT, -1);
        if (limit > 0) {
            return connection.getResultList(query);
        } else {
            return connection.getSingleResult(query);
        }
    }
}
