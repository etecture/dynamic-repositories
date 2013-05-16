package de.etecture.opensource.dynamicrepositories.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * defines the queries (with different technologies) for a method
 *
 * @author rhk
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Queries {

    Query[] value();
}
