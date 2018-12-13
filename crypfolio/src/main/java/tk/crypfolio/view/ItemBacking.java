package tk.crypfolio.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.primefaces.event.SelectEvent;
import tk.crypfolio.business.ApplicationContainer;
import tk.crypfolio.business.ItemService;
import tk.crypfolio.business.PortfolioService;
import tk.crypfolio.business.UserService;
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
    private UserService userService;

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

        if (getSelectedItem().getId() != null) {

            itemService.updateItemDB(getSelectedItem());

        } else {
            LOGGER.warn("Error on changing item showed currency!");
        }
    }

    public void doSubmitDeleteItem() {

        LOGGER.info("doSubmitDeleteItem____");

        if (getSelectedItem().getId() != null && activeUser.getUser().getPortfolio().getItems().contains(getSelectedItem())) {

            activeUser.getUser().getPortfolio().getItems().remove(getSelectedItem());

            // recount values (netcost) of portfolio
            activeUser.getUser().getPortfolio().recountNetCosts();

            // updates whole portfolio entity
            activeUser.setPortfolio(portfolioService.updatePortfolioDB(activeUser.getUser().getPortfolio()));

            // reread items (archived and notArchived) to update them in tabViews-datatables (portfolio and archive)
            // do it strictly only after updatePortfolioDB !!!
            portfolioBacking.init();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "The item has been deleted from your portfolio successfully.",
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
        return "?";
    }

    // is used to show bought date of the transaction
    public String getTransactionBoughtDate(TransactionEntity transactionEntity) {

        return transactionEntity.getBoughtDate().format(DateTimeFormatter.ofPattern(Constants.dateShortPattern, Constants.mainLocale));
    }

    public void doSubmitDeleteTransaction(TransactionEntity transactionEntity) {
        LOGGER.info("doSubmitDeleteTransaction.........");

        if (getSelectedItem().getTransactions().contains(transactionEntity)) {

            // search a current item index in the ArrayLst of transactions to then replace them
            int index = activeUser.getUser().getPortfolio().getItems().indexOf(getSelectedItem());

            // here we tested transaction && then remove from the item, but
            // remove only from this selectedItem instance,
            // and not from activeUser SessionScoped bean, so we will do it then with
            Boolean isTransactionValid = selectedItem.removeTransaction(transactionEntity);

            if (isTransactionValid) {

                // recount values (netcost) of portfolio
                activeUser.getUser().getPortfolio().recountNetCosts();

                activeUser.getUser().getPortfolio().getItems().set(index, getSelectedItem());

                // updates whole user entity
                activeUser.setUser(userService.updateUserDB(activeUser.getUser()));

                // updates whole portfolio entity
                // !!! - not working - causes removing 2 transactions from DB, dunno why !!!
                // activeUser.getUser().setPortfolio(portfolioService.updatePortfolioDB(activeUser.getUser().getPortfolio()));

                // reSetting itemBacking.selectedItem to keep it actual with DB
                for (ItemEntity item : activeUser.getUser().getPortfolio().getItems()) {

                    if (getSelectedItem().getId().equals(item.getId())) {

                        setSelectedItem(item);
                    }
                }

                // almost reSetting portfolio's datatables-tabViews in case some items was archived or desarchived
                portfolioBacking.init();

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "The transaction has been deleted successfully.",
                        ""));
                // exit from the method, thereby don't need to do a few "else"
                // to return FacesContext.getCurrentInstance().addMessage... depends of situation
                return;
            }
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error on deleting the transaction!",
                ""));
        LOGGER.warn("Error on deleting a transaction!");
    }
}