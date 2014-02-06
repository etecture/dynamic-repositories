package de.etecture.opensource.dynamicrepositories.annotation;

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
