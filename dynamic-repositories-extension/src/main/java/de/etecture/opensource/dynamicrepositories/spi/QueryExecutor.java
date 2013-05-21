/*
 * This file is part of the ETECTURE Open Source Community Projects.
 *
 * Copyright (c) 2013 by:
 *
 * ETECTURE GmbH
 * Darmstädter Landstraße 112
 * 60598 Frankfurt
 * Germany
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the author nor the names of its contributors may be
 *    used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
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
