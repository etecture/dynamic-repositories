/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.etecture.opensource.dynamicrepositories.technologies;

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
                return result.get(propertyName);
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
