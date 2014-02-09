package de.etecture.opensource.dynamicrepositories.api.utils;

import de.etecture.opensource.dynamicrepositories.api.ParamValueGenerator;
import de.etecture.opensource.dynamicrepositories.api.annotations.Param;
import de.herschke.converters.api.ConvertException;
import de.herschke.converters.api.Converters;
import javax.inject.Inject;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public class DefaultParamValueGenerator implements ParamValueGenerator {

    @Inject
    Converters converters;

    @Override
    public Object generate(Param param) {
        final String value = param.value();
        if ("$$$generated$$$".equals(value)) {
            throw new IllegalArgumentException(String.format(
                    "Either generator or value must be specified for parameter defintion '%s'!",
                    param.name()));
        }
        try {
            return converters.select(param.type()).convert(param.value());
        } catch (ConvertException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
