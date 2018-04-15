package tk.crypfolio.DAO;

import tk.crypfolio.domain.UserWatchCoinsEntity;

import javax.persistence.EntityManager;

public class UserWatchCoinsDAOImpl extends DAOImpl<Long, UserWatchCoinsEntity> implements UserWatchCoinsDAO {

	protected UserWatchCoinsDAOImpl(EntityManager em) {
		super(em, UserWatchCoinsEntity.class);
	}

	@Override
	public UserWatchCoinsEntity getUserWatchCoinsById(Long id) {
		return this.getById(id);
	}

}
