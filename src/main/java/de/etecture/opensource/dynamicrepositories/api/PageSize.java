package de.etecture.opensource.dynamicrepositories.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * if applied to a method, then this annotation marks the query to be paged with
 * the given default size. When applied to a parameter, then this parameter is
 * used as the page size.
 *
 * @author rhk
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PageSize {

    int value() default -1;
}
