/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.etecture.opensource.dynamicrepositories.api;

import java.io.Serializable;
import javax.ejb.ApplicationException;

/**
 *
 * @author rherschke
 */
@ApplicationException
public class EntityNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	private final Serializable entityId;
	private final Class<? extends Serializable> entityClass;

	public EntityNotFoundException(Class<? extends Serializable> entityClass, Serializable entityId) {
        super(String.format("an Entity of type %s with id %s cannot be found!", entityClass.getName(), entityId));
		this.entityId = entityId;
		this.entityClass = entityClass;
	}

	public EntityNotFoundException(Throwable cause, Class<? extends Serializable> entityClass, Serializable entityId) {
        super(String.format("an Entity of type %s with id %s cannot be found!", entityClass.getName(), entityId), cause);
		this.entityId = entityId;
		this.entityClass = entityClass;
	}

	public Class<? extends Serializable> getEntityClass() {
		return entityClass;
	}

	public Serializable getEntityId() {
		return entityId;
	}

}
