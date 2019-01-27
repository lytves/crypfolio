package tk.crypfolio.DAO;

import tk.crypfolio.common.SettingsDB;

public abstract class AbstractDAOFactory {

    /*
    * DAOFactory getter
    * */
    public static AbstractDAOFactory getDAOFactory(SettingsDB.Type t) {

        switch (t) {
            case JPA:
                return new JPADAOFactory();

            case NoSQL:
                return new NoSQLDAOFactory();
        }
        return null;
    }

    /*
     * define the getDAO method for all entities here
     * */
    public abstract PortfolioDAO getPortfolioDAO();

    public abstract TransactionDAO getTransactionDAO();

    public abstract UserDAO getUserDAO();

    public abstract CoinDAO getCoinDAO();
}