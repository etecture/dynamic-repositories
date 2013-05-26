/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.etecture.opensource.dynamicrepositories.technologies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author rhk
 */
public class CypherResultProxy implements InvocationHandler {

    private final Map<String, Object> result;

    public CypherResultProxy(Map<String, Object> result) {
        this.result = result;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            return result.toString();
        } else if (method.getName().startsWith("get")) {
            String methodName = method.getName().substring(3);
            methodName = methodName.substring(0, 1).toLowerCase(Locale.getDefault()) + methodName.substring(1);
            if (result.containsKey(methodName)) {
                return result.get(methodName);
            }
        }
        return null;
    }
}
