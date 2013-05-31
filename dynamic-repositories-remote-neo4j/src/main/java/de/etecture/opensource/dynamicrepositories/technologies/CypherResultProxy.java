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
package de.etecture.opensource.dynamicrepositories.technologies;

import de.etecture.opensource.dynamicrepositories.technologies.utils.ResultHelper;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author rhk
 */
public class CypherResultProxy implements InvocationHandler {

    private final String prefix;
    private final Map<String, Object> result = new HashMap<>();

    public CypherResultProxy(Map<String, Object> result) {
        this("", result);
    }

    public CypherResultProxy(String prefix, Map<String, Object> result) {
        this.prefix = prefix;
        this.result.putAll(result);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            return result.toString();
        } else if (method.getName().startsWith("get")) {
            String methodName = method.getName().substring(3);
            String propertyName = prefix + methodName.substring(0, 1).toLowerCase(Locale.getDefault()) + methodName.substring(1);
            if (result.containsKey(propertyName)) {
                Object value = result.get(propertyName);
                if (method.getReturnType().isInstance(value)) {
                    return value;
                } else if (!List.class.isAssignableFrom(method.getReturnType())) {
                    return ResultHelper.extractValue(value, 0);
                } else if (value != null) {
                    throw new ClassCastException(String.format("cannot convert: %s to: %s", value.getClass().getSimpleName(), method.getReturnType()));
                }
            } else if (List.class.isAssignableFrom(method.getReturnType())) {
                // get the type of List
                if (method.getGenericReturnType() instanceof ParameterizedType) {
                    ParameterizedType pgrt = (ParameterizedType) method.getGenericReturnType();
                    // convert the list
                    List nestedProxy = new CypherResultProxyList((Class<?>) pgrt.getActualTypeArguments()[0], propertyName, result);
                    result.put(propertyName, nestedProxy);
                    return nestedProxy;
                }
            } else if (method.getReturnType().isInterface()) {
                Object nestedProxy = Proxy.newProxyInstance(method.getReturnType().getClassLoader(), new Class<?>[]{method.getReturnType()}, new CypherResultProxy(propertyName + ".", result));
                result.put(propertyName, nestedProxy);
                return nestedProxy;
            }
        }
        return null;
    }
}
