package de.etecture.opensource.dynamicrepositories.technologies.jpa;

/**
 *
 * @author rhk
 * @version
 * @since
 */
public interface JPAQueryHints {

    /**
     * this hint specifies the kind of the query. possible values are:
     * <ul>
     * <li>CREATE
     * <li>RETRIEVE
     * <li>UPDATE
     * <li>DELETE
     * </ul>
     */
    String QUERY_KIND = "JPA_QUERY_KIND";
    /**
     * this hint specifies the type of the query. possible values are:
     * <ul>
     * <li>NAMED
     * <li>NATIVE
     * <li>JPAQL
     * </ul>
     */
    String QUERY_TYPE = "JPA_QUERY_TYPE";
}
