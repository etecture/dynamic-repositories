package de.etecture.opensource.dynamicrepositories.annotation;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public class DefaultHintValueGenerator implements HintValueGenerator {

    @Override
    public Object generate(Hint hint) {
        return hint.value();
    }

}
