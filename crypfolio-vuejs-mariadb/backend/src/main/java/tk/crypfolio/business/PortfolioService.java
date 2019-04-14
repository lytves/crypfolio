package tk.crypfolio.business;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.DAO.AbstractDAOFactory;
import tk.crypfolio.DAO.PortfolioDAO;
import tk.crypfolio.common.SettingsDB;
import tk.crypfolio.model.PortfolioEntity;

import java.io.Serializable;

public class PortfolioService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LogManager.getLogger(PortfolioService.class);

    private PortfolioDAO getPortfolioDAO() {

        LOGGER.info("PortfolioService getPortfolioDAO()");
        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.APP_DB_TYPE);

        return myFactory.getPortfolioDAO();
    }

    public PortfolioEntity updatePortfolioDB(PortfolioEntity portfolio) {

        return getPortfolioDAO().updatePortfolio(portfolio);

    }

    /*
     * RESTful API methods to using with Vue.js
     *
     * */
    public PortfolioEntity getPortfolioDBById(Long Id) {

        PortfolioEntity portfolioDB = getPortfolioDAO().getPortfolioById(Id);

        if (portfolioDB != null) {

            return portfolioDB;
        }
        return null;
    }

}