package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.api.DefaultQueryHints;
import de.etecture.opensource.dynamicrepositories.api.HintValueGenerator;
import de.etecture.opensource.dynamicrepositories.api.ParamValueGenerator;
import de.etecture.opensource.dynamicrepositories.api.annotations.Hint;
import de.etecture.opensource.dynamicrepositories.api.annotations.Hints;
import de.etecture.opensource.dynamicrepositories.api.annotations.Limit;
import de.etecture.opensource.dynamicrepositories.api.annotations.Param;
import de.etecture.opensource.dynamicrepositories.api.annotations.ParamName;
import de.etecture.opensource.dynamicrepositories.api.annotations.Params;
import de.etecture.opensource.dynamicrepositories.api.annotations.Queries;
import de.etecture.opensource.dynamicrepositories.api.annotations.Query;
import de.etecture.opensource.dynamicrepositories.api.annotations.Skip;
import de.etecture.opensource.dynamicrepositories.executor.QueryExecutionContext;
import de.etecture.opensource.dynamicrepositories.metadata.AnnotatedQueryDefinition;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@Default
public class DefaultQueryExecutionContextBuilder implements
        QueryExecutionContextBuilder {

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
    public <R> QueryExecutionContext<R> buildQueryExecutionContext(
            final String requestedTechnology, final Method method,
            Object... args) {
        Query qa = null;
        outer:
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(Query.class)) {
                qa = annotation.annotationType().getAnnotation(Query.class);
            }
            if (annotation.annotationType().isAnnotationPresent(Queries.class)) {
                for (Query query : annotation.annotationType().getAnnotation(
                        Queries.class).value()) {
                    if (requestedTechnology.equals(query.technology())) {
                        qa = query;
                        continue outer;
                    }
                }
            }
        }
        if (method.isAnnotationPresent(Queries.class)) {
            for (Query query : method.getAnnotation(Queries.class).value()) {
                if (requestedTechnology.equals(query.technology())) {
                    qa = query;
                    break;
                }
            }
        }
        if (method.isAnnotationPresent(Query.class)) {
            qa = method.getAnnotation(Query.class);
        }

        DefaultQueryExecutionContext<R> query;
        if (qa != null) {
            query = new DefaultQueryExecutionContext(
                    method.getReturnType(),
                    method.getGenericReturnType(),
                    new AnnotatedQueryDefinition(qa) {
                @Override
                public String getStatement() {
                    return createStatement(method, super.getStatement());
                }
            });
        } else {
            throw new IllegalArgumentException("the method " + method
                    + " is not a repository method. Must at least be annotated with @Query");
        }
        outer:
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            for (Annotation annotation : method.getParameterAnnotations()[i]) {
                if (ParamName.class.isInstance(annotation)) {
                    query.addParameter(((ParamName) annotation).value(),
                            args[i]);
                    continue outer;
                }
                if (Limit.class.isInstance(annotation)) {
                    int limit = ((Limit) annotation).value();
                    if (args[i] != null) {
                        if (Number.class.isAssignableFrom(method
                                .getParameterTypes()[i])) {
                            limit = ((Number) args[i]).intValue();
                        } else if (method.getParameterTypes()[i].isPrimitive()) {
                            limit = (int) args[i];
                        }
                    }
                    query.addHint(DefaultQueryHints.LIMIT, limit);
                }
                if (Skip.class.isInstance(annotation)) {
                    int skip = 0;
                    if (args[i] != null) {
                        if (Number.class.isAssignableFrom(method
                                .getParameterTypes()[i])) {
                            skip = ((Number) args[i]).intValue();
                        } else if (method.getParameterTypes()[i].isPrimitive()) {
                            skip = (int) args[i];
                        }
                    }
                    query.addHint(DefaultQueryHints.SKIP, skip);
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
            if (Limit.class.isInstance(annotation)) {
                query.addHint(DefaultQueryHints.LIMIT, ((Limit) annotation)
                        .value());
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
            if (annotation.annotationType().isAnnotationPresent(Query.class)) {
                qa = annotation.annotationType().getAnnotation(Query.class);
                addHints(query, qa.hints());
                addParams(query, qa.params());
            }
        }
        return query;
    }

    private void addHints(DefaultQueryExecutionContext query, Hint... hints) {
        for (Hint hint : hints) {
            if (hintValueGenerators == null) {
                query.addHint(hint.name(), hint.value());
            } else {
                query.addHint(hint.name(), hintValueGenerators.select(hint
                        .generator()).get().generate(hint));
            }
        }
    }

    private void addParams(DefaultQueryExecutionContext query, Param... params) {
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
