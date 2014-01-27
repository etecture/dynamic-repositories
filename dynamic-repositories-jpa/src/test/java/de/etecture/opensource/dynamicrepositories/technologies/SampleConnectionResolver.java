package de.etecture.opensource.dynamicrepositories.technologies;

import de.etecture.opensource.dynamicrepositories.spi.Technology;
import de.etecture.opensource.dynamicrepositories.spi.ConnectionResolver;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rhk
 * @version
 * @since
 *
 */
@Technology("JPA")
@Singleton
public class SampleConnectionResolver implements
        ConnectionResolver<EntityManager> {

    @PersistenceContext(name = "persistence/DynamicRepositoryDB")
    EntityManager em;

    @Override
    public EntityManager getConnection(String name) {
        return em;
    }
}
