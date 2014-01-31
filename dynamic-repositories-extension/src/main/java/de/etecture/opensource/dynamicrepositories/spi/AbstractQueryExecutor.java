/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.etecture.opensource.dynamicrepositories.spi;

import de.etecture.opensource.dynamicrepositories.api.EntityAlreadyExists;
import de.etecture.opensource.dynamicrepositories.api.EntityAlreadyExistsException;
import de.etecture.opensource.dynamicrepositories.api.EntityNotFound;
import de.etecture.opensource.dynamicrepositories.api.EntityNotFoundException;
import java.util.List;

/**
 *
 * @author rhk
 */
public abstract class AbstractQueryExecutor implements QueryExecutor {

    protected abstract <T> T executeSingletonQuery(QueryMetaData<T> metadata)
            throws Exception;

    protected abstract <T> List<T> executeCollectionQuery(
            QueryMetaData<T> metadata)
            throws Exception;

    protected abstract <T> T executeUpdateQuery(QueryMetaData<T> metadata)
            throws Exception;

    protected abstract <T> List<T> executeBulkUpdateQuery(
            QueryMetaData<T> metadata)
            throws Exception;

    protected abstract <T> T executeDeleteQuery(QueryMetaData<T> metadata)
            throws Exception;

    protected abstract <T> List<T> executeBulkDeleteQuery(
            QueryMetaData<T> metadata)
            throws Exception;

    protected abstract <T> T executeCreateQuery(QueryMetaData<T> metadata)
            throws Exception;

    protected abstract <T> List<T> executeBulkCreateQuery(
            QueryMetaData<T> metadata)
            throws Exception;

    @Override
    public Object execute(QueryMetaData metadata) throws Exception {
        switch (metadata.getQueryKind()) {
            case CREATE:
                if (metadata.getType() == QueryMetaData.Type.LIST) {
                    return executeBulkCreateQuery(metadata);
                } else {
                    try {
                        return executeCreateQuery(metadata);
                    } catch (EntityAlreadyExistsException ex) {
                        throw metadata
                                .createException(EntityAlreadyExists.class,
                                "cannot create entity", ex);
                    }
                }
            case RETRIEVE:
                if (metadata.getType() == QueryMetaData.Type.LIST) {
                    return executeCollectionQuery(metadata);
                } else {
                    try {
                        return executeSingletonQuery(metadata);
                    } catch (EntityNotFoundException ex) {
                        throw metadata.createException(EntityNotFound.class,
                                "cannot find result", ex);
                    }
                }
            case DELETE:
                if (metadata.getType() == QueryMetaData.Type.LIST) {
                    return executeBulkDeleteQuery(metadata);
                } else {
                    try {
                        return executeDeleteQuery(metadata);
                    } catch (EntityNotFoundException ex) {
                        throw metadata.createException(EntityNotFound.class,
                                "cannot find result", ex);
                    }
                }
            case UPDATE:
                if (metadata.getType() == QueryMetaData.Type.LIST) {
                    return executeBulkUpdateQuery(metadata);
                } else {
                    try {
                        return executeUpdateQuery(metadata);
                    } catch (EntityNotFoundException ex) {
                        throw metadata.createException(EntityNotFound.class,
                                "cannot find result", ex);
                    }
                }
            default:
                throw new UnsupportedOperationException(String.format(
                        "Query of kind: %s is not supported.", metadata
                        .getQueryKind()));
        }
    }
}
