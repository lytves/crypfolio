package tk.crypfolio.DAO;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DAOImpl<K, T> implements DAO<K, T> {

    private static final Logger logger = Logger.getLogger(DAOImpl.class.getName());

    private EntityManager em;
    private Class<T> entityClass;

    protected DAOImpl(EntityManager em, Class<T> entityClass) {

        this.em = em;
        this.entityClass = entityClass;
        this.em.getTransaction().begin();
    }

    @Override
    public T getById(K id) {

        return em.find(entityClass, id);
    }

    //    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll() {

        Query q = em.createQuery("from " + this.entityClass.getName());
        return q.getResultList();
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

            em.getTransaction().rollback();
            logger.log(Level.WARNING, e.getMessage());
        }
    }
        @Override
        public void update (T entity){

            if (entity != null) {
                em.merge(entity);
                em.getTransaction().commit();
            }
        }

        @Override
        public void delete (T entity){

            if (entity != null) {
                em.remove(entity);
                em.getTransaction().commit();
            }
        }

        @Override
        public void deleteById (K entityId){

            T entity = em.find(entityClass, entityId);
            if (entity != null) {
                em.remove(entity);
                em.getTransaction().commit();
            }
        }

        @Override
        public T findByUniqueStringColumn (String column, String value){

            try {
                CriteriaBuilder cBuilder = em.getCriteriaBuilder();
                CriteriaQuery criteriaQuery = cBuilder.createQuery();

                Root entity = criteriaQuery.from(entityClass);
                criteriaQuery.select(entity);
                criteriaQuery.where(cBuilder.equal(entity.get(column), value));

                Query query = em.createQuery(criteriaQuery);
                T t = (T) query.getSingleResult();

            } catch (NoResultException e) {

                logger.log(Level.WARNING, e.getMessage());
            }

            return null;
        }

    }
