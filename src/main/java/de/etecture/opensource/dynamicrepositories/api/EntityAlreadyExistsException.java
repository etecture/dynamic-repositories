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
public class EntityAlreadyExistsException extends Exception {
	private static final long serialVersionUID = 1L;

	private final Class<? extends Serializable> entityClass;

	public EntityAlreadyExistsException(Class<? extends Serializable> entityClass) {
		super("an Entity of type "+entityClass.getName()+" already exists!");
		this.entityClass = entityClass;
	}

	public EntityAlreadyExistsException(Throwable cause, Class<? extends Serializable> entityClass) {
		super("an Entity of type "+entityClass.getName()+" already exists!", cause);
		this.entityClass = entityClass;
	}

	public Class<? extends Serializable> getEntityClass() {
		return entityClass;
	}

}
