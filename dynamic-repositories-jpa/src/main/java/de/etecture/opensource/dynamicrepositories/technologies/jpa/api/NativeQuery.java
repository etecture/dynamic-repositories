package de.etecture.opensource.dynamicrepositories.technologies.jpa.api;

import de.etecture.opensource.dynamicrepositories.api.annotations.Hint;
import de.etecture.opensource.dynamicrepositories.technologies.jpa.JPAQueryHints;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author rhk
 * @version
 * @since
 */
@Hint(name = JPAQueryHints.QUERY_TYPE,
      value = "NATIVE")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NativeQuery {
}
