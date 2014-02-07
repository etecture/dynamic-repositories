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

import de.etecture.opensource.dynamicrepositories.api.exceptions.EntityNotFoundException;
import de.etecture.opensource.dynamicrepositories.spi.AbstractQueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.ConnectionResolver;
import de.etecture.opensource.dynamicrepositories.spi.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.QueryMetaData;
import de.etecture.opensource.dynamicrepositories.executor.Technology;
import de.etecture.opensource.jeelogging.api.Log;
import static de.etecture.opensource.jeelogging.api.LogEvent.Severity.*;
import de.herschke.neo4j.uplink.api.CypherResult;
import de.herschke.neo4j.uplink.api.Neo4jUplink;
import java.util.AbstractList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * this is an implementation of a {@link QueryExecutor} to use an remote Neo4j
 * database service.
 *
 * @author rhk
 */
@Technology("Neo4j")
@Singleton
public class RemoteNeo4jQueryExecutor extends AbstractQueryExecutor {

    @Inject
    @Default
    Log log;
    @Inject
    @Technology("Neo4j")
    ConnectionResolver<Neo4jUplink> connectionResolver;

    @Override
    protected <T> T executeSingletonQuery(QueryMetaData<T> metadata) throws
            Exception {
        String query = buildQuery(metadata);
        log.log(FINER, "connection is: %s%n", metadata.getConnection());
        log.log(FINER, "query is: %n%s%n", query);
        log.log(FINER, "parameters are: %n%s%n", metadata.getParameterMap());
        Neo4jUplink neo4jServer = connectionResolver.getConnection(metadata
                .getConnection());
        if (metadata.getConverter() == null && metadata.getQueryType()
                .isInterface()) {
            List<T> resultList = neo4jServer.executeCypherQuery(metadata
                    .getQueryType(), query, metadata
                    .getParameterMap());
            log.log(FINEST, resultList.toString());
            if (!resultList.isEmpty()) {
                return resultList.get(0);
            } else {
                throw new EntityNotFoundException(metadata.getQueryType());
            }
        } else {
            CypherResult result = neo4jServer.executeCypherQuery(query, metadata
                    .getParameterMap());
            log.log(FINEST, result.toString());
            if (!result.isEmpty()) {
                // get the single result.
                Map<String, Object> singleResult = result.getRowData(0);
                if (metadata.getConverter() == null) {
                    return (T) singleResult;
                } else {
                    return metadata.getConverter().convert(metadata
                            .getQueryType(),
                            singleResult);
                }
            } else {
                throw new EntityNotFoundException(metadata.getQueryType());
            }
        }
    }

    @Override
    protected <T> List<T> executeCollectionQuery(final QueryMetaData<T> metadata)
            throws
            Exception {
        String query = buildQuery(metadata);
        log.log(FINER, "query is: %n%s%n", query);
        log.log(FINER, "parameters are: %n%s%n", metadata.getParameterMap());
        Neo4jUplink neo4jServer = connectionResolver.getConnection(metadata
                .getConnection());
        final List<T> result = neo4jServer.executeCypherQuery(metadata
                .getQueryType(), query, metadata.getParameterMap());
        if (metadata.getConverter() == null && metadata.getQueryType()
                .isInterface()) {
            return result;
        } else {
            return new AbstractList<T>() {
                @Override
                public T get(int index) {
                    return metadata.getConverter().convert(metadata
                            .getQueryType(), result.get(index));
                }

                @Override
                public int size() {
                    return result.size();
                }
            };
        }
    }

    @Override
    protected <T> T executeUpdateQuery(QueryMetaData<T> metadata) throws
            Exception {
        return executeSingletonQuery(metadata);
    }

    @Override
    protected <T> List<T> executeBulkUpdateQuery(QueryMetaData<T> metadata)
            throws
            Exception {
        return executeCollectionQuery(metadata);
    }

    @Override
    protected <T> T executeDeleteQuery(QueryMetaData<T> metadata) throws
            Exception {
        return executeSingletonQuery(metadata);
    }

    @Override
    protected <T> List<T> executeBulkDeleteQuery(QueryMetaData<T> metadata)
            throws
            Exception {
        return executeCollectionQuery(metadata);
    }

    @Override
    protected <T> List<T> executeBulkCreateQuery(QueryMetaData<T> metadata)
            throws
            Exception {
        return executeCollectionQuery(metadata);
    }

    @Override
    protected <T> T executeCreateQuery(QueryMetaData<T> metadata) throws
            Exception {
        return executeSingletonQuery(metadata);
    }

    @Override
    public void delete(String connection, Object instance) throws
            EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T update(String connection, T instance) throws
            EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String buildQuery(
            QueryMetaData metadata) {
        String query;
        if (metadata.getQuery() == null || metadata.getQuery().trim().length()
                == 0) {
            query = ResourceBundle.getBundle(metadata.getRepositoryClass()
                    .getName()).getString(metadata.getQueryName());
        } else {
            query = metadata.getQuery();
        }
        return query;
    }
}
