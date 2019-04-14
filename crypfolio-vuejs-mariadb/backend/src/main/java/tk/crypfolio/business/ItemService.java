package tk.crypfolio.business;

import tk.crypfolio.DAO.AbstractDAOFactory;
import tk.crypfolio.DAO.ItemDAO;
import tk.crypfolio.common.SettingsDB;
import tk.crypfolio.model.ItemEntity;

import java.io.Serializable;

public class ItemService implements Serializable {

    private static final long serialVersionUID = 1L;

    public ItemEntity updateItemDB(ItemEntity itemEntity) {

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.APP_DB_TYPE);
        ItemDAO iDAO = myFactory.getItemDAO();
        ItemEntity itemDB = iDAO.updateItem(itemEntity);

        return itemDB;
    }
}