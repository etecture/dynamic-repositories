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

import de.etecture.opensource.dynamicrepositories.api.ResultConverter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * converts a neo4j result to a proxy implementation of R
 *
 * @author rhk
 */
public class ProxyConverter<R> implements ResultConverter<R> {

    private <T> List<T> convert(Class<T> clazz, List<Map<String, Object>> toConvert) {
        List<T> result = new ArrayList<>();
        for (Map<String, Object> o : toConvert) {
            result.add(convert(clazz, o));
        }
        return result;
    }

    private <T> T convert(Class<T> clazz, Map<String, Object> toConvert) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new CypherResultProxy(toConvert));
    }

    @Override
    public R convert(Class<R> returnType, Type genericReturnType, Object result) {
        if (result == null) {
            return null;
        }
        if (List.class.isAssignableFrom(returnType)) {
            // get the type of List
            if (genericReturnType instanceof ParameterizedType) {
                ParameterizedType pgrt = (ParameterizedType) genericReturnType;
                Class<?> interfaceType = (Class<?>) pgrt.getActualTypeArguments()[0];
                if (!interfaceType.isInterface()) {
                    throw new IllegalArgumentException("Type for conversion must be an interface!");
                }
                // convert the list
                return (R) convert(interfaceType, (List<Map<String, Object>>) result);
            }
        } else if (returnType.isInterface()) {
            return convert(returnType, (Map<String, Object>) result);
        }
        throw new IllegalArgumentException("Type for conversion must be an interface!");
    }
}
