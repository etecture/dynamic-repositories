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
