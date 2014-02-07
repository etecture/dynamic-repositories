package de.etecture.opensource.dynamicrepositories.technologies.jpa.utils;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@SuppressWarnings("AnnotationAsSuperInterface")
public class NamedLiteral extends AnnotationLiteral<Named> implements Named {

    private static final long serialVersionUID = 1L;
    private final String name;

    public NamedLiteral(String name) {
        this.name = name;
    }

    @Override
    public String value() {
        return name;
    }
}
