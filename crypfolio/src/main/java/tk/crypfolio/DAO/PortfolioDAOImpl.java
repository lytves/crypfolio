package tk.crypfolio.DAO;

import tk.crypfolio.model.PortfolioEntity;

import javax.persistence.EntityManager;

public class PortfolioDAOImpl extends DAOImpl<Long, PortfolioEntity> implements PortfolioDAO {

	protected PortfolioDAOImpl(EntityManager em) {
		super(em, PortfolioEntity.class);
	}

	@Override
	public PortfolioEntity getPortfolioById(Long id) {
		return this.getById(id);
	}

	@Override
	public void createPortfolio(PortfolioEntity portfolio) {
        this.create(portfolio);
	}

    @Override
    public void deletePortfolio(PortfolioEntity portfolio) {
        this.delete(portfolio);
    }

    @Override
    public PortfolioEntity updatePortfolio(PortfolioEntity portfolio) {
        return this.update(portfolio);
    }
}
