package tk.crypfolio.DAO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.business.exception.AppDAOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

        } catch (Exception ex) {

            LOGGER.error(ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll() {

        List<T> resultList = new ArrayList<T>();

        try {

/*          1. OGM-23 - Criteria queries are not supported yet!*/

/*          2. JPQL (HQL) doesn't worked
            https://forum.hibernate.org/viewtopic.php?f=31&t=1044918
            Hi, currently, Hibernate OGM does not support HQL query on on one to many associations.
            If you need to run a query in this situation your best bet is to use a native query.

            Hibernate Documentation doesn't have no mention nothing with "Query query = em.createQuery(...)",
            only "Query query = session.createQuery(...)",
            so I believe OMG doesn't support it, coz didn't work any variation when I tested it*/

/*          3. worked CLI-syntax version
            resultList = (List<T>) em.createNativeQuery("db.users.find({})", entityClass).getResultList();*/

            String nativeQuery = "{}";
            resultList = (List<T>) em.createNativeQuery(nativeQuery, entityClass).getResultList();

        } catch (Exception ex) {

            LOGGER.warn(ex.getMessage());
            ex.printStackTrace();
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

        } catch (Exception ex) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            LOGGER.warn(ex.getMessage());
            ex.printStackTrace();
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

        T t = null;

        try {

            String nativeQuery = "{ " + column + " : '" + value + "' }";
            List results = em.createNativeQuery(nativeQuery, entityClass).getResultList();

            if (!results.isEmpty()) {
                // we find only one entity, then we will ignore multiple results, and use only 1st result
                t = (T) results.get(0);
            }

            return t;

        } catch (NoResultException ex) {

            LOGGER.warn(ex.getMessage());
            ex.printStackTrace();
        }

        return t;
    }
}