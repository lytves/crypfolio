package tk.crypfolio.DAO;

import tk.crypfolio.model.ItemEntity;

import javax.persistence.EntityManager;

public class ItemDAOImpl extends DAOImpl<Long, ItemEntity> implements ItemDAO {

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
        return this.update(item);
    }
}
