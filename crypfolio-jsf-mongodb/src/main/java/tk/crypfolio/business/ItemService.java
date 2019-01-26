package tk.crypfolio.business;

import tk.crypfolio.DAO.AbstractDAOFactory;
import tk.crypfolio.DAO.ItemDAO;
import tk.crypfolio.common.SettingsDB;
import tk.crypfolio.model.ItemEntity;

import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.logging.Logger;

@Transactional
@Stateless
public class ItemService implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(ItemService.class.getName());

    public ItemEntity updateItemDB(ItemEntity itemEntity) {

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.APP_DB_TYPE);
        ItemDAO iDAO = myFactory.getItemDAO();
        ItemEntity itemDB = iDAO.updateItem(itemEntity);

        return itemDB;
    }
}