package tk.crypfolio.business;

import tk.crypfolio.DAO.AbstractDAOFactory;
import tk.crypfolio.DAO.PortfolioDAO;
import tk.crypfolio.common.SettingsDB;
import tk.crypfolio.model.PortfolioEntity;

import java.io.Serializable;

public class PortfolioService implements Serializable {

    private static final long serialVersionUID = 1L;

    public PortfolioEntity updatePortfolioDB(PortfolioEntity portfolio) {

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.APP_DB_TYPE);
        PortfolioDAO pDAO = myFactory.getPortfolioDAO();
        return pDAO.updatePortfolio(portfolio);
    }
}