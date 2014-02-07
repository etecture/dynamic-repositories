package de.etecture.opensource.dynamicrepositories.executor;

import de.etecture.opensource.dynamicrepositories.api.HintValueGenerator;
import de.etecture.opensource.dynamicrepositories.api.ParamValueGenerator;
import de.etecture.opensource.dynamicrepositories.api.annotations.Hint;
import de.etecture.opensource.dynamicrepositories.api.annotations.Hints;
import de.etecture.opensource.dynamicrepositories.api.annotations.Param;
import de.etecture.opensource.dynamicrepositories.api.annotations.ParamName;
import de.etecture.opensource.dynamicrepositories.api.annotations.Params;
import de.etecture.opensource.dynamicrepositories.api.annotations.Queries;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@Default
public class DefaultQueryBuilder implements QueryBuilder {

    @Inject
    Instance<HintValueGenerator> hintValueGenerators;
    @Inject
    Instance<ParamValueGenerator> paramValueGenerators;
    @Inject
    Instance<Object> instances;

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
    public boolean isRepositoryMethod(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(
                    de.etecture.opensource.dynamicrepositories.api.annotations.Query.class)) {
                return true;
            }
            if (annotation.annotationType().isAnnotationPresent(Queries.class)) {
                return true;
            }
            if (de.etecture.opensource.dynamicrepositories.api.annotations.Query.class
                    .isInstance(annotation)) {
                return true;
            }
            if (de.etecture.opensource.dynamicrepositories.api.annotations.Query.class
                    .isInstance(annotation)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <R> Query<R> buildQuery(Method method, Object... args) {
        String technology = "default";
        String connection = "default";
        String statement = "";
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(
                    de.etecture.opensource.dynamicrepositories.api.annotations.Query.class)) {
                de.etecture.opensource.dynamicrepositories.api.annotations.Query qa =
                        annotation.annotationType().getAnnotation(
                        de.etecture.opensource.dynamicrepositories.api.annotations.Query.class);
                technology = StringUtils.defaultIfBlank(qa.technology(),
                        technology);
                connection = StringUtils.defaultIfBlank(qa.connection(),
                        connection);
                statement = StringUtils
                        .defaultIfBlank(qa.statement(), statement);
            }
        }
        if (method.isAnnotationPresent(
                de.etecture.opensource.dynamicrepositories.api.annotations.Query.class)) {
            de.etecture.opensource.dynamicrepositories.api.annotations.Query qa =
                    method.getAnnotation(
                    de.etecture.opensource.dynamicrepositories.api.annotations.Query.class);
            technology = StringUtils.defaultIfBlank(qa.technology(), technology);
            connection = StringUtils.defaultIfBlank(qa.connection(), connection);
            statement = StringUtils.defaultIfBlank(qa.statement(), statement);
        }

        DefaultQuery<R> query = new DefaultQuery(method.getGenericReturnType(),
                technology, connection,
                createStatement(method, statement));
        outer:
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            for (Annotation annotation : method.getParameterAnnotations()[i]) {
                if (ParamName.class.isInstance(annotation)) {
                    query.addParameter(((ParamName) annotation).value(),
                            args[i]);
                    continue outer;
                }
            }
        }
        for (Annotation annotation : method.getAnnotations()) {
            if (de.etecture.opensource.dynamicrepositories.api.annotations.Query.class
                    .isInstance(annotation)) {
                addHints(query,
                        ((de.etecture.opensource.dynamicrepositories.api.annotations.Query) annotation)
                        .hints());
                addParams(query,
                        ((de.etecture.opensource.dynamicrepositories.api.annotations.Query) annotation)
                        .params());
            }
            if (Hint.class.isInstance(annotation)) {
                addHints(query, (Hint) annotation);
            }
            if (Param.class.isInstance(annotation)) {
                addParams(query, (Param) annotation);
            }
            if (Hints.class.isInstance(annotation)) {
                addHints(query, ((Hints) annotation).value());
            }
            if (Params.class.isInstance(annotation)) {
                addParams(query, ((Params) annotation).value());
            }
            if (annotation.annotationType().isAnnotationPresent(Hint.class)) {
                addHints(query, annotation.annotationType()
                        .getAnnotation(Hint.class));
            }
            if (annotation.annotationType().isAnnotationPresent(Hints.class)) {
                addHints(query, annotation.annotationType()
                        .getAnnotation(Hints.class).value());
            }
            if (annotation.annotationType().isAnnotationPresent(Param.class)) {
                addParams(query, annotation.annotationType()
                        .getAnnotation(Param.class));
            }
            if (annotation.annotationType().isAnnotationPresent(Params.class)) {
                addParams(query, annotation.annotationType()
                        .getAnnotation(Params.class).value());
            }
            if (annotation.annotationType().isAnnotationPresent(
                    de.etecture.opensource.dynamicrepositories.api.annotations.Query.class)) {
                de.etecture.opensource.dynamicrepositories.api.annotations.Query qa =
                        annotation.annotationType().getAnnotation(
                        de.etecture.opensource.dynamicrepositories.api.annotations.Query.class);
                addHints(query, qa.hints());
                addParams(query, qa.params());
            }
        }
        return query;
    }

    private void addHints(DefaultQuery query, Hint... hints) {
        for (Hint hint : hints) {
            if (hintValueGenerators == null) {
                query.addHint(hint.name(), hint.value());
            } else {
                query.addHint(hint.name(), hintValueGenerators.select(hint
                        .generator()).get().generate(hint));
            }
        }
    }

    private void addParams(DefaultQuery query, Param... params) {
        for (Param param : params) {
            if (paramValueGenerators == null) {
                query.addParameter(param.name(), param.value());
            } else {
                query.addParameter(param.name(), paramValueGenerators
                        .select(param.generator()).get().generate(param));
            }
        }
    }

}
