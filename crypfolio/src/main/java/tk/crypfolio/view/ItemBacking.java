package tk.crypfolio.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.primefaces.event.SelectEvent;
import tk.crypfolio.business.ApplicationContainer;
import tk.crypfolio.business.ItemService;
import tk.crypfolio.business.PortfolioService;
import tk.crypfolio.common.Constants;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.model.CoinEntity;
import tk.crypfolio.model.ItemEntity;
import tk.crypfolio.model.TransactionEntity;
import tk.crypfolio.util.MathRounders;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Named
@ViewScoped
public class ItemBacking implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(PortfolioBacking.class);

    // application scoped
    @Inject
    private ApplicationContainer applicationContainer;

    // session scoped
    @Inject
    private ActiveUser activeUser;

    // view scoped
    @Inject
    private PortfolioBacking portfolioBacking;

    // stateless business
    @Inject
    private PortfolioService portfolioService;

    // stateless business
    @Inject
    private ItemService itemService;

    private ItemEntity selectedItem;

    // it's used in p:selectOneButton form
    private CurrencyType[] currencies;

    private String itemImageID = "https://s2.coinmarketcap.com/static/img/coins/32x32/1.png";

    @PostConstruct
    public void init() {
        LOGGER.info("ItemBacking @PostConstruct");

        // initialize selectedItem to avoid "itemBacking.selectedItem.showedCurrency" error in the jsf-view
        setSelectedItem(new ItemEntity(new CoinEntity()));
    }

    /*
     * * * * * * * * * * * * * * * * * * * * * Setters and Getters * * * * * * * * * * * * * * * * * * * * *
     * */
    public ActiveUser getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(ActiveUser activeUser) {
        this.activeUser = activeUser;
    }

    public ItemEntity getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(ItemEntity selectedItem) {
        this.selectedItem = selectedItem;
    }

    public CurrencyType[] getCurrencies() {
        return CurrencyType.values();
    }

    public String getItemImageID() {
        return itemImageID;
    }

    public void setItemImageID(String itemImageID) {
        this.itemImageID = itemImageID;
    }

    /*
     * * * * * * * * * * * * * * * * * * * * * Bean's methods * * * * * * * * * * * * * * * * * * * * *
     * */
    public void onRowSelect(SelectEvent event) {

        LOGGER.info("onRowSelect: " + event.getObject().toString());

        // to avoid "html/js console error" with no image at the page loaded
        String imageID = String.valueOf(((ItemEntity) event.getObject()).getCoin().getId());
        setItemImageID("https://s2.coinmarketcap.com/static/img/coins/32x32/" + imageID + ".png");
    }

    // is used to avoid rowKey=null
    public int getRowKey(Integer id) {

        LOGGER.info("getRowKey " + id);
        return id != null ? id : -1;
    }

    public void changeItemShowedCurrency() {

        if (selectedItem != null) {

            itemService.updateItemDB(selectedItem);

        } else {
            LOGGER.warn("Error on changing item showed currency!");
        }
    }

    public void doSubmitDeleteItem() {

        LOGGER.info("doSubmitDeleteItem____");
        LOGGER.warn(selectedItem.toString());

        if (selectedItem != null && activeUser.getUser().getPortfolio().getItems().contains(selectedItem)) {

            activeUser.getUser().getPortfolio().getItems().remove(selectedItem);

            // recount values (netcost) of portfolio
            activeUser.getUser().getPortfolio().recountNetCosts();

            // updates whole portfolio entity
            activeUser.setPortfolio(portfolioService.updatePortfolioDB(activeUser.getUser().getPortfolio()));

            // reread items to update and show in porftolio and archive datatables (after updatePortfolioDB!!!)
            portfolioBacking.init();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "The item has been deleted from your portfolio successully.",
                    ""));
        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error on deleting the item from portfolio!",
                    ""));
            LOGGER.warn("Error on deleting an item from user's portfolio!");
        }
    }

    public BigDecimal getCoinCirculatingSupply(@NotNull ItemEntity itemEntity) {

        for (Map.Entry<Long, Map<String, Double>> entry : applicationContainer.getAllCoinsByTickerAdditionalData().entrySet()) {

            Long key = entry.getKey();
            Map<String, Double> value = entry.getValue();

            if (key.equals(itemEntity.getCoin().getId())) {
                return MathRounders.roundBigDecimalToTwoDecimal(BigDecimal.valueOf(value.get("circulating_supply")));
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getCoinTotalSupply(@NotNull ItemEntity itemEntity) {

        for (Map.Entry<Long, Map<String, Double>> entry : applicationContainer.getAllCoinsByTickerAdditionalData().entrySet()) {

            Long key = entry.getKey();
            Map<String, Double> value = entry.getValue();

            if (key.equals(itemEntity.getCoin().getId())) {
                return MathRounders.roundBigDecimalToTwoDecimal(BigDecimal.valueOf(value.get("total_supply")));
            }
        }
        return BigDecimal.ZERO;
    }

    public String getCoinCmcRank(@NotNull ItemEntity itemEntity) {

        for (Map.Entry<Long, Map<String, Double>> entry : applicationContainer.getAllCoinsByTickerAdditionalData().entrySet()) {

            Long key = entry.getKey();
            Map<String, Double> value = entry.getValue();

            if (key.equals(itemEntity.getCoin().getId())) {
                return String.valueOf(value.get("cmc_rank").intValue());
            }
        }
        return "-";
    }

    // is used to show bought date of the transaction
    public String getTransactionBoughtDate(TransactionEntity transactionEntity) {

        return transactionEntity.getBoughtDate().format(DateTimeFormatter.ofPattern(Constants.dateShortPattern, Constants.mainLocale));
    }
}