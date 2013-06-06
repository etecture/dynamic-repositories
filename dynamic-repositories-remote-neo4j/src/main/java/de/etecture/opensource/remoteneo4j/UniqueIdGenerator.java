package de.etecture.opensource.remoteneo4j;

import de.etecture.opensource.dynamicrepositories.api.Generator;
import de.etecture.opensource.dynamicrepositories.api.Param;
import java.util.UUID;

/**
 * a basic implementation of the {@link Generator} interface to generate unique
 * ids
 *
 * @author rhk
 */
public class UniqueIdGenerator implements Generator {

    @Override
    public Object generateValue(Param definition) {
        return UUID.randomUUID().toString();
    }
}
