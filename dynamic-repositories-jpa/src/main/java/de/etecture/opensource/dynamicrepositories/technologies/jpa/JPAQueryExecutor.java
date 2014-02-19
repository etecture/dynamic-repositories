package de.etecture.opensource.dynamicrepositories.technologies.jpa;

import de.etecture.opensource.dynamicrepositories.api.DefaultQueryHints;
import de.etecture.opensource.dynamicrepositories.executor.NoResultException;
import de.etecture.opensource.dynamicrepositories.executor.NonUniqueResultException;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionContext;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionException;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.executor.Technology;
import de.etecture.opensource.dynamicrepositories.utils.DefaultLiteral;
import de.etecture.opensource.dynamicrepositories.utils.NamedLiteral;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
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

    private static final Logger LOG = Logger.getLogger(JPAQueryExecutor.class
            .getName());
    @Inject
    @Any
    Instance<AbstractConnection> jpaConnections;
    @Inject
    BeanManager beans;

    @Override
    public Object execute(
            QueryExecutionContext context) throws QueryExecutionException {
        try {
            AbstractConnection connection = resolve(context.getQuery()
                    .getConnection());
            switch (context.getQueryHintValue(JPAQueryHints.QUERY_KIND)
                    .toString()) {
                case "CREATE":
                    return create(connection, context);
                case "UPDATE":
                    return update(connection, context);
                case "DELETE":
                    return delete(connection, context);
                case "RETRIEVE":
                default:
                    return retrieve(connection, context);
            }
        } catch (EntityNotFoundException | javax.persistence.NoResultException ex) {
            throw new NoResultException(context, ex);
        } catch (EntityExistsException ex) {
            throw new NonUniqueResultException(context, ex);
        } catch (PersistenceException ex) {
            throw new QueryExecutionException(context, "cannot execute query",
                    ex);
        }
    }

    private AbstractConnection resolve(String connection) {
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

    protected Object create(AbstractConnection connection,
            QueryExecutionContext<?> query)
            throws
            QueryExecutionException {
        throw new UnsupportedOperationException(
                "jpa create queries not yet supported!");
    }

    protected Object update(AbstractConnection connection,
            QueryExecutionContext<?> query)
            throws
            QueryExecutionException {
        throw new UnsupportedOperationException(
                "jpa update queries not yet supported!");
    }

    protected Object delete(AbstractConnection connection,
            QueryExecutionContext<?> query)
            throws
            QueryExecutionException {
        throw new UnsupportedOperationException(
                "jpa delete queries not yet supported!");
    }

    protected Object retrieve(AbstractConnection connection,
            QueryExecutionContext<?> context)
            throws
            QueryExecutionException {
        LOG.log(Level.FINE, "execute RETRIEVE query: {0}", context);
        int limit = (int) context.getQueryHintValue(DefaultQueryHints.LIMIT, -1);
        if (limit == 1) {
            return connection.getSingleResult(context);
        } else {
            return connection.getResultList(context);
        }
    }
}
