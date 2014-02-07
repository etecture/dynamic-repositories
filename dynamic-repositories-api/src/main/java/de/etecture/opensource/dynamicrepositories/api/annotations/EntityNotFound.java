package de.etecture.opensource.dynamicrepositories.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * marks an exception to be thrown, when a query does not find a result.
 *
 * @author rhk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntityNotFound {
}
