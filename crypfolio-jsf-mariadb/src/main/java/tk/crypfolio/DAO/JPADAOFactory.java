package tk.crypfolio.DAO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPADAOFactory extends AbstractDAOFactory {

    private static final Logger LOGGER = LogManager.getLogger(JPADAOFactory.class);

    private volatile static EntityManagerFactory emf;

    private static EntityManagerFactory getInstanceEntityManagerFactory() {

        if (emf == null) {
            synchronized (EntityManagerFactory.class) {
                if (emf == null) {
                    emf = Persistence.createEntityManagerFactory("CrypFolioPersistenceUnitJPA");
                    LOGGER.info("createEntityManagerFactory - only one for all Project!");
                }
            }
        }
        return emf;
    }

    private static EntityManager getEntityManager() {

        try {
            LOGGER.info("EntityManager is creating...");
            return getInstanceEntityManagerFactory().createEntityManager();

        } catch (Exception ex) {
            LOGGER.error("Failed on creating EntityManager: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public CoinDAO getCoinDAO() {
        return new CoinDAOImpl(getEntityManager());
    }

    @Override
    public ItemDAO getItemDAO() {
        return new ItemDAOImpl(getEntityManager());
    }

    @Override
    public PortfolioDAO getPortfolioDAO() {
        return new PortfolioDAOImpl(getEntityManager());
    }

    @Override
    public TransactionDAO getTransactionDAO() {
        return new TransactionDAOImpl(getEntityManager());
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDAOImpl(getEntityManager());
    }

    @Override
    public UserWatchCoinDAO getUserWatchCoinDAO() {
        return new UserWatchCoinDAOImpl(getEntityManager()) {
        };
    }
}