/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.etecture.opensource.dynamicrepositories.spi;

import de.etecture.opensource.dynamicrepositories.api.DeleteSupport;
import de.etecture.opensource.dynamicrepositories.api.EntityAlreadyExistsException;
import de.etecture.opensource.dynamicrepositories.api.Query;
import de.etecture.opensource.dynamicrepositories.api.UpdateSupport;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * the implementations of this interface executes queries
 *
 * @author rhk
 */
public interface QueryExecutor<T extends Serializable> extends UpdateSupport<T>, DeleteSupport<T> {

    /**
     * creates an entity by delegating to a constructor of the entity class
     *
     * @param query the query metadata for the create method
     * @param clazz the clazz that represents the entity to be returned
     * @param paramTypes an array of the parameter types of the create method
     * @param paramValues an array of the parameter values of the invoked create
     * method
     * @return the persisted and created entity instance
     * @throws EntityAlreadyExistsException when the unique entity id constraint
     * is violated
     */
    T create(Query query, Class<T> clazz, Class<?>[] paramTypes, Object[] paramValues) throws EntityAlreadyExistsException;

    /**
     * creates an entity with the given class and the given fieldvalues
     *
     * @param query the query metadata for the create method
     * @param clazz the clazz that represents the entity to be returned
     * @param fieldvalues the values of the fields to be set when the entity is
     * created
     * @return the persisted and created entity instance
     * @throws EntityAlreadyExistsException when the unique entity id constraint
     * is violated
     */
    T create(Query query, Class<T> clazz, Map<String, Object> fieldvalues) throws EntityAlreadyExistsException;

    /**
     * creates an entity by delegating to a constructor of the entity class
     *
     * @param queryName the name of the query to be used.
     * @param clazz the clazz that represents the entity to be returned
     * @param paramTypes an array of the parameter types of the create method
     * @param paramValues an array of the parameter values of the invoked create
     * method
     * @return the persisted and created entity instance
     * @throws EntityAlreadyExistsException when the unique entity id constraint
     * is violated
     */
    T create(String queryName, Class<T> clazz, Class<?>[] paramTypes, Object[] paramValues) throws EntityAlreadyExistsException;

    /**
     * creates an entity with the given class and the given fieldvalues
     *
     * @param queryName the name of the query to be used.
     * @param clazz the clazz that represents the entity to be returned
     * @param fieldvalues the values of the fields to be set when the entity is
     * created
     * @return the persisted and created entity instance
     * @throws EntityAlreadyExistsException when the unique entity id constraint
     * is violated
     */
    T create(String queryName, Class<T> clazz, Map<String, Object> fieldvalues) throws EntityAlreadyExistsException;

    /**
     * executes the given query
     *
     * @param query the query to be executed
     * @param clazz the class that represents the entity to be returned
     * @param parameter the map with parameters (may be null or empty)
     * @return the resultset of the query
     */
    List<T> retrieve(Query query, Class<T> clazz, Map<String, Object> parameter);

    /**
     * executes the given query
     *
     * @param query the query to be executed
     * @param clazz the class that represents the entity to be returned
     * @param parameter the map with parameters (may be null or empty)
     * @param offset the offset of the result page
     * @param count the size of the page
     * @return the resultset of the query
     */
    List<T> retrieve(Query query, Class<T> clazz, Map<String, Object> parameter, int offset, int count);

    /**
     * executes the given query
     *
     * @param queryName the name of the query to be executed
     * @param clazz the class that represents the entity to be returned
     * @param parameter the map with parameters (may be null or empty)
     * @return the resultset of the query
     */
    List<T> retrieve(String queryName, Class<T> clazz, Map<String, Object> parameter);

    /**
     * executes the given query
     *
     * @param queryName the name of the query to be executed
     * @param clazz the class that represents the entity to be returned
     * @param parameter the map with parameters (may be null or empty)
     * @param offset the offset of the result page
     * @param count the size of the page
     * @return the resultset of the query
     */
    List<T> retrieve(String queryName, Class<T> clazz, Map<String, Object> parameter, int offset, int count);

    /**
     * executes the given delete query
     *
     * @param query the query to be executed
     * @param clazz the class that represents the entity to be returned
     * @param parameter the map with parameters (may be null or empty)
     * @return the number of deleted entities that are made when executed this
     * query
     */
    int delete(Query query, Class<T> clazz, Map<String, Object> parameter);

    /**
     * executes the given delete query
     *
     * @param queryName the name of the query to be executed
     * @param clazz the class that represents the entity to be returned
     * @param parameter the map with parameters (may be null or empty)
     * @return the number of deleted entities that are made when executed this
     * query
     */
    int delete(String queryName, Class<T> clazz, Map<String, Object> parameter);

    /**
     * executes the given update query
     *
     * @param query the query to be executed
     * @param clazz the class that represents the entity to be returned
     * @param parameter the map with parameters (may be null or empty)
     * @return the number of updates that are made when executed this query
     */
    int update(Query query, Class<T> clazz, Map<String, Object> parameter);

    /**
     * executes the given update query
     *
     * @param queryName the name of the query to be executed
     * @param clazz the class that represents the entity to be returned
     * @param parameter the map with parameters (may be null or empty)
     * @return the number of updates that are made when executed this query
     */
    int update(String queryName, Class<T> clazz, Map<String, Object> parameter);
}
