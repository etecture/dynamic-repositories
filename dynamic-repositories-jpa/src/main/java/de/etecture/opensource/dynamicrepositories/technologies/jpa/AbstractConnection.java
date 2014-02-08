package de.etecture.opensource.dynamicrepositories.technologies.jpa;

import de.etecture.opensource.dynamicrepositories.executor.Query;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionException;
import de.etecture.opensource.dynamicrepositories.executor.QueryHints;
import de.herschke.converters.api.ConvertException;
import de.herschke.converters.api.Converters;
import de.herschke.converters.api.TypedConverter;
import java.util.AbstractList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author rhk
 * @version
 * @since
 *
 */
public abstract class AbstractConnection {

    private static final Logger LOG = Logger.getLogger(AbstractConnection.class
            .getName());
    @Inject
    Converters converters;

    protected abstract EntityManager getEntityManager();

    protected javax.persistence.Query createQuery(Query<?> query) {
        EntityManager em = getEntityManager();
        javax.persistence.Query jpaQuery;
        Object queryType = null;
        if (query.hasQueryHint(JPAQueryHints.QUERY_TYPE)) {
            queryType = query.getQueryHintValue(JPAQueryHints.QUERY_TYPE);
        }
        if (queryType == null) {
            queryType = "JPAQL";
        }
        switch (queryType.toString()) {
            case "NAMED":
                LOG.log(Level.FINE, "create JPA NamedQuery with name: {0}",
                        query.getStatement());
                jpaQuery = em.createNamedQuery(query.getStatement(), query
                        .getResultType());
                break;
            case "NATIVE":
                LOG
                        .log(Level.FINE,
                        "create JPA NativeQuery with statement: {0}", query
                        .getStatement());
                jpaQuery = em.createNativeQuery(query.getStatement(), query
                        .getResultType());
                break;
            default:
                LOG.log(Level.FINE, "create JPA Query with statement: {0}",
                        query.getStatement());
                jpaQuery = em.createQuery(query.getStatement(), query
                        .getResultType());

        }
        int limit = (int) query.getQueryHintValue(QueryHints.LIMIT, -1);
        if (limit > 0) {
            LOG.log(Level.FINE, "add: {0} as max result to jpa query", limit);
            jpaQuery.setMaxResults(limit);
        }
        int skip = (int) query.getQueryHintValue(QueryHints.SKIP, 0);
        if (skip >= 0) {
            LOG.log(Level.FINE, "add: {0} as offset to jpa query", skip);
            jpaQuery.setFirstResult(skip);
        }
        for (String parameterName : query.getParameterNames()) {
            final Object parameterValue = query.getParameterValue(parameterName);
            LOG
                    .log(Level.FINE,
                    "add parameter: {0} with value {1} to jpa query",
                    new Object[]{parameterName,
                converters.toString(parameterValue)});
            jpaQuery.setParameter(parameterName, parameterValue);
        }
        return jpaQuery;
    }

    public <T> T convert(Query<T> query, Object value) throws ConvertException {
        TypedConverter<?> converter;
        if (StringUtils.isNotBlank(query.getConverter())) {
            LOG.log(Level.FINE, "use converter: {0} to convert the result",
                    query.getConverter());
            converter = converters.select(query.getConverter());
        } else {
            LOG.log(Level.FINE, "lookup a typed converter for result type: {0}",
                    query.getResultType());
            converter = converters.select(query.getResultType());
        }
        return query.getResultType().cast(converter.convert(value));
    }

    public <T> T getSingleResult(final Query<T> query) throws
            QueryExecutionException {
        javax.persistence.Query jpaQuery = createQuery(query);
        try {
            LOG.log(Level.FINER, "get single result for query: {0}", jpaQuery);
            return convert(query, jpaQuery.getSingleResult());
        } catch (NoResultException ex) {
            throw new de.etecture.opensource.dynamicrepositories.executor.NoResultException(
                    query, ex);
        } catch (ConvertException ex) {
            throw new QueryExecutionException(query,
                    "cannot convert the result for the query", ex);
        }
    }

    public <T> List<T> getResultList(final Query<T> query) {
        javax.persistence.Query jpaQuery = createQuery(query);
        LOG.log(Level.FINER, "get result list for query: {0}", jpaQuery);
        final List<?> resultList = jpaQuery.getResultList();
        return new AbstractList<T>() {
            @Override
            public T get(int index) {
                try {
                    return convert(query, resultList.get(index));
                } catch (ConvertException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public int size() {
                return resultList.size();
            }
        };
    }
}
