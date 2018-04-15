package tk.crypfolio.DAO;

import tk.crypfolio.domain.PortfolioEntity;

import javax.persistence.EntityManager;

public class PortfolioDAOImpl extends DAOImpl<Long, PortfolioEntity> implements PortfolioDAO {

	protected PortfolioDAOImpl(EntityManager em) {
		super(em, PortfolioEntity.class);
	}

	@Override
	public PortfolioEntity getPortfolioById(Long id) {
		return this.getById(id);
	}

}
