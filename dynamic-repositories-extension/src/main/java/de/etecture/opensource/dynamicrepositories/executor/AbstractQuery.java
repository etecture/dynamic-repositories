package de.etecture.opensource.dynamicrepositories.executor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * basic implementation of a query.
 *
 * @param <R>
 * @author rhk
 * @version
 * @since
 */
public abstract class AbstractQuery<R> implements Query<R> {

    private final Class<R> resultType;
    private final String technology, connection;
    protected final Map<String, Object> parameters = new HashMap<>();
    protected final Map<String, Object> hints = new HashMap<>();

    public AbstractQuery(
            Class<R> resultType, String technology, String connection) {
        this.resultType = resultType;
        this.technology = technology;
        this.connection = connection;
    }

    @Override
    public String getTechnology() {
        return this.technology;
    }

    @Override
    public String getConnection() {
        return this.connection;
    }

    @Override
    public Set<String> getParameterNames() {
        return Collections.unmodifiableSet(this.parameters.keySet());
    }

    @Override
    public Object getParameterValue(String name) {
        return parameters.get(name);
    }

    @Override
    public boolean hasParameter(String name) {
        return parameters.containsKey(name);
    }

    @Override
    public Set<String> getQueryHints() {
        return Collections.unmodifiableSet(this.hints.keySet());
    }

    @Override
    public Object getQueryHintValue(String name) {
        return hints.get(name);
    }

    @Override
    public boolean hasHint(String name) {
        return hints.containsKey(name);
    }

    @Override
    public Class<R> getResultType() {
        return resultType;
    }
}
