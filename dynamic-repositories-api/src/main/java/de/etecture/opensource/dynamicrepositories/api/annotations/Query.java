package de.etecture.opensource.dynamicrepositories.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * defines a query.
 *
 * @author rhk
 * @version
 * @since
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,
    ElementType.ANNOTATION_TYPE})
public @interface Query {

    /**
     * the technology for which this query is defined.
     *
     * @return
     */
    String technology() default "";

    /**
     * the connection to be used to execute the query
     *
     * @return
     */
    String connection() default "";

    /**
     * the statement. if the statement is not applied, the following rules
     * apply:
     *
     * <ol>
     * <li>lookup a resource bundle with the name of this method.</li>
     * <li>otherwise the name of the method is used as the statement</li>
     * </ol>
     *
     * @return
     */
    String statement() default "";

    /**
     * defines hints for this query.
     *
     * @return
     */
    Hint[] hints() default {};

    /**
     * defines additional parameters for this query.
     *
     * @return
     */
    Param[] params() default {};

}
