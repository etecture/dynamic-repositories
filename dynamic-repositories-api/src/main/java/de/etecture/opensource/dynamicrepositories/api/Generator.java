package de.etecture.opensource.dynamicrepositories.api;

/**
 * generates parameter values.
 *
 * @author rhk
 */
public interface Generator {

    /**
     * generates a new value for the given parameter definition.
     *
     * @param definition
     * @return
     */
    Object generateValue(Param definition);
}
