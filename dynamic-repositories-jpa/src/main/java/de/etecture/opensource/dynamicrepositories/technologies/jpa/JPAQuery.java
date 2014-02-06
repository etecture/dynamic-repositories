package de.etecture.opensource.dynamicrepositories.technologies.jpa;

import de.etecture.opensource.dynamicrepositories.executor.AbstractQuery;

/**
 *
 * @param <R>
 * @author rhk
 * @version
 * @since
 */
public class JPAQuery<R> extends AbstractQuery<R> {

    private final String statement;

    public JPAQuery(
            Class<R> resultType, String technology, String connection,
            String statement) {
        super(resultType, technology, connection);
        this.statement = statement;
    }

    @Override
    public String getStatement() {
        return this.statement;
    }
}
