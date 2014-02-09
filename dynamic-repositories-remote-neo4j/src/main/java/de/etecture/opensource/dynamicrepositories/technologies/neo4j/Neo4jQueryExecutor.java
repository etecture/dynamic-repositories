package de.etecture.opensource.dynamicrepositories.technologies.neo4j;

import de.etecture.opensource.dynamicrepositories.executor.NoResultException;
import de.etecture.opensource.dynamicrepositories.executor.Query;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionException;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.executor.QueryHints;
import de.etecture.opensource.dynamicrepositories.executor.Technology;
import de.etecture.opensource.dynamicrepositories.utils.DefaultLiteral;
import de.etecture.opensource.dynamicrepositories.utils.NamedLiteral;
import de.herschke.neo4j.uplink.api.CypherResultMappingException;
import de.herschke.neo4j.uplink.api.Neo4jServerException;
import de.herschke.neo4j.uplink.api.Neo4jUplink;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@Technology("Neo4j")
public class Neo4jQueryExecutor implements QueryExecutor {

    private static final Logger LOG = Logger.getLogger(Neo4jQueryExecutor.class
            .getName());
    @Inject
    Instance<Neo4jUplink> uplinks;

    private Neo4jUplink resolve(String connection) {
        if (uplinks.isUnsatisfied()) {
            throw new InjectionException(
                    "no neo4j uplinks found. please provide implementation for connection: "
                    + connection);
        } else if (uplinks.isAmbiguous()) {
            if ("".equals(connection) || "default"
                    .equals(connection)) {
                return uplinks.select(new DefaultLiteral()).get();
            } else {
                for (Neo4jUplink uplink : uplinks) {
                    System.out.println("found: " + uplink.getClass().getName());
                }
                return uplinks.select(new NamedLiteral(connection)).get();
            }
        } else if ("".equals(connection) || "default".equals(connection)) {
            return uplinks.get();
        } else {
            throw new InjectionException(
                    "cannot find a neo4j-uplink for connection: " + connection);
        }
    }

    @Override
    public Object execute(
            Query<?> query) throws QueryExecutionException {
        LOG.log(Level.FINE, "execute RETRIEVE query: {0}", query);
        int limit = (int) query.getQueryHintValue(QueryHints.LIMIT, -1);
        if (limit == 1) {
            return getSingleResult(query);
        } else {
            return getResultList(query);
        }
    }

    private <R> R getSingleResult(
            Query<R> query) throws QueryExecutionException {
        List<R> list = getResultList(query);
        if (list.isEmpty()) {
            throw new NoResultException(query,
                    "the result list was empty. So cannot return single result");
        } else {
            return list.get(0);
        }
    }

    private <R> List<R> getResultList(
            Query<R> query) throws QueryExecutionException {
        try {
            Neo4jUplink uplink = resolve(query.getConnection());
            return uplink.executeCypherQuery(query.getResultType(), query
                    .getStatement(), query.getParameters());
        } catch (Neo4jServerException | CypherResultMappingException ex) {
            throw new QueryExecutionException(query, ex);
        }
    }
}
