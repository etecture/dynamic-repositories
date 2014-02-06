package de.etecture.opensource.dynamicrepositories.executor;

import de.etecture.opensource.dynamicrepositories.annotation.Hint;
import de.etecture.opensource.dynamicrepositories.annotation.HintValueGenerator;
import de.etecture.opensource.dynamicrepositories.api.ParamName;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public abstract class AbstractQueryBuilder implements QueryBuilder {

    @Inject
    Instance<HintValueGenerator> hintValueGenerators;

    protected abstract <R, Q extends AbstractQuery<R>> Q createQuery(
            Class<R> resultType,
            String technology,
            String connection,
            String statement);

    protected <R> void addParameter(
            AbstractQuery<?> query,
            String paramName,
            Object arg) {
        query.parameters.put(paramName, arg);
    }

    protected <R> void addHint(AbstractQuery<?> query, String hintName,
            Object value) {
        query.hints.put(hintName, value);
    }

    protected String createStatement(Method method, String statement) {
        if (statement == null || statement.trim().isEmpty()) {
            statement = method.getName();
        }
        try {
            return ResourceBundle.getBundle(method.getDeclaringClass()
                    .getName()).getString(statement);
        } catch (MissingResourceException e) {
            return statement;
        }
    }

    @Override
    public <R> Query<R> buildQuery(Method method, Object... args) {
        String technology = "default";
        String connection = "default";
        String statement = "";
        Hint[] hints = new Hint[0];
        if (method.isAnnotationPresent(
                de.etecture.opensource.dynamicrepositories.annotation.Query.class)) {
            de.etecture.opensource.dynamicrepositories.annotation.Query annotation =
                    method.getAnnotation(
                    de.etecture.opensource.dynamicrepositories.annotation.Query.class);
            technology = annotation.technology();
            connection = annotation.connection();
            statement = annotation.statement();
            hints = annotation.hint();
        }

        AbstractQuery<R> query = createQuery((Class<R>) method.getReturnType(),
                technology, connection, createStatement(method, statement));
        outer:
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            for (Annotation annotation : method.getParameterAnnotations()[i]) {
                if (ParamName.class.isInstance(annotation)) {
                    addParameter(query, ((ParamName) annotation).value(),
                            args[i]);
                    continue outer;
                }
            }
        }
        for (Hint hint : hints) {
            addHint(query, hint.name(), hintValueGenerators.select(hint
                    .generator()).get().generate(hint));
        }
        return query;
    }
}
