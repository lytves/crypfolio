package tk.crypfolio.DAO;

import tk.crypfolio.business.exception.AppDAOException;
import tk.crypfolio.model.ItemEntity;

import javax.persistence.EntityManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemDAOImpl extends DAOImpl<Long, ItemEntity> implements ItemDAO {

    private static final Logger LOGGER = Logger.getLogger(ItemDAOImpl.class.getName());

    protected ItemDAOImpl(EntityManager em) {
        super(em, ItemEntity.class);
    }

    @Override
    public ItemEntity getItemById(Long id) {
        return this.getById(id);
    }

    @Override
    public void createItem(ItemEntity item) {
        this.create(item);
    }

    @Override
    public ItemEntity updateItem(ItemEntity item) {

        try {
            return this.update(item);

        } catch (AppDAOException ex) {
            LOGGER.log(Level.WARNING, ex.toString());
        }
        return item;
    }
}