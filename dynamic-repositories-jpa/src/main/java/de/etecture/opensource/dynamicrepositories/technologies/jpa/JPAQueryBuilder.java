package de.etecture.opensource.dynamicrepositories.technologies.jpa;

import de.etecture.opensource.dynamicrepositories.api.Create;
import de.etecture.opensource.dynamicrepositories.api.Delete;
import de.etecture.opensource.dynamicrepositories.api.Update;
import de.etecture.opensource.dynamicrepositories.executor.AbstractQuery;
import de.etecture.opensource.dynamicrepositories.executor.AbstractQueryBuilder;
import de.etecture.opensource.dynamicrepositories.executor.Query;
import de.etecture.opensource.dynamicrepositories.spi.Technology;
import java.lang.reflect.Method;

/**
 * builds a JPA query
 *
 * @author rhk
 * @version
 * @since
 */
@Technology("JPA")
public class JPAQueryBuilder extends AbstractQueryBuilder {

    @Override
    protected <R, Q extends AbstractQuery<R>> Q createQuery(
            Class<R> resultType, String technology, String connection,
            String statement) {
        return (Q) new JPAQuery(resultType, technology, connection, statement);
    }

    @Override
    public <R> Query<R> buildQuery(Method method, Object... args) {
        final AbstractQuery<R> query = (AbstractQuery<R>) super.buildQuery(
                method, args);

        if (method.isAnnotationPresent(Create.class)) {
            addHint(query, JPAQueryHints.QUERY_KIND, "CREATE");
        } else if (method.isAnnotationPresent(Update.class)) {
            addHint(query, JPAQueryHints.QUERY_KIND, "UPDATE");
        } else if (method.isAnnotationPresent(Delete.class)) {
            addHint(query, JPAQueryHints.QUERY_KIND, "DELETE");
        } else {
            addHint(query, JPAQueryHints.QUERY_KIND, "RETRIEVE");
        }

        if (!query.hasHint(JPAQueryHints.QUERY_TYPE)) {
            addHint(query, JPAQueryHints.QUERY_TYPE, "NAMED");
        }

        return query;
    }
}
