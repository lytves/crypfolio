package tk.crypfolio.DAO;

import tk.crypfolio.domain.ItemEntity;

public interface ItemDAO {

	public ItemEntity getItemById(Long id);

	public void createItem(ItemEntity i);

    public ItemEntity updateItem(ItemEntity i);
}
