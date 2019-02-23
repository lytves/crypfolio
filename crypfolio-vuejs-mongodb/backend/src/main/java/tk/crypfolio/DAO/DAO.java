package tk.crypfolio.DAO;

import tk.crypfolio.business.exception.AppDAOException;

import java.util.List;

public interface DAO<K, T> {

    public T getById(K id);

    public List<T> findAll();

    public void create(T entity);

    public T update(T entity) throws AppDAOException;

    public void delete(T entity);

    public void deleteById(K entityId);

    public T findByUniqueStringColumn(String column, String value);

/*    public List<T> findByCriteria(String join, String criteria);

    public List<T> findAllOrderByDesc(String campo);

    public List<Object[]> findObjectsByNativeQuery(String query);

    public Object findByNativeQuery(String query);

    public void updateByNativeQuery(String query);*/

}