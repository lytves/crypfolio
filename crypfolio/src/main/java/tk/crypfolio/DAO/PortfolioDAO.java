package tk.crypfolio.DAO;

import tk.crypfolio.domain.PortfolioEntity;

public interface PortfolioDAO {

	public PortfolioEntity getPortfolioById(Long id);

	public void createPortfolio(PortfolioEntity p);

	public void deletePortfolio(PortfolioEntity p);
}
