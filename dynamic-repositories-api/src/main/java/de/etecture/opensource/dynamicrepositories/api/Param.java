package de.etecture.opensource.dynamicrepositories.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * defines an additional parameter to be used in query.
 *
 * @author rhk
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {

    /**
     * the name of the parameter to be added
     *
     * @return
     */
    String name();

    /**
     * the value of the parameter to be added
     *
     * @return
     */
    String value() default "$$$generated$$$";

    /**
     * the type of the parameter value
     *
     * @return
     */
    Class type() default String.class;

    /**
     * the generator, that generates this parameter value.
     *
     * @return
     */
    Class<? extends Generator> generator() default Generator.class;
}
