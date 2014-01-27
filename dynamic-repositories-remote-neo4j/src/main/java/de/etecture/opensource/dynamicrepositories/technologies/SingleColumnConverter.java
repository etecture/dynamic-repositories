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
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * converts a neo4j result to a proxy implementation of R
 *
 * @author rhk
 */
public class SingleColumnConverter<R> implements ResultConverter<R> {

    private <T> List<T> convert(Class<T> clazz, List<Map<String, Object>> toConvert) {
        List<T> result = new ArrayList<>();
        for (Map<String, Object> o : toConvert) {
            result.add(convert(clazz, o));
        }
        return result;
    }

    private <T> T convert(Class<T> clazz, Map<String, Object> toConvert) {
        final Object value = toConvert.values().iterator().next();
        System.out.println("convert result-value: " + value + " to class: "
                + clazz.getSimpleName());
        if (Number.class.isAssignableFrom(clazz) && value instanceof Number) {
            Number number = (Number) value;
            if (BigDecimal.class.isAssignableFrom(clazz)) {
                return (T) new BigDecimal(number.doubleValue());
            } else if (BigInteger.class.isAssignableFrom(clazz)) {
                return (T) new BigInteger(Long.valueOf(number.longValue()).toString());
            } else if (Byte.class.isAssignableFrom(clazz)) {
                return (T) Byte.valueOf(number.byteValue());
            } else if (Double.class.isAssignableFrom(clazz)) {
                return (T) Double.valueOf(number.doubleValue());
            } else if (Float.class.isAssignableFrom(clazz)) {
                return (T) Float.valueOf(number.floatValue());
            } else if (Integer.class.isAssignableFrom(clazz)) {
                return (T) Integer.valueOf(number.intValue());
            } else if (Long.class.isAssignableFrom(clazz)) {
                return (T) Long.valueOf(number.longValue());
            } else if (Short.class.isAssignableFrom(clazz)) {
                return (T) Short.valueOf(number.shortValue());
            } else {
                throw new IllegalArgumentException(String.format("cannot convert: %s to: %s", value, clazz.getSimpleName()));
            }
        } else if (Boolean.class.isAssignableFrom(clazz) || clazz == Boolean.TYPE) {
            return (T) (value == null ? Boolean.FALSE : (Boolean) value);
        } else if (String.class.isAssignableFrom(clazz)) {
            return (T) (value == null ? null : value.toString());
        } else if (clazz.isPrimitive()) {
            Number number = (Number) value;
            if (clazz == byte.class) {
                return (T) Byte.valueOf(number.byteValue());
            } else if (clazz == double.class) {
                return (T) Double.valueOf(number.doubleValue());
            } else if (clazz == float.class) {
                return (T) Float.valueOf(number.floatValue());
            } else if (clazz == int.class) {
                return (T) Integer.valueOf(number.intValue());
            } else if (clazz == long.class) {
                return (T) Long.valueOf(number.longValue());
            } else if (clazz == short.class) {
                return (T) Short.valueOf(number.shortValue());
            } else {
                throw new IllegalArgumentException(String.format("cannot convert: %s to: %s", value, clazz.getSimpleName()));
            }
        } else {
            throw new IllegalArgumentException(String.format("cannot convert: %s to: %s", value, clazz.getSimpleName()));
        }
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
                return (R) convert(interfaceType, (List<Map<String, Object>>) result);
            }
            throw new IllegalArgumentException("cannot extract type of List.");
        } else {
            return convert(returnType, (Map<String, Object>) result);
        }
    }
}
