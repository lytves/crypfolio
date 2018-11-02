package tk.crypfolio.DAO;

import tk.crypfolio.common.SettingsDB;

public abstract class AbstractDAOFactory {

//    public enum TYPE {JPA,XML}

    //Definimos un getDAO para cada entidad
    public abstract CoinDAO getCoinDAO();

    public abstract ItemDAO getItemDAO();

    public abstract PortfolioDAO getPortfolioDAO();

    public abstract PositionDAO getPositionDAO();

    public abstract UserDAO getUserDAO();

    public abstract UserWatchCoinDAO getUserWatchCoinDAO();

    @SuppressWarnings("incomplete-switch")
    public static AbstractDAOFactory getDAOFactory(SettingsDB.Type t){

        switch(t){
            case JPA:
                return new JPADAOFactory();
            /*case XML:
                return new XMLDAOFactory();*/
        }
        return null;
    }
}
