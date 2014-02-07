package de.etecture.opensource.dynamicrepositories.api;

import de.etecture.opensource.dynamicrepositories.api.annotations.Param;

/**
 * generates parameter values.
 *
 * @author rhk
 */
public interface ParamValueGenerator {

    /**
     * generates a new value for the given parameter definition.
     *
     * @param definition
     * @return
     */
    Object generate(Param definition);
}
