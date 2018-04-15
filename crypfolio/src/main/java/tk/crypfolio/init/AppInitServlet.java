package tk.crypfolio.init;

import tk.crypfolio.DAO.AbstractDAOFactory;
import tk.crypfolio.DAO.CoinDAO;
import tk.crypfolio.common.SettingsDB;
import tk.crypfolio.domain.CoinEntity;
import tk.crypfolio.parse.ParserAPI;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppInitServlet implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(AppInitServlet.class.getName());

    /*
    * application startup method
    * */
    public void contextInitialized(ServletContextEvent sce) {

        logger.log(Level.INFO, "CrypFolio application initializing.");

        initTableCoins();

    }


    public void contextDestroyed(ServletContextEvent sce) {

    }

    /*
    * Insert all parsed coin to SettingsDB.APP_DB_TYPE - DataBase
    * */
    private void initTableCoins(){

        ParserAPI parserAPI = new ParserAPI();
        ArrayList<CoinEntity> listCoins = (ArrayList<CoinEntity>) parserAPI.parseAllCoin();

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.APP_DB_TYPE);

        CoinDAO cDAO = myFactory.getCoinDAO();

        for (CoinEntity coin: listCoins){

            cDAO.createCoin(coin);
        }

    }

}
