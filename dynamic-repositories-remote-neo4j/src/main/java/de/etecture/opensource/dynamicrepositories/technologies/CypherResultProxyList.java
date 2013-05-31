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
import java.lang.reflect.Proxy;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author rhk
 */
public class CypherResultProxyList<T> extends AbstractList<T> {

    private final Class<T> clazz;
    private final String propertyName;
    private final Map<String, Object> result = new HashMap<>();
    private Integer size = null;
    private Map<Integer, T> values = new HashMap<>();

    public CypherResultProxyList(Class<T> clazz, String propertyName, Map<String, Object> result) {
        this.clazz = clazz;
        this.propertyName = propertyName;
        this.result.putAll(result);
        this.size = calculateSize();
    }

    @Override
    public T get(int index) {
        if (values.containsKey(index)) {
            return values.get(index);
        } else {
            if (result.containsKey(propertyName)) {
                return (T) ResultHelper.extractValue(result.get(propertyName), index);
            } else if (clazz.isInterface()) {
                Map<String, Object> nestedResult = new HashMap<>();
                for (Entry<String, Object> entry : result.entrySet()) {
                    if (entry.getKey().startsWith(propertyName + ".")) {
                        nestedResult.put(entry.getKey().substring(propertyName.length() + 1), ResultHelper.extractValue(entry.getValue(), index));
                    }
                }
                T nestedProxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new CypherResultProxy("", nestedResult));
                values.put(index, nestedProxy);
                return nestedProxy;
            } else {
                throw new IllegalStateException("cannot build nested object for: " + clazz.getSimpleName());
            }
        }
    }

    @Override
    public int size() {
        if (size == null) {
            this.size = Integer.valueOf(calculateSize());
        }
        return size.intValue();
    }

    private int calculateSize() {
        int size = 0;
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            if (entry.getKey().startsWith(propertyName)) {
                if (entry.getValue() == null) {
                    size = Math.max(size, 0);
                } else if (List.class.isAssignableFrom(entry.getValue().getClass())) {
                    size = Math.max(size, ((List) entry.getValue()).size());
                } else if (entry.getValue().getClass().isArray()) {
                    size = Math.max(size, ((Object[]) entry.getValue()).length);
                } else {
                    size = Math.max(size, 1);
                }
            }
        }
        return size;
    }

}
