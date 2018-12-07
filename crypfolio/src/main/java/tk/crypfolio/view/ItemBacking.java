package tk.crypfolio.view;

import org.primefaces.event.SelectEvent;
import tk.crypfolio.model.ItemEntity;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class ItemBacking implements Serializable {

    private ItemEntity selectedItem;

    public ItemEntity getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(ItemEntity selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void init() {
    }

    public void onRowSelect(SelectEvent event) {
        System.out.println(((ItemEntity) event.getObject()).getId().toString());
    }

    // is used to avoid rowKey=null
    public int getRowKey(Integer id) {
        return id != null ? id : -1;
    }
}