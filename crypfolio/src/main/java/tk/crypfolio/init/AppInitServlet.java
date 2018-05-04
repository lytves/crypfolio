package tk.crypfolio.init;

import tk.crypfolio.DAO.*;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.common.SettingsDB;
import tk.crypfolio.model.*;
import tk.crypfolio.parse.ParserAPI;
import tk.crypfolio.util.StringGenerator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Logger;

public class AppInitServlet implements ServletContextListener {

//    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AppInitServlet.class);

    private static final Logger logger = Logger.getLogger(AppInitServlet.class.getName());

    /*
     * application startup method
     * */
    public void contextInitialized(ServletContextEvent sce) {

        logger.info("CrypFolio application initializing.");
        logger.info("\n" +
                "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░\n" +
                "░░██████░░░░░░░░░░░░░░░░░░░░░░░░\n" +
                "░░██░░░░░░░░░░░░░░░░░░░░░░░░░░░░\n" +
                "░░██░░░░░░██████░░██░░██░░██████\n" +
                "░░██░░░░░░██░░░░░░██░░██░░██░░██\n" +
                "░░██░░░░░░██░░░░░░██░░██░░██░░██\n" +
                "░░██████░░██░░░░░░██████░░██████\n" +
                "░░░░░░░░░░░░░░░░░░░░░░██░░██░░░░\n" +
                "░░░░░░░░░░░░░░░░░░░░████░░██░░░░\n" +
                "░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░\n" +
                "░░██████░░░░░░░░░░░░░░░░░░░░░░░░\n" +
                "░░██░░░░░░░░░░░░██░░░░██░░░░░░░░\n" +
                "░░██░░░░██████░░██░░░░░░░░██████\n" +
                "░░████░░██░░██░░██░░░░██░░██░░██\n" +
                "░░██░░░░██░░██░░██░░░░██░░██░░██\n" +
                "░░██░░░░██████░░████░░██░░██████\n");

//        initTableCoins();

//        insertUser();

//        createPosition();

//        addWatchCoin();

//        addUserFollowee();
    }



    private void createPosition(){

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.Type.JPA);

        UserDAO uDAO = myFactory.getUserDAO();
        PortfolioDAO pDAO = myFactory.getPortfolioDAO();
        ItemDAO iDAO = myFactory.getItemDAO();
        CoinDAO cDAO = myFactory.getCoinDAO();

        UserEntity user = uDAO.getUserById(Long.valueOf(1));
        PortfolioEntity p = user.getPortfolio();
        CoinEntity coin = cDAO.getCoinById(Long.valueOf(1 + (int)(Math.random() * 1000)));

        System.out.println("p░r░i░v░a░t░e░ ░v░o░i░d░ ░c░r░e░a░t░e░I░t░e░m░");
        System.out.println("______________________________________________");

        //here create a new item or use one from DB
        ItemEntity item = new ItemEntity();
        item.setCoin(coin);
//        ItemEntity item = p.getItems().get(p.getItems().size() - 1);

//        p.addItem(item);
//        item.setCoin(coin);
        LocalDateTime date = LocalDateTime.now();

        PositionEntity pos = new PositionEntity(BigDecimal.valueOf(1 + (int)(Math.random() * 1000)),
                date, CurrencyType.ETH);
        item.addPosition(pos);
        p.addItem(item);

        System.out.println("░____________________portfolio before_______________________░");
        System.out.println(p);

        item = iDAO.updateItem(item);

//        p = pDAO.updatePortfolio(p);

        System.out.println("░____________________portfolio after_______________________░");
        System.out.println(p);

    }

    /*
    * Insert a user and its portfolio to the DB
    * */
    private void insertUser() {

        PortfolioEntity p = new PortfolioEntity();
        p.setName("mine");
        UserEntity user = new UserEntity(StringGenerator.setStringAlphanumeric(8), "passwordito");

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.APP_DB_TYPE);

        UserDAO uDAO = myFactory.getUserDAO();

        user.setPortfolio(p);
        uDAO.createUser(user);


        System.out.println(user);
        System.out.println("\n portfolio id: " + p.getId());
    }

    public void contextDestroyed(ServletContextEvent sce) {

    }

    /*
     * Insert all parsed coin to SettingsDB.APP_DB_TYPE - DataBase
     * */
    private void initTableCoins() {

        ParserAPI parserAPI = new ParserAPI();
        ArrayList<CoinEntity> listCoins = (ArrayList<CoinEntity>) parserAPI.parseAllCoin();

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.APP_DB_TYPE);

        CoinDAO cDAO = myFactory.getCoinDAO();

        for (CoinEntity coin : listCoins) {

            System.out.println(coin.getSymbol());
            cDAO.createCoin(coin);
        }

    }

    private void addUserFollowee() {

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.Type.JPA);
        UserDAO uDAO = myFactory.getUserDAO();
        UserEntity user = uDAO.getUserById(Long.valueOf(1));

        UserEntity userFollowee = uDAO.getUserById(Long.valueOf(2));

        user.addUserFollowee(userFollowee);

        uDAO.updateUser(user);
    }

    private void addWatchCoin() {

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.Type.JPA);
        UserDAO uDAO = myFactory.getUserDAO();
        UserEntity user = uDAO.getUserById(Long.valueOf(4));

        CoinDAO cDAO = myFactory.getCoinDAO();
        CoinEntity coin = cDAO.getCoinById(Long.valueOf(1 + (int)(Math.random() * 1000)));

//        UserWatchCoinsEntity uwc = new UserWatchCoinsEntity();

        user.addWatchCoin(coin, CurrencyType.ETH);
//        user.addWatchCoin(coin);

        System.out.println("░____________________user__________before_____________░");
        System.out.println(user);

        uDAO.updateUser(user);

        System.out.println("░____________________user_________after______________░");
        System.out.println(user);
    }
}
