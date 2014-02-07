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
package de.etecture.opensource.dynamicrepositories.spi;

import de.etecture.opensource.dynamicrepositories.api.ResultConverter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 *
 * @param <T>
 * @author rhk
 */
public interface QueryMetaData<T> {

    public enum Type {

        SINGLE,
        LIST
    }

    public enum Kind {

        CREATE(),
        RETRIEVE(),
        UPDATE(),
        DELETE();
        private final Class<? extends Annotation> annotation;

        private Kind() {
            this.annotation = null;
        }

        public static Kind valueOf(Method method) {
            for (Kind kind : Kind.values()) {
                if (method.isAnnotationPresent(kind.annotation)) {
                    return kind;
                }
            }
            return null;
        }
    }

    Type getType();

    Set<String> getParameterNames();

    Map<String, Object> getParameterMap();

    Object getParameterValue(String parameterName);

    int getOffset();

    int getCount();

    String getQueryName();

    String getQuery();

    Class<T> getQueryType();

    Kind getQueryKind();

    Exception createException(Class<? extends Annotation> qualifier,
            String message, Exception cause);

    ResultConverter getConverter();

    Annotation[] getAnnotations();

    Class<?> getRepositoryClass();

    String getQueryTechnology();

    String getConnection();
}
