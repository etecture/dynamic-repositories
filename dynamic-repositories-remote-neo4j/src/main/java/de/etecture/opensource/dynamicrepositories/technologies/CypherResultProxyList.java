package de.etecture.opensource.dynamicrepositories.technologies;

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
                return (T) extractValue(result.get(propertyName), index);
            } else if (clazz.isInterface()) {
                Map<String, Object> nestedResult = new HashMap<>();
                for (Entry<String, Object> entry : result.entrySet()) {
                    if (entry.getKey().startsWith(propertyName + ".")) {
                        nestedResult.put(entry.getKey().substring(propertyName.length() + 1), extractValue(entry.getValue(), index));
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

    private Object extractValue(Object value, int index) {
        if (value instanceof List) {
            return ((List) value).get(index);
        } else if (value instanceof Object[]) {
            return ((Object[]) value)[index];
        } else if (index == 0) {
            return value;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }
}
