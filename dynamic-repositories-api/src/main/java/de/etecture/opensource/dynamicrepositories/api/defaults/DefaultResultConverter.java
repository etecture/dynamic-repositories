package de.etecture.opensource.dynamicrepositories.api.defaults;

import de.etecture.opensource.dynamicrepositories.api.ResultConverter;
import de.etecture.opensource.dynamicrepositories.api.annotations.Query;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public class DefaultResultConverter implements ResultConverter {

    @Override
    public Object convert(Query query, Object result) {
        return result;
    }
}
