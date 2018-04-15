package tk.crypfolio.DAO;

import tk.crypfolio.domain.ItemEntity;

import javax.persistence.EntityManager;

public class ItemDAOImpl extends DAOImpl<Long, ItemEntity> implements ItemDAO {

	protected ItemDAOImpl(EntityManager em) {
		super(em, ItemEntity.class);
	}

	@Override
	public ItemEntity getItemById(Long id) {
		return this.getById(id);
	}

}
