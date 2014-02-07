package de.etecture.opensource.dynamicrepositories.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 * marks an exception to be thrown, when a query got a unique exception
 *
 * @author rhk
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,
    ElementType.PARAMETER,
    ElementType.FIELD,
    ElementType.TYPE})
public @interface EntityAlreadyExists {
}
