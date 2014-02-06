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
package de.etecture.opensource.dynamicrepositories.technologies.jpa;

import de.etecture.opensource.dynamicrepositories.api.EntityAlreadyExistsException;
import de.etecture.opensource.dynamicrepositories.api.EntityNotFoundException;
import de.etecture.opensource.dynamicrepositories.spi.AbstractQueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.ConnectionResolver;
import de.etecture.opensource.dynamicrepositories.spi.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.QueryMetaData;
import de.etecture.opensource.dynamicrepositories.spi.Technology;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.List;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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
public class OldJPAQueryExecutor extends AbstractQueryExecutor {

    @Inject
    @Technology("JPA")
    ConnectionResolver<EntityManager> resolver;

    @Override
    public <T> T update(String connection, T instance) throws
            EntityNotFoundException {
        try {
            return resolver.getConnection(connection).merge(instance);
        } catch (javax.persistence.EntityNotFoundException enfe) {
            throw new EntityNotFoundException(enfe, instance.getClass());
        }
    }

    @Override
    public void delete(String connection, Object instance) throws
            EntityNotFoundException {
        try {
            resolver.getConnection(connection).remove(instance);
        } catch (javax.persistence.EntityNotFoundException enfe) {
            throw new EntityNotFoundException(enfe, instance.getClass());
        }
    }

    private <T> TypedQuery<T> createQuery(QueryMetaData<T> metadata) {
        EntityManager em = resolver.getConnection(metadata.getConnection());
        TypedQuery<T> jpaQuery;
        if (metadata.getQuery() == null || metadata.getQuery().trim().length()
                == 0) {
            // look if there is a NamedQuery with query as name
            jpaQuery = em.createNamedQuery(metadata.getQueryName(), metadata
                    .getQueryType());
        } else {
            // not found, so it is a normal query
            jpaQuery = em.createQuery(metadata.getQuery(), metadata
                    .getQueryType());
        }
        if (metadata.getCount() > 0) {
            jpaQuery.setMaxResults(metadata.getCount());
        }
        if (metadata.getOffset() >= 0) {
            jpaQuery.setFirstResult(metadata.getOffset());
        }
        for (String parameterName : metadata.getParameterNames()) {
            jpaQuery.setParameter(parameterName, metadata.getParameterValue(
                    parameterName));
        }
        return jpaQuery;
    }

    @Override
    protected <T> T executeSingletonQuery(QueryMetaData<T> metadata) throws
            EntityNotFoundException {
        EntityManager em = resolver.getConnection(metadata.getConnection());
        try {
            T result = createQuery(metadata).getSingleResult();
            if (result != null) {
                // get the single result.
                if (metadata.getConverter() == null) {
                    return result;
                } else {
                    return metadata.getConverter().convert(metadata
                            .getQueryType(),
                            result);
                }
            } else {
                throw new EntityNotFoundException(metadata.getQueryType());
            }
        } catch (javax.persistence.EntityNotFoundException | NoResultException |
                NonUniqueResultException ex) {
            throw new EntityNotFoundException(ex, metadata.getQueryType());
        }
    }

    @Override
    protected <T> List<T> executeCollectionQuery(final QueryMetaData<T> metadata) {
        EntityManager em = resolver.getConnection(metadata.getConnection());
        final List<T> resultList = createQuery(metadata).getResultList();
        if (metadata.getConverter() == null) {
            return resultList;
        } else {
            return new AbstractList<T>() {
                @Override
                public T get(int index) {
                    return metadata.getConverter().convert(metadata
                            .getQueryType(), resultList.get(index));
                }

                @Override
                public int size() {
                    return resultList.size();
                }
            };
        }
    }

    @Override
    protected <T> T executeUpdateQuery(QueryMetaData<T> metadata) throws
            Exception {
        EntityManager em = resolver.getConnection(metadata.getConnection());
        return metadata.getQueryType().cast(createQuery(metadata)
                .executeUpdate());
    }

    @Override
    protected <T> List<T> executeBulkUpdateQuery(QueryMetaData<T> metadata) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected <T> T executeDeleteQuery(QueryMetaData<T> metadata) throws
            Exception {
        return metadata.getQueryType().cast(createQuery(metadata)
                .executeUpdate());
    }

    @Override
    protected <T> List<T> executeBulkDeleteQuery(QueryMetaData<T> metadata) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected <T> T executeCreateQuery(QueryMetaData<T> metadata) throws
            EntityAlreadyExistsException {
        EntityManager em = resolver.getConnection(metadata.getConnection());
        try {
            // create a new instance
            T t = metadata.getQueryType().newInstance();
            // fill the instance
            for (PropertyDescriptor pd : Introspector.getBeanInfo(metadata
                    .getQueryType()).getPropertyDescriptors()) {
                if (metadata.getParameterMap().containsKey(pd.getName())) {
                    pd.getWriteMethod().invoke(t, metadata.getParameterMap()
                            .get(pd.getName()));
                    continue;
                }
            }
            // persist the instance
            em.persist(t);
            return t;
        } catch (IntrospectionException | InstantiationException |
                IllegalAccessException | IllegalArgumentException |
                InvocationTargetException ex) {
            throw new PersistenceException("cannot set the fieldvalues", ex);
        } catch (javax.persistence.EntityExistsException eee) {
            throw new EntityAlreadyExistsException(metadata.getQueryType());
        }
    }

    @Override
    protected <T> List<T> executeBulkCreateQuery(QueryMetaData<T> metadata) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
