package tk.crypfolio.DAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JPADAOFactory extends AbstractDAOFactory {

    private static final Logger logger = Logger.getLogger(JPADAOFactory.class.getName());

    private volatile static EntityManagerFactory emf;

    private static EntityManagerFactory getInstanceEntityManagerFactory() {

        if (emf == null) {
            synchronized (EntityManagerFactory.class) {
                if (emf == null) {
                    emf = Persistence.createEntityManagerFactory("CrypFolioPersistenceUnit");
                }
            }
        }

        return emf;
    }

    private static EntityManager getEntityManager() {

        try {
            return getInstanceEntityManagerFactory().createEntityManager();
        } catch (Exception e){
            logger.log(Level.WARNING, e.getMessage());
        }

        return null;
    }

    @Override
    public CoinDAO getCoinDAO() {return new CoinDAOImpl(getEntityManager());}

    @Override
    public ItemDAO getItemDAO() {
        return new ItemDAOImpl(getEntityManager());
    }

    @Override
    public PortfolioDAO getPortfolioDAO() {
        return new PortfolioDAOImpl(getEntityManager());
    }

    @Override
    public PositionDAO getPositionDAO() {
        return new PositionDAOImpl(getEntityManager());
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
