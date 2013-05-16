package de.etecture.opensource.dynamicrepositories.api;

import java.io.Serializable;

/**
 * this is an extension for {@link Repository} interfaces to use the repository
 * also to update instances
 *
 * @author rhk
 */
public interface UpdateSupport<T extends Serializable> {

    T update(T instance) throws EntityNotFoundException;
}
