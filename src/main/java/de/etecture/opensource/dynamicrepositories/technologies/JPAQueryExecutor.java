package de.etecture.opensource.dynamicrepositories.technologies;

import de.etecture.opensource.dynamicrepositories.api.DeleteSupport;
import de.etecture.opensource.dynamicrepositories.api.EntityAlreadyExistsException;
import de.etecture.opensource.dynamicrepositories.api.EntityNotFoundException;
import de.etecture.opensource.dynamicrepositories.api.Query;
import de.etecture.opensource.dynamicrepositories.api.UpdateSupport;
import de.etecture.opensource.dynamicrepositories.spi.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.Technology;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

/**
 * this is a {@link QueryExecutor} implementation to execute all kind of JPA
 * Queries
 *
 * @author rhk
 */
@Technology("JPA")
@Singleton
public class JPAQueryExecutor<T extends Serializable> implements QueryExecutor<T>, UpdateSupport<T>, DeleteSupport<T> {

    @PersistenceContext(name = "persistence/DynamicRepositoryDB")
    EntityManager em;

    @Override
    public List<T> retrieve(Query query, Class<T> clazz, Map<String, Object> parameter) {
        return buildJPAQueryByQuery(query.value(), clazz, parameter).getResultList();
    }

    @Override
    public int delete(Query query, Class<T> clazz, Map<String, Object> parameter) {
        return buildJPAQueryByQuery(query.value(), clazz, parameter).executeUpdate();
    }

    @Override
    public int update(Query query, Class<T> clazz, Map<String, Object> parameter) {
        return buildJPAQueryByQuery(query.value(), clazz, parameter).executeUpdate();
    }

    @Override
    public List<T> retrieve(Query query, Class<T> clazz, Map<String, Object> parameter, int offset, int count) {
        return buildJPAQueryByQuery(query.value(), clazz, parameter).setFirstResult(offset).setMaxResults(count).getResultList();
    }

    @Override
    public List<T> retrieve(String queryName, Class<T> clazz, Map<String, Object> parameter) {
        return buildJPAQueryByName(queryName, clazz, parameter).getResultList();
    }

    @Override
    public int delete(String queryName, Class<T> clazz, Map<String, Object> parameter) {
        return buildJPAQueryByName(queryName, clazz, parameter).executeUpdate();
    }

    @Override
    public int update(String queryName, Class<T> clazz, Map<String, Object> parameter) {
        return buildJPAQueryByName(queryName, clazz, parameter).executeUpdate();
    }

    @Override
    public List<T> retrieve(String queryName, Class<T> clazz, Map<String, Object> parameter, int offset, int count) {
        return buildJPAQueryByName(queryName, clazz, parameter).setFirstResult(offset).setMaxResults(count).getResultList();
    }

    private javax.persistence.Query buildJPAQueryByQuery(String query, Class<T> clazz, Map<String, Object> parameter) {
        javax.persistence.Query jpaQuery = em.createQuery(query, clazz);
        for (Map.Entry<String, Object> param : parameter.entrySet()) {
            jpaQuery.setParameter(param.getKey(), param.getValue());
        }
        return jpaQuery;
    }

    private javax.persistence.Query buildJPAQueryByName(String queryName, Class<T> clazz, Map<String, Object> parameter) {
        javax.persistence.Query jpaQuery = em.createNamedQuery(queryName, clazz);
        for (Map.Entry<String, Object> param : parameter.entrySet()) {
            jpaQuery.setParameter(param.getKey(), param.getValue());
        }
        return jpaQuery;
    }

    @Override
    public T create(Query create, Class<T> clazz, Map<String, Object> fieldvalues) throws EntityAlreadyExistsException {
        try {
            // create a new instance
            T t = clazz.newInstance();
            // fill the instance
            for (PropertyDescriptor pd : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
                if (fieldvalues.containsKey(pd.getName())) {
                    pd.getWriteMethod().invoke(t, fieldvalues.get(pd.getName()));
                    continue;
                }
            }
            // persist the instance
            em.persist(t);
            return t;
        } catch (IntrospectionException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new PersistenceException("cannot set the fieldvalues", ex);
        } catch (javax.persistence.EntityExistsException eee) {
            throw new EntityAlreadyExistsException(eee, clazz);
        }
    }

    @Override
    public T create(Query create, Class<T> clazz, Class<?>[] paramTypes, Object[] paramValues) throws EntityAlreadyExistsException {
        try {
            // create a new instance
            T t = clazz.getConstructor(paramTypes).newInstance(paramValues);
            // persist the instance
            em.persist(t);
            return t;
        } catch (InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            throw new PersistenceException("cannot create the instance", ex);
        } catch (javax.persistence.EntityExistsException eee) {
            throw new EntityAlreadyExistsException(eee, clazz);
        }
    }

    @Override
    public T create(String queryName, Class<T> clazz, Map<String, Object> fieldvalues) throws EntityAlreadyExistsException {
        return create((Query) null, clazz, fieldvalues);
    }

    @Override
    public T create(String queryName, Class<T> clazz, Class<?>[] paramTypes, Object[] paramValues) throws EntityAlreadyExistsException {
        return create((Query) null, clazz, paramTypes, paramValues);
    }

    @Override
    public T update(T instance) throws EntityNotFoundException {
        System.out.println("update: " + instance);
        //try {
            return em.merge(instance);
        //} catch (javax.persistence.EntityNotFoundException enfe) {
        //    throw new EntityNotFoundException(enfe, instance.getClass(), null);
        //}
    }

    @Override
    public void delete(T instance) throws EntityNotFoundException {
        System.out.println("delete: " + instance);
        //try {
            em.remove(instance);
        //} catch (javax.persistence.EntityNotFoundException enfe) {
        //    throw new EntityNotFoundException(enfe, instance.getClass(), null);
        //}
    }
}
