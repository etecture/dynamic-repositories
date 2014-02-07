package de.etecture.opensource.dynamicrepositories.api.utils;

import de.etecture.opensource.dynamicrepositories.api.annotations.Converter;
import javax.enterprise.util.AnnotationLiteral;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@SuppressWarnings("AnnotationAsSuperInterface")
public class ConverterLiteral extends AnnotationLiteral<Converter> implements
        Converter {

    private static final long serialVersionUID = 1L;
    private final Class<?> type;

    public ConverterLiteral(
            Class<?> type) {
        this.type = type;
    }

    @Override
    public Class<?> value() {
        return type;
    }
}
