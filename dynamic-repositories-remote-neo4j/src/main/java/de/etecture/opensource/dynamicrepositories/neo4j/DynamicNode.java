package de.etecture.opensource.dynamicrepositories.neo4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * marks an interface as a dynamically implemented Node in Neo4j
 *
 * @author rhk
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicNode {
}
