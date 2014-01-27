package de.etecture.opensource.dynamicrepositories.api;

import org.apache.commons.beanutils.ConvertUtils;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public class DefaultGenerator implements Generator {

    @Override
    public Object generateValue(Param param) {
        final String value = param.value();
        if ("$$$generated$$$".equals(value)) {
            throw new IllegalArgumentException(String.format(
                    "Either generator or value must be specified for parameter defintion '%s'!",
                    param.name()));
        }
        return ConvertUtils.convert(value, param.type());
    }
}
