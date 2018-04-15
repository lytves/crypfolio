package tk.crypfolio.DAO;

import tk.crypfolio.domain.PositionEntity;

import javax.persistence.EntityManager;

public class PositionDAOImpl extends DAOImpl<Long, PositionEntity> implements PositionDAO {

	protected PositionDAOImpl(EntityManager em) {
		super(em, PositionEntity.class);
	}

	@Override
	public PositionEntity getPositionById(Long id) {
		return this.getById(id);
	}

}
