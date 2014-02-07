package de.etecture.opensource.dynamicrepositories.api.annotations;

import de.etecture.opensource.dynamicrepositories.api.ResultConverter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 * specifies an implementation of {@link ResultConverter} and registers it.
 *
 * @author rhk
 * @version
 * @since
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,
    ElementType.PARAMETER,
    ElementType.FIELD,
    ElementType.TYPE})
public @interface Converter {

    Class<?> value();
}
