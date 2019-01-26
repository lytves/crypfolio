package tk.crypfolio.business;

import tk.crypfolio.DAO.AbstractDAOFactory;
import tk.crypfolio.DAO.PortfolioDAO;
import tk.crypfolio.common.SettingsDB;
import tk.crypfolio.model.PortfolioEntity;

import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.logging.Logger;

@Transactional
@Stateless
public class PortfolioService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    public PortfolioEntity updatePortfolioDB(PortfolioEntity portfolio){

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.APP_DB_TYPE);
        PortfolioDAO pDAO = myFactory.getPortfolioDAO();
        PortfolioEntity portfolioDB = pDAO.updatePortfolio(portfolio);

        return portfolioDB;
    }
}