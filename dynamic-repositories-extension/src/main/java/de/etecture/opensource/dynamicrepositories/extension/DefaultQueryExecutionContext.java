package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.api.DefaultQueryHints;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionContext;
import de.etecture.opensource.dynamicrepositories.metadata.QueryDefinition;
import de.etecture.opensource.dynamicrepositories.metadata.QueryHintDefinition;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
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
public class DefaultQueryExecutionContext<R> implements QueryExecutionContext<R> {

    private final QueryDefinition query;
    private final Class<R> resultType;
    private final Type genericResultType;
    private final Map<String, Object> parameters = new HashMap<>();
    private final Map<String, Object> hints = new HashMap<>();

    public DefaultQueryExecutionContext(Class<R> resultType,
            Type genericResultType, QueryDefinition query) {
        this.query = query;
        for (QueryHintDefinition hint : this.query.getHints()) {
            this.hints.put(hint.getName(), hint.getValue());
        }
        this.genericResultType = genericResultType;
        if (genericResultType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericResultType;
            if (Collection.class.isAssignableFrom((Class) pt.getRawType())) {
                this.resultType = (Class<R>) pt.getActualTypeArguments()[0];
                this.hints.put(DefaultQueryHints.LIMIT, -1);
            } else {
                this.resultType = (Class<R>) pt.getRawType();
                this.hints
                        .put(DefaultQueryHints.LIMIT, 1);
            }
        } else if (Collection.class.isAssignableFrom((Class) genericResultType)) {
            this.resultType = (Class<R>) Object.class;
            this.hints.put(DefaultQueryHints.LIMIT, -1);
        } else {
            this.resultType = (Class<R>) genericResultType;
            this.hints.put(DefaultQueryHints.LIMIT, 1);
        }
    }

    @Override
    public Type getGenericResultType() {
        return genericResultType;
    }

    @Override
    public Class<R> getResultType() {
        return resultType;
    }

    @Override
    public QueryDefinition getQuery() {
        return this.query;
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
    public Object getParameterValue(String name, Object defaultValue) {
        Object value = getParameterValue(name);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    @Override
    public boolean hasParameter(String name) {
        return parameters.containsKey(name);
    }

    @Override
    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public void addParameter(String name, Object value) {
        this.parameters.put(name, value);
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
    public Object getQueryHintValue(String name, Object defaultValue) {
        Object value = getQueryHintValue(name);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    @Override
    public boolean hasQueryHint(String name) {
        return hints.containsKey(name);
    }

    public void addHint(String name, Object value) {
        this.hints.put(name, value);
    }

    @Override
    public String toString() {
        try (
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw)) {
            pw.printf("technology: %s%n", query.getTechnology());
            pw.printf("connection: %s%n", query.getConnection());
            pw.printf("statement: %s%n", query.getStatement());
            pw.printf("converter: %s%n", query.getConverter());
            pw.printf("resultType: %s%n", getGenericResultType());
            pw.println("parameter:");
            for (String paramName : getParameterNames()) {
                pw.printf("\t%s = %s%n", paramName,
                        getParameterValue(paramName));
            }
            pw.println("hints:");
            for (String hintName : getQueryHints()) {
                pw.printf("\t%s = %s%n", hintName, getQueryHintValue(
                        hintName));
            }
            return sw.toString();
        } catch (IOException ex) {
            return super.toString();
        }
    }
}
