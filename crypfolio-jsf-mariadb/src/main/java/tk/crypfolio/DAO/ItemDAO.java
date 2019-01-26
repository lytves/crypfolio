package tk.crypfolio.DAO;

import tk.crypfolio.model.ItemEntity;

public interface ItemDAO {

	public ItemEntity getItemById(Long id);

	public void createItem(ItemEntity i);

    public ItemEntity updateItem(ItemEntity i);
}
