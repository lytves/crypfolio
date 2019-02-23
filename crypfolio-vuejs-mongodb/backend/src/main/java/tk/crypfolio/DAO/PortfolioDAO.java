package tk.crypfolio.DAO;

import tk.crypfolio.model.PortfolioEntity;

public interface PortfolioDAO {

	PortfolioEntity getPortfolioById(Long id);

	void createPortfolio(PortfolioEntity p);

	void deletePortfolio(PortfolioEntity p);

	PortfolioEntity updatePortfolio(PortfolioEntity p);
}
