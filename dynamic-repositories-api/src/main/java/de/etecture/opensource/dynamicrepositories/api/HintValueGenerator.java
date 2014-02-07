package de.etecture.opensource.dynamicrepositories.api;

import de.etecture.opensource.dynamicrepositories.api.annotations.Hint;

/**
 * generates a value for the hint.
 *
 * @author rhk
 * @version
 * @since
 */
public interface HintValueGenerator {

    /**
     * generates the value for the hint.
     *
     * @param hint
     * @return
     */
    Object generate(Hint hint);
}
