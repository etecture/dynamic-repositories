package de.etecture.opensource.dynamicrepositories.api.annotations;

import de.etecture.opensource.dynamicrepositories.api.utils.DefaultHintValueGenerator;
import de.etecture.opensource.dynamicrepositories.api.HintValueGenerator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE,
    ElementType.METHOD,
    ElementType.PARAMETER})
public @interface Hint {

    String name();

    String value() default "";

    Class<? extends HintValueGenerator> generator() default DefaultHintValueGenerator.class;
}
