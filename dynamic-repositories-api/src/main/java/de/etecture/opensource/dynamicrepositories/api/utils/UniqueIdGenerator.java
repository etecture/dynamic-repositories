package de.etecture.opensource.dynamicrepositories.api.utils;

import de.etecture.opensource.dynamicrepositories.api.ParamValueGenerator;
import de.etecture.opensource.dynamicrepositories.api.annotations.Param;
import java.util.UUID;

/**
 * a basic implementation of the {@link ParamValueGenerator} interface to
 * generate unique ids.
 *
 * @author rhk
 */
public class UniqueIdGenerator implements ParamValueGenerator {

    @Override
    public Object generate(Param definition) {
        return UUID.randomUUID().toString();
    }
}
