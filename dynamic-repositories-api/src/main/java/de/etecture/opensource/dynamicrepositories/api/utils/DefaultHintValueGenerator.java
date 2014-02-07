package de.etecture.opensource.dynamicrepositories.api.utils;

import de.etecture.opensource.dynamicrepositories.api.HintValueGenerator;
import de.etecture.opensource.dynamicrepositories.api.annotations.Hint;

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
