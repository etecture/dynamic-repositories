package de.etecture.opensource.dynamicrepositories.utils;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@SuppressWarnings("AnnotationAsSuperInterface")
public class DefaultLiteral extends AnnotationLiteral<Default> implements
        Default {

    private static final long serialVersionUID = 1L;
}
