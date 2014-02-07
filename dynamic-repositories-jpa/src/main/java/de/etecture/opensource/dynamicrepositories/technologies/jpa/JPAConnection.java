package de.etecture.opensource.dynamicrepositories.technologies.jpa;

import de.etecture.opensource.dynamicrepositories.executor.Query;
import de.etecture.opensource.dynamicrepositories.executor.QueryHints;
import java.util.AbstractList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author rhk
 * @version
 * @since
 *
 */
public abstract class JPAConnection {

    protected abstract EntityManager getEntityManager();

    protected javax.persistence.Query createQuery(Query<?> query) {
        EntityManager em = getEntityManager();
        javax.persistence.Query jpaQuery;
        switch (StringUtils.defaultIfBlank(
                query.getQueryHintValue(JPAQueryHints.QUERY_TYPE).toString(),
                "JPAQL")) {
            case "NAMED":
                jpaQuery = em.createNamedQuery(query.getStatement(), query
                        .getResultType());
                break;
            case "NATIVE":
                jpaQuery = em.createNativeQuery(query.getStatement(), query
                        .getResultType());
                break;
            default:
                jpaQuery = em.createQuery(query.getStatement(), query
                        .getResultType());

        }
        int limit = (int) query.getQueryHintValue(QueryHints.LIMIT, -1);
        if (limit > 0) {
            jpaQuery.setMaxResults(limit);
        }
        int skip = (int) query.getQueryHintValue(QueryHints.SKIP, 0);
        if (skip >= 0) {
            jpaQuery.setFirstResult(skip);
        }
        for (String parameterName : query.getParameterNames()) {
            jpaQuery.setParameter(parameterName, query.getParameterValue(
                    parameterName));
        }
        return jpaQuery;
    }

    public <T> T getSingleResult(final Query<T> query) {
        javax.persistence.Query jpaQuery = createQuery(query);
        if (jpaQuery instanceof TypedQuery) {
            return ((TypedQuery<T>) jpaQuery).getSingleResult();
        } else {
            return query.getResultType().cast(jpaQuery.getSingleResult());
        }
    }

    public <T> List<T> getResultList(final Query<T> query) {
        javax.persistence.Query jpaQuery = createQuery(query);
        if (jpaQuery instanceof TypedQuery) {
            return ((TypedQuery<T>) jpaQuery).getResultList();
        } else {
            final List<?> resultList =
                    jpaQuery.getResultList();
            return new AbstractList<T>() {
                @Override
                public T get(int index) {
                    return query.getResultType().cast(resultList.get(index));
                }

                @Override
                public int size() {
                    return resultList.size();
                }
            };

        }
    }
}
