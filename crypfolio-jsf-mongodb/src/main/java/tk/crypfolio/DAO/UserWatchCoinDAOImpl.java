package tk.crypfolio.DAO;

import tk.crypfolio.business.exception.AppDAOException;
import tk.crypfolio.model.UserWatchCoinEntity;

import javax.persistence.EntityManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserWatchCoinDAOImpl extends DAOImpl<Long, UserWatchCoinEntity> implements UserWatchCoinDAO {

    private static final Logger LOGGER = Logger.getLogger(UserWatchCoinDAOImpl.class.getName());

    protected UserWatchCoinDAOImpl(EntityManager em) {
        super(em, UserWatchCoinEntity.class);
    }

    @Override
    public UserWatchCoinEntity updateUserWatchCoinEntity(UserWatchCoinEntity uwc) {

        try {
            return this.update(uwc);

        } catch (AppDAOException ex) {
            LOGGER.log(Level.WARNING, ex.toString());
        }
        return uwc;
    }
}