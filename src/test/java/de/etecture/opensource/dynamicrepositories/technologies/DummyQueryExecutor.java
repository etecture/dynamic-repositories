package de.etecture.opensource.dynamicrepositories.technologies;

import de.etecture.opensource.dynamicrepositories.api.EntityAlreadyExistsException;
import de.etecture.opensource.dynamicrepositories.api.EntityNotFoundException;
import de.etecture.opensource.dynamicrepositories.api.Query;
import de.etecture.opensource.dynamicrepositories.spi.QueryExecutor;
import de.etecture.opensource.dynamicrepositories.spi.Technology;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ejb.Singleton;

/**
 * represents a dummy implementation of {@link QueryExecutor}
 *
 * @author rhk
 */
@Technology("Neo4j")
@Singleton
public class DummyQueryExecutor<T extends Serializable> implements QueryExecutor<T> {

    @Override
    public T create(Query query, Class<T> clazz, Class<?>[] paramTypes, Object[] paramValues) throws EntityAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public T create(Query query, Class<T> clazz, Map<String, Object> fieldvalues) throws EntityAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public T create(String queryName, Class<T> clazz, Class<?>[] paramTypes, Object[] paramValues) throws EntityAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public T create(String queryName, Class<T> clazz, Map<String, Object> fieldvalues) throws EntityAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<T> retrieve(Query query, Class<T> clazz, Map<String, Object> parameter) {
        return (List<T>) Collections.singletonList(String.format(query.value(), parameter.values().toArray()));
    }

    @Override
    public List<T> retrieve(Query query, Class<T> clazz, Map<String, Object> parameter, int offset, int count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<T> retrieve(String queryName, Class<T> clazz, Map<String, Object> parameter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<T> retrieve(String queryName, Class<T> clazz, Map<String, Object> parameter, int offset, int count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int delete(Query query, Class<T> clazz, Map<String, Object> parameter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int delete(String queryName, Class<T> clazz, Map<String, Object> parameter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(Query query, Class<T> clazz, Map<String, Object> parameter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(String queryName, Class<T> clazz, Map<String, Object> parameter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public T update(T instance) throws EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(T instance) throws EntityNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
