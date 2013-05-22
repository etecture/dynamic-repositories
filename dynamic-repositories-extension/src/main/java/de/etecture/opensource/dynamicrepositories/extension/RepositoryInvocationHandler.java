/*
 * This file is part of the ETECTURE Open Source Community Projects.
 *
 * Copyright (c) 2013 by:
 *
 * ETECTURE GmbH
 * Darmstädter Landstraße 112
 * 60598 Frankfurt
 * Germany
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the author nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.api.Create;
import de.etecture.opensource.dynamicrepositories.api.Delete;
import de.etecture.opensource.dynamicrepositories.api.DeleteSupport;
import de.etecture.opensource.dynamicrepositories.api.EntityAlreadyExistsException;
import de.etecture.opensource.dynamicrepositories.api.EntityNotFoundException;
import de.etecture.opensource.dynamicrepositories.api.FieldName;
import de.etecture.opensource.dynamicrepositories.api.PageIndex;
import de.etecture.opensource.dynamicrepositories.api.PageSize;
import de.etecture.opensource.dynamicrepositories.api.ParamName;
import de.etecture.opensource.dynamicrepositories.api.Queries;
import de.etecture.opensource.dynamicrepositories.api.Query;
import de.etecture.opensource.dynamicrepositories.api.QueryName;
import de.etecture.opensource.dynamicrepositories.api.Repository;
import de.etecture.opensource.dynamicrepositories.api.ResultConverter;
import de.etecture.opensource.dynamicrepositories.api.Retrieve;
import de.etecture.opensource.dynamicrepositories.api.Update;
import de.etecture.opensource.dynamicrepositories.api.UpdateSupport;
import de.etecture.opensource.dynamicrepositories.spi.QueryExecutor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

/**
 * this is an {@link InvocationHandler} for finder methods in an interface,
 * tagged with the annotation {@link Repository}
 *
 * @author rhk
 */
public class RepositoryInvocationHandler implements InvocationHandler {

    @SuppressWarnings("AnnotationAsSuperInterface")
    private static class QueryDelegate extends AnnotationLiteral<Query> implements Query {

        static final long serialVersionUID = 0;
        private final Method method;
        private final String repositoryTechnology;

        public QueryDelegate(Method method, String repositoryTechnology) {
            this.method = method;
            this.repositoryTechnology = repositoryTechnology;
        }

        @Override
        public String technology() {
            return repositoryTechnology;
        }

        @Override
        public String value() {
            return getQuery().value();
        }

        private Query getQuery() {
            if (method.isAnnotationPresent(Query.class)) {
                return method.getAnnotation(Query.class);
            } else if (method.isAnnotationPresent(Queries.class)) {
                for (Query query : method.getAnnotation(Queries.class).value()) {
                    if (query.technology().equalsIgnoreCase(repositoryTechnology)) {
                        return query;
                    }
                }
                // not found, so return "default" query, if existing.
                for (Query query : method.getAnnotation(Queries.class).value()) {
                    if (query.technology().equalsIgnoreCase("default")) {
                        return query;
                    }
                }
            }
            throw new IllegalStateException(String.format("Cannot find Query for technology: %s in method: %s", repositoryTechnology, method.getName()));
        }

        @Override
        public Class<? extends ResultConverter> converter() {
            return getQuery().converter();
        }
    }
    private final BeanManager beanManager;
    private final String technology;
    private final CreationalContext ctx;

    public RepositoryInvocationHandler(String technology, BeanManager beanManager, CreationalContext ctx) {
        this.beanManager = beanManager;
        this.ctx = ctx;
        this.technology = technology;
    }

    private QueryExecutor getExecutorByTechnology(String technology) {
        Set<Bean<?>> queryExecutors = beanManager.getBeans(QueryExecutor.class, new TechnologyLiteral(technology));
        QueryExecutor qe = (QueryExecutor) this.beanManager.getReference(beanManager.resolve(queryExecutors), QueryExecutor.class, ctx);
        return qe;
    }

    private int getPageSize(Method method, Object[] values) {
        if (method.isAnnotationPresent(PageSize.class)) {
            return method.getAnnotation(PageSize.class).value();
        }
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (PageSize.class.isAssignableFrom(annotation.annotationType())) {
                    return (int) values[i];
                }
            }
        }
        return -1;
    }

    private int getPageIndex(Method method, Object[] values) {
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (PageIndex.class.isAssignableFrom(annotation.annotationType())) {
                    return (int) values[i];
                }
            }
        }
        return 0;
    }

    private Map<String, Object> buildParameterMap(Method method, Object[] values) {
        Map<String, Object> result = new HashMap<>();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (ParamName.class.isAssignableFrom(annotation.annotationType())) {
                    result.put(((ParamName) annotation).value(), values[i]);
                }
            }
        }
        return result;
    }

    private Map<String, Object> buildFieldValueMap(Method method, Object[] values) {
        Map<String, Object> result = new HashMap<>();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (FieldName.class.isAssignableFrom(annotation.annotationType())) {
                    result.put(((FieldName) annotation).value(), values[i]);
                }
            }
        }
        return result;
    }

    private <T extends Exception> T createException(Class<T> type, String message, Throwable cause) throws Exception {
        try {
            // find Constructor with String and Throwable parameter
            return type.getConstructor(String.class, Throwable.class).newInstance(message, cause);
        } catch (NoSuchMethodException ex) {
            try {
                // find Constructor with Throwable parameter
                return type.getConstructor(Throwable.class).newInstance(cause);
            } catch (NoSuchMethodException ex2) {
                T exception;
                try {
                    // find Constructor with String parameter
                    exception = type.getConstructor(String.class).newInstance(message);
                } catch (NoSuchMethodException ex3) {
                    // find no-args Constructor
                    exception = type.newInstance();
                }
                if (cause != null) {
                    exception.initCause(cause);
                }
                return exception;
            }
        }
    }

    private <M> M convert(Class<M> returnType, Class<?> queryResultType, Object queryResult) throws Exception {
        for (Bean bean : beanManager.getBeans(ResultConverter.class)) {
            ResultConverter converter = (ResultConverter) bean.create(beanManager.createCreationalContext(null));
            if (converter.isResponsibleFor(queryResultType, returnType)) {
                return returnType.cast(converter.convert(queryResult));
            }
        }
        return returnType.cast(queryResult);
    }

    private Object invokeAsFinder(Method method, Object[] args) throws Exception {
        QueryExecutor qe = getExecutorByTechnology(this.technology);
        Map<String, Object> parameterMap = buildParameterMap(method, args);

        int pageSize = getPageSize(method, args);
        if (pageSize == 0) {
            return Collections.emptyList();
        }

        try {
            Collection<?> result;

            if (method.isAnnotationPresent(QueryName.class)) {
                QueryName queryName = method.getAnnotation(QueryName.class);
                if (pageSize > 0) {
                    int pageIndex = getPageIndex(method, args);
                    result = qe.retrieve(queryName.value(), null, parameterMap, pageIndex, pageSize);
                } else {
                    result = qe.retrieve(queryName.value(), null, parameterMap);
                }
                if (queryName.converter() != ResultConverter.class) {
                    return method.getReturnType().cast(queryName.converter().newInstance().convert(result));
                }
            } else if (method.isAnnotationPresent(Queries.class) || method.isAnnotationPresent(Query.class)) {
                QueryDelegate query = new QueryDelegate(method, this.technology);
                if (pageSize > 0) {
                    int pageIndex = getPageIndex(method, args);
                    result = qe.retrieve(query, null, parameterMap, pageIndex, pageSize);
                } else {
                    result = qe.retrieve(query, null, parameterMap);
                }
                if (query.converter() != ResultConverter.class) {
                    return method.getReturnType().cast(query.converter().newInstance().convert(result));
                }
            } else {
                String queryName = method.getName();
                if (pageSize > 0) {
                    int pageIndex = getPageIndex(method, args);
                    result = qe.retrieve(queryName, null, parameterMap, pageIndex, pageSize);
                } else {
                    result = qe.retrieve(queryName, null, parameterMap);
                }
            }

            if (method.getReturnType().isAssignableFrom(result.getClass())) {
                return method.getReturnType().cast(result);
            } else {
                Object converted = convert(method.getReturnType(), Collection.class, result);
                if (converted == null) {
                    throw createException(method.getAnnotation(Retrieve.class).notFoundException(), "cannot found result", null);
                }
                return converted;
            }
        } catch (EntityNotFoundException ex) {
            throw createException(method.getAnnotation(Retrieve.class).notFoundException(), ex.getMessage(), ex);
        }
    }

    private Object invokeAsDelete(Method method, Object[] args) throws Exception {
        QueryExecutor qe = getExecutorByTechnology(this.technology);
        Map<String, Object> parameterMap = buildParameterMap(method, args);

        try {
            if (method.isAnnotationPresent(QueryName.class)) {
                String queryName = method.getAnnotation(QueryName.class).value();
                return qe.delete(queryName, null, parameterMap);
            } else if (method.isAnnotationPresent(Queries.class) || method.isAnnotationPresent(Query.class)) {
                QueryDelegate query = new QueryDelegate(method, this.technology);
                return qe.delete(query, null, parameterMap);
            } else {
                return qe.delete(method.getName(), null, parameterMap);
            }
        } catch (EntityNotFoundException ex) {
            throw createException(method.getAnnotation(Delete.class).notFoundException(), ex.getMessage(), ex);
        }
    }

    private Object invokeAsUpdate(Method method, Object[] args) throws Exception {
        QueryExecutor qe = getExecutorByTechnology(this.technology);
        Map<String, Object> parameterMap = buildParameterMap(method, args);

        try {
            if (method.isAnnotationPresent(QueryName.class)) {
                String queryName = method.getAnnotation(QueryName.class).value();
                return qe.update(queryName, null, parameterMap);
            } else if (method.isAnnotationPresent(Queries.class) || method.isAnnotationPresent(Query.class)) {
                QueryDelegate query = new QueryDelegate(method, this.technology);
                return qe.update(query, null, parameterMap);
            } else {
                return qe.update(method.getName(), null, parameterMap);
            }
        } catch (EntityNotFoundException ex) {
            throw createException(method.getAnnotation(Update.class).notFoundException(), ex.getMessage(), ex);
        }
    }

    private Object invokeAsCreateMethod(Method method, Object[] args) throws Exception {
        QueryExecutor qe = getExecutorByTechnology(this.technology);
        Create create = method.getAnnotation(Create.class);
        assert create != null : "create annotation must be set on method for invokeAsCreateMethod!";
        try {
            Object result;
            if (method.isAnnotationPresent(QueryName.class)) {
                QueryName queryName = method.getAnnotation(QueryName.class);
                if (create.useConstructor()) {
                    result = qe.create(queryName.value(), method.getReturnType(), method.getParameterTypes(), args);
                } else {
                    Map<String, Object> fieldValues = buildFieldValueMap(method, args);
                    result = qe.create(queryName.value(), method.getReturnType(), fieldValues);
                }
                if (method.getReturnType().isAssignableFrom(result.getClass())) {
                    return method.getReturnType().cast(result);
                } else if (queryName.converter() != ResultConverter.class) {
                    return method.getReturnType().cast(queryName.converter().newInstance().convert(result));
                }
            } else if (method.isAnnotationPresent(Queries.class) || method.isAnnotationPresent(Query.class)) {
                QueryDelegate query = new QueryDelegate(method, this.technology);
                if (create.useConstructor()) {
                    result = qe.create(query, method.getReturnType(), method.getParameterTypes(), args);
                } else {
                    Map<String, Object> fieldValues = buildFieldValueMap(method, args);
                    result = qe.create(query, method.getReturnType(), fieldValues);
                }
                if (query.converter() != ResultConverter.class) {
                    return method.getReturnType().cast(query.converter().newInstance().convert(result));
                }
            } else {
                String queryName = method.getName();
                if (create.useConstructor()) {
                    result = qe.create(queryName, method.getReturnType(), method.getParameterTypes(), args);
                } else {
                    Map<String, Object> fieldValues = buildFieldValueMap(method, args);
                    result = qe.create(queryName, method.getReturnType(), fieldValues);
                }
            }

            if (method.getReturnType().isAssignableFrom(result.getClass())) {
                return method.getReturnType().cast(result);
            } else {
                Object converted = convert(method.getReturnType(), result.getClass(), result);
                if (converted == null) {
                    throw createException(create.alreadyExistException(), "cannot create entity", null);
                }
                return converted;
            }
        } catch (EntityAlreadyExistsException ex) {
            throw createException(create.alreadyExistException(), ex.getMessage(), ex);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isAnnotationPresent(Create.class)) {
            return invokeAsCreateMethod(method, args);
        } else if (method.isAnnotationPresent(Retrieve.class)) {
            return invokeAsFinder(method, args);
        } else if (method.isAnnotationPresent(Update.class)) {
            return invokeAsUpdate(method, args);
        } else if (method.isAnnotationPresent(Delete.class)) {
            return invokeAsDelete(method, args);
        } else if (method.getDeclaringClass() == UpdateSupport.class
                || method.getDeclaringClass() == DeleteSupport.class) {
            return method.invoke(getExecutorByTechnology(this.technology), args);
        } else {
            throw new UnsupportedOperationException("no implementation for method: " + method.getName());
        }
    }
}
