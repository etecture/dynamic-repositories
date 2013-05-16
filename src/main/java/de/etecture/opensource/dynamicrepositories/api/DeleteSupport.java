package de.etecture.opensource.dynamicrepositories.api;

import java.io.Serializable;

/**
 * this is an extension for {@link Repository} interfaces to use the repository
 * also to delete instances
 *
 * @author rhk
 */
public interface DeleteSupport<T extends Serializable> {

    void delete(T instance) throws EntityNotFoundException;
}
