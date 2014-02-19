package de.etecture.opensource.dynamicrepositories.technologies.neo4j;

import de.etecture.opensource.dynamicrepositories.api.DefaultQueryHints;
import de.etecture.opensource.dynamicrepositories.executor.NoResultException;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionContext;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionException;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutor;
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
            QueryExecutionContext context) throws QueryExecutionException {
        LOG.log(Level.FINE, "execute RETRIEVE context: {0}", context);
        int limit = (int) context.getQueryHintValue(DefaultQueryHints.LIMIT, -1);
        if (limit == 1) {
            return getSingleResult(context);
        } else {
            return getResultList(context);
        }
    }

    private <R> R getSingleResult(
            QueryExecutionContext<R> context) throws QueryExecutionException {
        List<R> list = getResultList(context);
        if (list.isEmpty()) {
            throw new NoResultException(context,
                    "the result list was empty. So cannot return single result");
        } else {
            return list.get(0);
        }
    }

    private <R> List<R> getResultList(
            QueryExecutionContext<R> context) throws QueryExecutionException {
        try {
            Neo4jUplink uplink = resolve(context.getQuery().getConnection());
            return uplink.executeCypherQuery(context.getResultType(),
                    context
                    .getQuery().getStatement(), context.getParameters());
        } catch (Neo4jServerException | CypherResultMappingException ex) {
            throw new QueryExecutionException(context, ex);
        }
    }
}
