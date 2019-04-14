package tk.crypfolio.DAO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.business.exception.AppDAOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

// TODO create some general DAO Exception functions, may be should use my own exception class

public abstract class DAOImpl<K, T> implements DAO<K, T> {

    private static final Logger LOGGER = LogManager.getLogger(DAOImpl.class);

    private EntityManager em;
    private Class<T> entityClass;

    protected DAOImpl(EntityManager em, Class<T> entityClass) {

        this.em = em;
        this.entityClass = entityClass;
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    @Override
    public T getById(K id) {

        try {

            return em.find(entityClass, id);

        } catch (Exception e) {

            LOGGER.warn(e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll() {

        List<T> resultList = new ArrayList<T>();

        try {

            Query q = em.createQuery("from " + this.entityClass.getName());
            resultList = (List<T>) q.getResultList();

        } catch (Exception e) {

            LOGGER.warn(e.getMessage());
        }
        return resultList;
    }

    @Override
    public void create(T entity) {

        try {

            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            if (entity != null) {
                em.persist(entity);
                em.getTransaction().commit();
            }

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.warn(e.getMessage());
        }

    }

    @Override
    public T update(T entity) throws AppDAOException {

        try {

            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
            }

            if (entity != null) {
                entity = em.merge(entity);
                em.getTransaction().commit();

                return entity;
            }

        } catch (Exception ex) {

            throw new AppDAOException("DAOImpl.update Exception: ", ex, AppDAOException._UPDATE_FAILED);

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        return null;
    }

    @Override
    public void delete(T entity) {

        if (entity != null) {
            em.remove(entity);
            em.getTransaction().commit();
        }
    }

    @Override
    public void deleteById(K entityId) {

        T entity = em.find(entityClass, entityId);
        if (entity != null) {
            em.remove(entity);
            em.getTransaction().commit();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T findByUniqueStringColumn(String column, String value) {

        try {
            CriteriaBuilder cBuilder = em.getCriteriaBuilder();
            CriteriaQuery criteriaQuery = cBuilder.createQuery();

            Root entity = criteriaQuery.from(entityClass);
            criteriaQuery.select(entity);
            criteriaQuery.where(cBuilder.equal(entity.get(column), value));

            Query query = em.createQuery(criteriaQuery);

//            return (T) query.getSingleResult();

            // below is a realisation of getResultList to avoid use of insecure method getSingleResult()
            List results = query.getResultList();

            T t = null;

            if (!results.isEmpty()) {
                // ignores multiple results
                t = (T) results.get(0);
            }

            return t;

        } catch (NoResultException e) {

            LOGGER.warn(e.getMessage());
        }

        return null;
    }
}