package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.executor.Query;
import de.etecture.opensource.dynamicrepositories.executor.QueryHints;
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
class DefaultQuery<R> implements Query<R> {

    private final Class<R> resultType;
    private final Type genericResultType;
    private final String technology;
    private final String connection;
    private final String converter;
    private final String statement;
    private final Map<String, Object> parameters = new HashMap<>();
    private final Map<String, Object> hints = new HashMap<>();

    DefaultQuery(Type genericResultType, String technology,
            String connection,
            String statement,
            String converter) {
        this.genericResultType = genericResultType;

        if (genericResultType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericResultType;
            if (Collection.class.isAssignableFrom((Class) pt.getRawType())) {
                this.resultType = (Class<R>) pt.getActualTypeArguments()[0];
                this.hints.put(QueryHints.LIMIT, -1);
            } else {
                this.resultType = (Class<R>) pt.getRawType();
                this.hints.put(QueryHints.LIMIT, 1);
            }
        } else if (Collection.class.isAssignableFrom((Class) genericResultType)) {
            this.resultType = (Class<R>) Object.class;
            this.hints.put(QueryHints.LIMIT, -1);
        } else {
            this.resultType = (Class<R>) genericResultType;
            this.hints.put(QueryHints.LIMIT, 1);
        }

        this.technology = technology;
        this.connection = connection;
        this.statement = statement;
        this.converter = converter;
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
    public String getConverter() {
        return this.converter;
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

    void addParameter(String name, Object value) {
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

    void addHint(String name, Object value) {
        this.hints.put(name, value);
    }

    @Override
    public String getStatement() {
        return this.statement;
    }

    @Override
    public Class<R> getResultType() {
        return resultType;
    }

    @Override
    public Type getGenericResultType() {
        return genericResultType;
    }

    @Override
    public String toString() {
        try (
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw)) {
            pw.printf("technology: %s%n", getTechnology());
            pw.printf("connection: %s%n", getConnection());
            pw.printf("statement: %s%n", getStatement());
            pw.printf("converter: %s%n", getConverter());
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
