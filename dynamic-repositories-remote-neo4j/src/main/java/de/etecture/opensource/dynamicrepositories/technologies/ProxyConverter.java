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
