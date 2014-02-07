package de.etecture.opensource.dynamicrepositories.api.annotations;

import de.etecture.opensource.dynamicrepositories.api.defaults.DefaultParamValueGenerator;
import de.etecture.opensource.dynamicrepositories.api.ParamValueGenerator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * defines an additional parameter to be used in query.
 *
 * @author rhk
 */
@Target({ElementType.METHOD,
    ElementType.PARAMETER})
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
    Class<? extends ParamValueGenerator> generator() default DefaultParamValueGenerator.class;
}
