package tk.crypfolio.business;

import tk.crypfolio.DAO.AbstractDAOFactory;
import tk.crypfolio.DAO.CoinDAO;
import tk.crypfolio.common.SettingsDB;
import tk.crypfolio.model.CoinEntity;

import java.io.Serializable;
import java.util.List;

public class CoinService implements Serializable {

    private static final long serialVersionUID = 1L;

    public List<CoinEntity> getAllCoinsDB() {

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.APP_DB_TYPE);
        CoinDAO cDAO = myFactory.getCoinDAO();
        return cDAO.getAllCoins();
    }
}
