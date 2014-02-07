package de.etecture.opensource.dynamicrepositories.api.utils;

import de.etecture.opensource.dynamicrepositories.api.ResultConverter;
import java.util.AbstractList;
import java.util.List;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@Default
public class Converters {

    @Inject
    @Any
    Instance<ResultConverter> converters;

    public <R> R convert(Object value, Class<R> type) {
        return type.cast(converters.select(new ConverterLiteral(type)).get()
                .convert(value, type));
    }

    public <R> List<R> convertList(final List<?> value, final Class<R> type) {
        return new AbstractList<R>() {
            @Override
            public R get(int index) {
                return Converters.this.convert(value.get(index), type);
            }

            @Override
            public int size() {
                return value.size();
            }
        };
    }
}
