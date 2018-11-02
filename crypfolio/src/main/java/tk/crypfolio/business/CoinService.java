package tk.crypfolio.business;

import tk.crypfolio.DAO.AbstractDAOFactory;
import tk.crypfolio.DAO.CoinDAO;
import tk.crypfolio.common.SettingsDB;
import tk.crypfolio.model.CoinEntity;

import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Transactional
@Stateless
public class CoinService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    public List<CoinEntity> getAllCoinsDB() {

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.APP_DB_TYPE);
        CoinDAO cDAO = myFactory.getCoinDAO();
        return cDAO.getAllCoins();
    }
}
