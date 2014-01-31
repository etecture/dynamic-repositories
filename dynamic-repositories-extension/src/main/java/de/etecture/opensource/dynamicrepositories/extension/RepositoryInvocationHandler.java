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

import de.etecture.opensource.dynamicrepositories.api.Count;
import de.etecture.opensource.dynamicrepositories.api.DeleteSupport;
import de.etecture.opensource.dynamicrepositories.api.Generator;
import de.etecture.opensource.dynamicrepositories.api.Offset;
import de.etecture.opensource.dynamicrepositories.api.Param;
import de.etecture.opensource.dynamicrepositories.api.ParamName;
import de.etecture.opensource.dynamicrepositories.api.Params;
import de.etecture.opensource.dynamicrepositories.api.Queries;
import de.etecture.opensource.dynamicrepositories.api.Query;
import de.etecture.opensource.dynamicrepositories.api.Repository;
import de.etecture.opensource.dynamicrepositories.api.ResultConverter;
import de.etecture.opensource.dynamicrepositories.api.UpdateSupport;
import de.etecture.opensource.dynamicrepositories.spi.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.QueryExecutorResolver;
import de.etecture.opensource.dynamicrepositories.spi.QueryMetaData;
import de.etecture.opensource.dynamicrepositories.spi.QueryMetaData.Kind;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * this is an {@link InvocationHandler} for finder methods in an interface,
 * tagged with the annotation {@link Repository}
 *
 * @author rhk
 */
public class RepositoryInvocationHandler implements InvocationHandler {

    private static class MethodQueryMetaData<T> implements QueryMetaData<T> {

        private final Type type;
        private final Class<T> queryType;
        private final Method method;
        private final Object[] values;
        private final Map<String, Object> parameterMap = new HashMap<>();
        private final Kind kind;
        private final String queryName;
        private final String query;
        private final ResultConverter<T> converter;
        private final Class<?> repositoryClass;
        private final String technology;
        private final String connection;

        private MethodQueryMetaData(Class<?> repositoryClass, String technology,
                Method method, Object[] values) throws Exception {
            this.kind = Kind.valueOf(method);
            if (Collection.class.isAssignableFrom(method.getReturnType())
                    || method.getReturnType().isArray()) {
                this.type = Type.LIST;
                if (method.getReturnType().isArray()) {
                    this.queryType = (Class<T>) method.getReturnType()
                            .getComponentType();
                } else {
                    this.queryType = (Class<T>) ((ParameterizedType) method
                            .getGenericReturnType()).getActualTypeArguments()[0];
                }
            } else {
                this.type = Type.SINGLE;
                this.queryType = (Class<T>) method.getReturnType();
            }
            this.technology = technology;
            this.repositoryClass = repositoryClass;
            this.method = method;
            this.values = values;
            final Annotation[][] parameterAnnotations = method
                    .getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (ParamName.class.isAssignableFrom(annotation
                            .annotationType())) {
                        parameterMap.put(((ParamName) annotation).value(),
                                values[i]);
                    }
                }
            }
            if (method.isAnnotationPresent(Param.class)) {
                Param param = method.getAnnotation(Param.class);
                addParameter(param);
            } else if (method.isAnnotationPresent(Params.class)) {
                for (Param param : method.getAnnotation(Params.class).value()) {
                    addParameter(param);
                }
            }
            if (method.isAnnotationPresent(Query.class)) {
                query = method.getAnnotation(Query.class).value();
                queryName =
                        method.getAnnotation(Query.class).name().isEmpty() ? method
                        .getName() : method.getAnnotation(Query.class).name();
                Class<? extends ResultConverter> converterClass = method
                        .getAnnotation(Query.class).converter();
                if (converterClass.equals(ResultConverter.class)) {
                    converter = null;
                } else {
                    converter = converterClass.newInstance();
                }
                connection = method.getAnnotation(Query.class).connection();
            } else if (method.isAnnotationPresent(Queries.class)) {
                for (Query queryA : method.getAnnotation(Queries.class).value()) {
                    if (queryA.technology().equalsIgnoreCase(technology)) {
                        query = queryA.value();
                        queryName =
                                queryA.name().isEmpty() ? method.getName() : queryA
                                .name();
                        Class<? extends ResultConverter> converterClass = queryA
                                .converter();
                        if (converterClass.equals(ResultConverter.class)) {
                            converter = null;
                        } else {
                            converter = converterClass.newInstance();
                        }
                        connection = queryA.connection();
                        return;
                    }
                }
                // not found, so return "default" query, if existing.
                for (Query queryA : method.getAnnotation(Queries.class).value()) {
                    if (queryA.technology().equalsIgnoreCase("default")) {
                        query = queryA.value();
                        queryName =
                                queryA.name().isEmpty() ? method.getName() : queryA
                                .name();
                        Class<? extends ResultConverter> converterClass = queryA
                                .converter();
                        if (converterClass.equals(ResultConverter.class)) {
                            converter = null;
                        } else {
                            converter = converterClass.newInstance();
                        }
                        connection = queryA.connection();
                        return;
                    }
                }
                query = "";
                queryName = method.getName();
                converter = null;
                connection = "default";
            } else {
                query = "";
                queryName = method.getName();
                converter = null;
                connection = "default";
            }
        }

        @Override
        public Class<?> getRepositoryClass() {
            return this.repositoryClass;
        }

        @Override
        public Annotation[] getAnnotations() {
            return method.getAnnotations();
        }

        @Override
        public Set<String> getParameterNames() {
            return parameterMap.keySet();
        }

        @Override
        public Map<String, Object> getParameterMap() {
            return Collections.unmodifiableMap(parameterMap);
        }

        @Override
        public Object getParameterValue(String parameterName) {
            return parameterMap.get(parameterName);
        }

        @Override
        public int getOffset() {
            final Annotation[][] parameterAnnotations = method
                    .getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (Offset.class.isAssignableFrom(annotation
                            .annotationType())) {
                        return (int) values[i];
                    }
                }
            }
            return 0;
        }

        @Override
        public int getCount() {
            if (method.isAnnotationPresent(Count.class)) {
                return method.getAnnotation(Count.class).value();
            }
            final Annotation[][] parameterAnnotations = method
                    .getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (Count.class
                            .isAssignableFrom(annotation.annotationType())) {
                        return (int) values[i];
                    }
                }
            }
            if (Collection.class.isAssignableFrom(getQueryType())
                    || getQueryType().isArray()) {
                return -1;
            }
            return 1;
        }

        @Override
        public String getQueryName() {
            return queryName;
        }

        @Override
        public String getQuery() {
            return this.query;
        }

        @Override
        public Class<T> getQueryType() {
            return queryType;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Kind getQueryKind() {
            return kind;
        }

        @Override
        public Exception createException(Class<? extends Annotation> qualifier,
                String message, Exception cause) {
            for (Class<?> exceptionType : method.getExceptionTypes()) {
                if (exceptionType.isAnnotationPresent(qualifier)) {
                    try {
                        try {
                            // find Constructor with String and Throwable parameter
                            return (Exception) exceptionType.getConstructor(
                                    String.class, Throwable.class).newInstance(
                                    message, cause);
                        } catch (NoSuchMethodException ex) {
                            try {
                                // find Constructor with Throwable parameter
                                return (Exception) exceptionType.getConstructor(
                                        Throwable.class).newInstance(cause);
                            } catch (NoSuchMethodException ex2) {
                                Exception exception;
                                try {
                                    // find Constructor with String parameter
                                    exception = (Exception) exceptionType
                                            .getConstructor(String.class)
                                            .newInstance(message);
                                } catch (NoSuchMethodException ex3) {
                                    // find no-args Constructor
                                    exception = (Exception) exceptionType
                                            .newInstance();
                                }
                                if (cause != null) {
                                    exception.initCause(cause);
                                }
                                return exception;
                            }
                        }
                    } catch (SecurityException | InstantiationException |
                            IllegalAccessException | IllegalArgumentException |
                            InvocationTargetException ex) {
                        throw new IllegalStateException(
                                "cannot create Exception: ", ex);
                    }
                }
            }
            return cause;
        }

        @Override
        public ResultConverter<T> getConverter() {
            return converter;
        }

        private void addParameter(Param param) {
            try {
                final Generator generator = param.generator().newInstance();
                parameterMap.put(param.name(),
                        generator.generateValue(param));
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new IllegalArgumentException(
                        "The generator cannot be instantiated. ", ex);
            }
        }

        @Override
        public String getQueryTechnology() {
            return technology;
        }

        @Override
        public String getConnection() {
            return connection;
        }
    }
    private final QueryExecutorResolver queryExecutorResolver;
    private final String technology;

    public RepositoryInvocationHandler(String technology,
            QueryExecutorResolver resolver) {
        this.queryExecutorResolver = resolver;
        this.technology = technology;
    }

    private QueryExecutor getExecutorByTechnology(String technology) {
        return this.queryExecutorResolver.getQueryExecutorForTechnology(
                technology);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws
            Throwable {
        Object result = null;
        try {
            if (Kind.valueOf(method) != null) {
                result = getExecutorByTechnology(this.technology).execute(
                        new MethodQueryMetaData<>(method.getDeclaringClass(),
                        this.technology, method, args));
            } else if (method.getDeclaringClass() == UpdateSupport.class
                    || method.getDeclaringClass() == DeleteSupport.class) {
                result = method.invoke(getExecutorByTechnology(this.technology),
                        args);
            } else {
                throw new UnsupportedOperationException(
                        "no implementation for method: " + method.getName());
            }
        } catch (Exception exception) {
            for (int i = 0; i < method.getExceptionTypes().length; i++) {
                if (method.getExceptionTypes()[i].isAssignableFrom(exception
                        .getClass())) {
                    throw exception;
                }
            }
            throw new UnsupportedOperationException(String.format(
                    "The repository method: %s#%s does not declare to throw: %s - but this exception is raised in execution of this method!%nMessage of raised Exception is: %s",
                    method.getDeclaringClass().getSimpleName(), method.getName(),
                    exception.getClass().getSimpleName(), exception.getMessage()),
                    exception);
        }
        return result;
    }
}
