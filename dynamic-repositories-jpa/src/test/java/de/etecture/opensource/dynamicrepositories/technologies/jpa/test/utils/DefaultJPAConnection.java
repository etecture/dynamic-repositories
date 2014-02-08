package de.etecture.opensource.dynamicrepositories.technologies.jpa.test.utils;

import de.etecture.opensource.dynamicrepositories.technologies.jpa.AbstractConnection;
import javax.ejb.Singleton;
import javax.enterprise.inject.Default;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rhk
 * @version
 * @since
 *
 */
@Default
@Singleton
public class DefaultJPAConnection extends AbstractConnection {

    @PersistenceContext
    EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
