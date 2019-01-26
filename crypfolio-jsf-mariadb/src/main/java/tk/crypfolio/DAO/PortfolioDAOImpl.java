package tk.crypfolio.DAO;

import tk.crypfolio.business.exception.AppDAOException;
import tk.crypfolio.model.PortfolioEntity;

import javax.persistence.EntityManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PortfolioDAOImpl extends DAOImpl<Long, PortfolioEntity> implements PortfolioDAO {

	private static final Logger LOGGER = Logger.getLogger(PortfolioDAOImpl.class.getName());

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

		try {
			return this.update(portfolio);

		} catch (AppDAOException ex) {
			LOGGER.log(Level.WARNING, ex.toString());
		}
		return portfolio;
    }
}