package de.etecture.opensource.dynamicrepositories.api.defaults;

import de.etecture.opensource.dynamicrepositories.api.ParamValueGenerator;
import de.etecture.opensource.dynamicrepositories.api.annotations.Param;
import org.apache.commons.beanutils.ConvertUtils;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public class DefaultParamValueGenerator implements ParamValueGenerator {

    @Override
    public Object generate(Param param) {
        final String value = param.value();
        if ("$$$generated$$$".equals(value)) {
            throw new IllegalArgumentException(String.format(
                    "Either generator or value must be specified for parameter defintion '%s'!",
                    param.name()));
        }
        return ConvertUtils.convert(value, param.type());
    }
}
