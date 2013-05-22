package de.etecture.opensource.dynamicrepositories.extension;

import de.etecture.opensource.dynamicrepositories.api.ResultConverter;
import java.util.Collection;

/**
 * a default {@link ResultConverter} to convert a List query Result to a
 * Singleton Result
 *
 * @author rhk
 */
public class ListToSingletonConverter<T extends Collection<?>, V extends Object> implements ResultConverter<T, V> {

    @Override
    public boolean isResponsibleFor(Class<?> queryResult, Class<?> methodResult) {
        return Collection.class.isAssignableFrom(queryResult) && !(Collection.class.isAssignableFrom(methodResult)) && !(methodResult.isArray());
    }

    @Override
    public V convert(T result) {
        for (Object o : result) {
            return (V) o;
        }
        return null;
    }
}
