package de.etecture.opensource.dynamicrepositories.technologies.jpa;

import de.etecture.opensource.dynamicrepositories.api.DefaultQueryHints;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionContext;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionException;
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

    protected javax.persistence.Query createQuery(
            QueryExecutionContext<?> context) {
        EntityManager em = getEntityManager();
        javax.persistence.Query jpaQuery;
        Object queryType = null;
        if (context.hasQueryHint(JPAQueryHints.QUERY_TYPE)) {
            queryType = context.getQueryHintValue(JPAQueryHints.QUERY_TYPE);
        }
        if (queryType == null) {
            queryType = "JPAQL";
        }
        switch (queryType.toString()) {
            case "NAMED":
                LOG.log(Level.FINE, "create JPA NamedQuery with name: {0}",
                        context.getQuery().getStatement());
                jpaQuery = em
                        .createNamedQuery(context.getQuery().getStatement(),
                        context
                        .getResultType());
                break;
            case "NATIVE":
                LOG
                        .log(Level.FINE,
                        "create JPA NativeQuery with statement: {0}", context
                        .getQuery().getStatement());
                jpaQuery = em.createNativeQuery(context.getQuery()
                        .getStatement(), context
                        .getResultType());
                break;
            default:
                LOG.log(Level.FINE, "create JPA Query with statement: {0}",
                        context.getQuery().getStatement());
                jpaQuery = em.createQuery(context.getQuery().getStatement(),
                        context
                        .getResultType());

        }
        int limit = (int) context.getQueryHintValue(DefaultQueryHints.LIMIT, -1);
        if (limit > 0) {
            LOG.log(Level.FINE, "add: {0} as max result to jpa query", limit);
            jpaQuery.setMaxResults(limit);
        }
        int skip = (int) context.getQueryHintValue(DefaultQueryHints.SKIP, 0);
        if (skip >= 0) {
            LOG.log(Level.FINE, "add: {0} as offset to jpa query", skip);
            jpaQuery.setFirstResult(skip);
        }
        for (String parameterName : context.getParameterNames()) {
            final Object parameterValue = context.getParameterValue(
                    parameterName);
            LOG
                    .log(Level.FINE,
                    "add parameter: {0} with value {1} to jpa query",
                    new Object[]{parameterName,
                converters.toString(parameterValue)});
            jpaQuery.setParameter(parameterName, parameterValue);
        }
        return jpaQuery;
    }

    public <T> T convert(QueryExecutionContext<T> context, Object value) throws
            ConvertException {
        TypedConverter<?> converter;
        if (StringUtils.isNotBlank(context.getQuery().getConverter())) {
            LOG.log(Level.FINE, "use converter: {0} to convert the result",
                    context.getQuery().getConverter());
            converter = converters.select(context.getQuery().getConverter());
        } else {
            LOG.log(Level.FINE, "lookup a typed converter for result type: {0}",
                    context.getResultType());
            converter = converters.select(context.getResultType());
        }
        return context.getResultType().cast(converter.convert(value));
    }

    public <T> T getSingleResult(final QueryExecutionContext<T> context) throws
            QueryExecutionException {
        javax.persistence.Query jpaQuery = createQuery(context);
        try {
            LOG.log(Level.FINER, "get single result for query: {0}", jpaQuery);
            return convert(context, jpaQuery.getSingleResult());
        } catch (NoResultException ex) {
            throw new de.etecture.opensource.dynamicrepositories.executor.NoResultException(
                    context, ex);
        } catch (ConvertException ex) {
            throw new QueryExecutionException(context,
                    "cannot convert the result for the query", ex);
        }
    }

    public <T> List<T> getResultList(final QueryExecutionContext<T> context) {
        javax.persistence.Query jpaQuery = createQuery(context);
        LOG.log(Level.FINER, "get result list for query: {0}", jpaQuery);
        final List<?> resultList = jpaQuery.getResultList();
        return new AbstractList<T>() {
            @Override
            public T get(int index) {
                try {
                    return convert(context, resultList.get(index));
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
