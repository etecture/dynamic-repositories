package de.etecture.opensource.dynamicrepositories.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * is a marker interface to specify this parameter as being the page index
 *
 * @author rhk
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageIndex {
}
