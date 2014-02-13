package de.etecture.opensource.dynamicrepositories.metadata;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public abstract class AbstractQueryDefinition implements QueryDefinition {
    protected final Set<QueryHintDefinition> hints = new HashSet<>();

    @Override
    public Set<QueryHintDefinition> getHints() {
        return Collections.unmodifiableSet(hints);
    }

}
