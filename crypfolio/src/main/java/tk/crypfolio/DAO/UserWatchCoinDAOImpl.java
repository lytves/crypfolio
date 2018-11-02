package tk.crypfolio.DAO;

import tk.crypfolio.model.UserWatchCoinEntity;

import javax.persistence.EntityManager;

public class UserWatchCoinDAOImpl extends DAOImpl<Long, UserWatchCoinEntity> implements UserWatchCoinDAO {

    protected UserWatchCoinDAOImpl(EntityManager em) {
        super(em, UserWatchCoinEntity.class);
    }

    @Override
    public UserWatchCoinEntity updateUserWatchCoinEntity(UserWatchCoinEntity uwc) {
        return this.update(uwc);
    }
}
