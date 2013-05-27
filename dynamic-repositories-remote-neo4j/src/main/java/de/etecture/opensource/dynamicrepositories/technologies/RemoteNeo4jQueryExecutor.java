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
import de.herschke.neo4j.uplink.api.CypherResult;
import de.herschke.neo4j.uplink.api.Neo4jUplink;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Singleton;

/**
 * this is an implementation of a {@link QueryExecutor} to use an remote Neo4j
 * database service.
 *
 * @author rhk
 */
@Technology("Neo4j")
@Singleton
public class RemoteNeo4jQueryExecutor extends AbstractQueryExecutor {

    @EJB
    Neo4jUplink neo4jServer;

    @Override
    protected <T> T executeSingletonQuery(QueryMetaData<T> metadata) throws Exception {
        CypherResult result = neo4jServer.executeCypherQuery(metadata.getQuery(), metadata.getParameterMap());
        if (!result.isEmpty()) {
            // get the single result.
            Map<String, Object> singleResult = result.getRowData(0);
            if (metadata.getConverter() == null) {
                return (T) singleResult;
            } else {
                return metadata.getConverter().convert(metadata.getQueryType(), metadata.getQueryGenericType(), singleResult);
            }
        } else {
            throw new EntityNotFoundException(metadata.getQueryType(), "");
        }
    }

    @Override
    protected <T> T executeCollectionQuery(QueryMetaData<T> metadata) throws Exception {
        CypherResult result = neo4jServer.executeCypherQuery(metadata.getQuery(), metadata.getParameterMap());
        if (metadata.getConverter() == null) {
            return metadata.getQueryType().cast(result);
        } else {
            return metadata.getConverter().convert(metadata.getQueryType(), metadata.getQueryGenericType(), result.getAllValues());
        }
    }

    @Override
    protected <T> T executeBulkUpdateQuery(QueryMetaData<T> metadata) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected <T> T executeBulkDeleteQuery(QueryMetaData<T> metadata) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected <T> T executeCreateQuery(QueryMetaData<T> metadata) throws EntityAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Object instance) throws EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T update(T instance) throws EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
