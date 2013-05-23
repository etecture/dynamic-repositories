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
package de.etecture.opensource.dynamicrepositories.technologies;

import de.etecture.opensource.dynamicrepositories.api.EntityAlreadyExistsException;
import de.etecture.opensource.dynamicrepositories.api.EntityNotFoundException;
import de.etecture.opensource.dynamicrepositories.spi.AbstractQueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.QueryMetaData;
import de.etecture.opensource.dynamicrepositories.spi.Technology;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

/**
 * this is a {@link QueryExecutor} implementation to execute all kind of JPA
 * Queries
 *
 * @author rhk
 */
@Technology("JPA")
@Singleton
public class JPAQueryExecutor extends AbstractQueryExecutor {

    @PersistenceContext(name = "persistence/DynamicRepositoryDB")
    EntityManager em;

    @Override
    public <T> T update(T instance) throws EntityNotFoundException {
        try {
            return em.merge(instance);
        } catch (javax.persistence.EntityNotFoundException enfe) {
            throw new EntityNotFoundException(enfe, instance.getClass(), null);
        }
    }

    @Override
    public void delete(Object instance) throws EntityNotFoundException {
        try {
            em.remove(instance);
        } catch (javax.persistence.EntityNotFoundException enfe) {
            throw new EntityNotFoundException(enfe, instance.getClass(), null);
        }
    }

    private <T> TypedQuery<T> createQuery(QueryMetaData<T> metadata) {
        TypedQuery<T> jpaQuery;
        if (metadata.getQuery() == null || metadata.getQuery().trim().length() == 0) {
            // look if there is a NamedQuery with query as name
            jpaQuery = em.createNamedQuery(metadata.getQueryName(), metadata.getQueryType());
        } else {
            // not found, so it is a normal query
            jpaQuery = em.createQuery(metadata.getQuery(), metadata.getQueryType());
        }
        if (metadata.getCount() > 0) {
            jpaQuery.setMaxResults(metadata.getCount());
        }
        if (metadata.getOffset() >= 0) {
            jpaQuery.setFirstResult(metadata.getOffset());
        }
        for (String parameterName : metadata.getParameterNames()) {
            jpaQuery.setParameter(parameterName, metadata.getParameterValue(parameterName));
        }
        return jpaQuery;
    }

    @Override
    protected <T> T executeSingletonQuery(QueryMetaData<T> metadata) throws EntityNotFoundException {
        List<T> resultList = createQuery(metadata).getResultList();
        if (!resultList.isEmpty()) {
            // get the single result.
            T singleResult = resultList.get(0);
            if (metadata.getConverter() == null) {
                return singleResult;
            } else {
                return metadata.getConverter().convert(metadata.getQueryType(), singleResult);
            }
        } else {
            throw new EntityNotFoundException(metadata.getQueryType(), "");
        }
    }

    @Override
    protected <T> T executeCollectionQuery(QueryMetaData<T> metadata) {
        List<T> resultList = createQuery(metadata).getResultList();
        if (metadata.getConverter() == null) {
            return metadata.getQueryType().cast(resultList);
        } else {
            return metadata.getConverter().convert(metadata.getQueryType(), resultList);
        }
    }

    @Override
    protected <T> T executeBulkUpdateQuery(QueryMetaData<T> metadata) {
        return metadata.getQueryType().cast(createQuery(metadata).executeUpdate());
    }

    @Override
    protected <T> T executeBulkDeleteQuery(QueryMetaData<T> metadata) {
        return metadata.getQueryType().cast(createQuery(metadata).executeUpdate());
    }

    @Override
    protected <T> T executeCreateQuery(QueryMetaData<T> metadata) throws EntityAlreadyExistsException {
        try {
            // create a new instance
            T t = metadata.getQueryType().newInstance();
            // fill the instance
            for (PropertyDescriptor pd : Introspector.getBeanInfo(metadata.getQueryType()).getPropertyDescriptors()) {
                if (metadata.getParameterMap().containsKey(pd.getName())) {
                    pd.getWriteMethod().invoke(t, metadata.getParameterMap().get(pd.getName()));
                    continue;
                }
            }
            // persist the instance
            em.persist(t);
            return t;
        } catch (IntrospectionException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new PersistenceException("cannot set the fieldvalues", ex);
        } catch (javax.persistence.EntityExistsException eee) {
            throw new EntityAlreadyExistsException(metadata.getQueryType());
        }
    }
}
