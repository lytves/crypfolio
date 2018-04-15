package tk.crypfolio.DAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPADAOFactory extends AbstractDAOFactory {

    private EntityManager getEntityManager() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CrypFolioPersistenceUnit");
        return emf.createEntityManager();
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
    public PositionDAO getPositionDAO() {
        return new PositionDAOImpl(getEntityManager());
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDAOImpl(getEntityManager());
    }

    @Override
    public UserWatchCoinsDAO getUserWatchCoinsDAO() {
        return new UserWatchCoinsDAOImpl(getEntityManager());
    }
}
