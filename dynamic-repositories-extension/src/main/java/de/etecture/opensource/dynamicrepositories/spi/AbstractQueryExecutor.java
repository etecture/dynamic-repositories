/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.etecture.opensource.dynamicrepositories.spi;

import de.etecture.opensource.dynamicrepositories.api.EntityAlreadyExists;
import de.etecture.opensource.dynamicrepositories.api.EntityAlreadyExistsException;
import de.etecture.opensource.dynamicrepositories.api.EntityNotFound;
import de.etecture.opensource.dynamicrepositories.api.EntityNotFoundException;
import java.util.Collection;

/**
 *
 * @author rhk
 */
public abstract class AbstractQueryExecutor implements QueryExecutor {

    protected abstract <T> T executeSingletonQuery(QueryMetaData<T> metadata) throws EntityNotFoundException;

    protected abstract <T> T executeCollectionQuery(QueryMetaData<T> metadata) throws Exception;

    protected abstract <T> T executeBulkUpdateQuery(QueryMetaData<T> metadata) throws Exception;

    protected abstract <T> T executeBulkDeleteQuery(QueryMetaData<T> metadata) throws Exception;

    protected abstract <T> T executeCreateQuery(QueryMetaData<T> metadata) throws EntityAlreadyExistsException;

    @Override
    public <T> T execute(QueryMetaData<T> metadata) throws Exception {
        switch (metadata.getQueryKind()) {
            case CREATE:
                try {
                    return executeCreateQuery(metadata);
                } catch (EntityAlreadyExistsException ex) {
                    throw metadata.createException(EntityAlreadyExists.class, "cannot create entity", ex);
                }
            case RETRIEVE:
                if (Collection.class.isAssignableFrom(metadata.getQueryType()) || metadata.getQueryType().isArray()) {
                    return executeCollectionQuery(metadata);
                } else {
                    try {
                        return executeSingletonQuery(metadata);
                    } catch (EntityNotFoundException ex) {
                        throw metadata.createException(EntityNotFound.class, "cannot find result", ex);
                    }
                }
            case DELETE:
                return executeBulkDeleteQuery(metadata);
            case UPDATE:
                return executeBulkUpdateQuery(metadata);
            default:
                throw new UnsupportedOperationException(String.format("Query of kind: %s is not supported.", metadata.getQueryKind()));
        }
    }
}
