package de.etecture.opensource.dynamicrepositories.api;

import de.etecture.opensource.dynamicrepositories.spi.Technology;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * defines the query for a given technology
 *
 * @author rhk
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    /**
     * @return the technology to be used to execute this query.
     */
    String technology() default Technology.defaultTechnology;

    /**
     * @return the value of the query.
     */
    String value() default "";
}
