package de.etecture.opensource.dynamicrepositories.spi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 * this is a qualifier to distinguish different technologies for repository
 * instances.
 *
 * @author rhk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
@Qualifier
public @interface Technology {

    String defaultTechnology = "JPA";

    String value();
}
