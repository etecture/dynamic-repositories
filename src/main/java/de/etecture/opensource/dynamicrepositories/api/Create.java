package de.etecture.opensource.dynamicrepositories.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * marks a method as a create method.
 *
 * @author rhk
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Create {

    /**
     * @return wether or not the create method must delegate to a corresponding
     * constructor of the entity
     */
    boolean useConstructor() default false;
}
