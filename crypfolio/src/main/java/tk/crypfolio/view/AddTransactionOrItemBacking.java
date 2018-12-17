package tk.crypfolio.view;

import org.apache.commons.lang.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tk.crypfolio.business.ApplicationContainer;
import tk.crypfolio.business.PortfolioService;
import tk.crypfolio.common.Constants;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.common.Settings;
import tk.crypfolio.model.CoinEntity;
import tk.crypfolio.model.ItemEntity;
import tk.crypfolio.model.TransactionEntity;
import tk.crypfolio.parse.ParserAPI;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class AddTransactionOrItemBacking implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(AddTransactionOrItemBacking.class);

    // application scoped
    @Inject
    private ApplicationContainer applicationContainer;

    // session scoped
    @Inject
    private ActiveUser activeUser;

    // view scoped
    @Inject
    private PortfolioBacking portfolioBacking;

    // view scoped
    @Inject
    private ItemBacking itemBacking;

    // stateless business
    @Inject
    private PortfolioService portfolioService;

    //it's used in p:selectOneMenu form
    private CurrencyType[] currencies;

    private CoinEntity coinTemp;

    private ItemEntity itemTemp;

    private TransactionEntity transactionTemp;

    // temporary transaction that is used only in the case
    // user intent to edit a transaction from itemDetails modal window
    private TransactionEntity transactionEditedOld;

    // here is using a temporary price coz we steel don't know
    // which transaction currency will used like main
    // so can't write directly to some TransactionEntity property
    private BigDecimal transactionPriceTemp;

    // see comment above p.s.: or won't use a separate variable, don't know yet))
    // this variable is used only to show value on the jsf-page,
    // in the business logic don't use it
    private BigDecimal transactionTotalTemp;

    // is used only in the function autoRecalculateTransactionInputData(...)
    // for recount or price or amount, depends of the last user's entered input form
    private String lastInputFormFocused;

    @PostConstruct
    private void init() {
        LOGGER.info("AddTransactionOrItemBacking @PostConstruct");
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

    public CurrencyType[] getCurrencies() {
        return CurrencyType.values();
    }

    public CoinEntity getCoinTemp() {
        return coinTemp;
    }

    public void setCoinTemp(CoinEntity coinTemp) {
        this.coinTemp = coinTemp;
    }

    public ItemEntity getItemTemp() {
        return itemTemp;
    }

    public void setItemTemp(ItemEntity itemTemp) {
        this.itemTemp = itemTemp;
    }

    public TransactionEntity getTransactionTemp() {
        return transactionTemp;
    }

    public void setTransactionTemp(TransactionEntity transactionTemp) {
        this.transactionTemp = transactionTemp;
    }

    public TransactionEntity getTransactionEditedOld() {
        return transactionEditedOld;
    }

    public void setTransactionEditedOld(TransactionEntity transactionEditedOld) {
        this.transactionEditedOld = transactionEditedOld;
    }

    public BigDecimal getTransactionPriceTemp() {
        return transactionPriceTemp;
    }

    public void setTransactionPriceTemp(BigDecimal transactionPriceTemp) {
        this.transactionPriceTemp = transactionPriceTemp;
    }

    public BigDecimal getTransactionTotalTemp() {
        return transactionTotalTemp;
    }

    public void setTransactionTotalTemp(BigDecimal transactionTotalTemp) {
        this.transactionTotalTemp = transactionTotalTemp;
    }

    @Contract(pure = true)
    private String getLastInputFormFocused() {
        return lastInputFormFocused;
    }

    private void setLastInputFormFocused(String lastInputFormFocused) {
        this.lastInputFormFocused = lastInputFormFocused;
    }

    /*
     * * * * * * * * * * * * * * * * * * * * * Bean's methods * * * * * * * * * * * * * * * * * * * * *
     * */
    // is used to show current date in the Date input placeholder
    public String getDateNowPlaceholder() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(Constants.datePattern, Constants.mainLocale));
    }

    public void getItemAllAmount() {
        transactionTemp.setAmount(itemTemp.getAmount());

        autoRecalculateTransactionInputData("amount");
    }

    // autocalculating add-item-transaction form variables
    public void autoRecalculateTransactionInputData(String changedInputForm) {
        LOGGER.warn("autoRecalculateTransactionInputData__________________________");

        switch (changedInputForm) {

            case "amount":

                if (transactionTemp.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                    setTransactionTotalTemp(BigDecimal.ZERO);
                } else if (transactionPriceTemp.compareTo(BigDecimal.ZERO) >= 1) {
                    setTransactionTotalTemp(transactionTemp.getAmount().multiply(transactionPriceTemp)
                            .setScale(8, RoundingMode.DOWN));
                }
                setLastInputFormFocused("amount");
                break;

            case "price":

                if (transactionTemp.getAmount() != null && transactionTemp.getAmount().compareTo(BigDecimal.ZERO) >= 1) {

                    setTransactionTotalTemp(transactionTemp.getAmount().multiply(transactionPriceTemp)
                            .setScale(8, RoundingMode.DOWN));
                }
                setLastInputFormFocused("price");
                break;

            case "total":

                if (transactionPriceTemp.compareTo(BigDecimal.ZERO) == 0) {
                    getTransactionTemp().setAmount(BigDecimal.ZERO);
                } else if (("amount").equals(getLastInputFormFocused())) {

                    try {
                        setTransactionPriceTemp(getTransactionTotalTemp()
                                .divide(transactionTemp.getAmount(), 8, BigDecimal.ROUND_HALF_DOWN));

                    } catch (ArithmeticException ex) {
                        LOGGER.warn(ex.toString());
                    }

                } else if (("price").equals(getLastInputFormFocused())) {

                    try {
                        getTransactionTemp().setAmount(transactionTotalTemp
                                .divide(transactionPriceTemp, 8, BigDecimal.ROUND_HALF_DOWN));
                    } catch (ArithmeticException ex) {
                        LOGGER.warn(ex.toString());
                    }
                }
                break;
        }
    }

    public void setActualTransactionPriceTemp() {
        transactionPriceTemp = portfolioBacking.getCoinPrice(itemTemp.getCoin(), transactionTemp.getBoughtCurrency());

        autoRecalculateTransactionInputData("price");
    }

    public void transactionAddFormReset() {
        LOGGER.info("PortfolioBacking.transactionAddFormReset");
        // executing every time when add-transaction modal window is closed
        // or Reset button pushed
        if (coinTemp != null) setCoinTemp(null);
        if (itemTemp != null) setItemTemp(null);
        if (transactionTemp != null) setTransactionTemp(null);
        if (transactionEditedOld != null) setTransactionEditedOld(null);
        if (transactionPriceTemp != null) setTransactionPriceTemp(null);
        if (transactionTotalTemp != null) setTransactionTotalTemp(null);
    }

    public void createEntitiesForAddition() {
        LOGGER.info("PortfolioBacking.createEntitiesForAddition");

        // if itemTemp is still == null, it means that user have used autocomplete to choose coin
        // and vice versa itemTemp != null, means user have chosen from the list of existing items,
        // coz on select from the list of existing items works ... selection="#{addTransactionOrItemBacking.itemTemp}"
        if (getItemTemp() == null && getCoinTemp() != null) {

            setItemTemp(new ItemEntity(coinTemp));

            for (ItemEntity item : activeUser.getUser().getPortfolio().getItems()) {

                if (item.getCoin().getId().equals(coinTemp.getId())) {
                    itemTemp = item;
                    break;
                }
            }

            // user have chosen a coin from the list of existing items: ... selection="#{addTransactionOrItemBacking.itemTemp}"
        } else if (getItemTemp() != null) {

            // also should do this to update condition in the jsf-view: rendered="#{addTransactionOrItemBacking.coinTemp eq null}
            setCoinTemp(itemTemp.getCoin());

            // user have clicked "add transaction" from item's details modal window,
            // so in the itemBacking we already have itemEntity in the selectedItem attribute
        } else if (itemBacking.getSelectedItem().getId() != null) {

            setItemTemp(itemBacking.getSelectedItem());

            // also should do this to update condition in the jsf-view: rendered="#{addTransactionOrItemBacking.coinTemp eq null}
            setCoinTemp(itemBacking.getSelectedItem().getCoin());
        }

        setTransactionTemp(new TransactionEntity());

        // to set initial item's coin price
        setTransactionPriceTemp(portfolioBacking.getCoinPrice(itemTemp.getCoin(), transactionTemp.getBoughtCurrency()));
    }

    public void createEntitiesForEdition(TransactionEntity transactionEntity) {
        LOGGER.info("PortfolioBacking.createEntitiesForAddition(transactionEntity)");

        setItemTemp(itemBacking.getSelectedItem());

        // also should do this to update condition in the jsf-view: rendered="#{addTransactionOrItemBacking.coinTemp eq null}
        setCoinTemp(itemBacking.getSelectedItem().getCoin());

        // as transactionTemp is used a copy(clone) of the transaction to edit
        setTransactionTemp((TransactionEntity) SerializationUtils.clone(transactionEntity));

        // and original transaction to edit we also temporary save to compare then
        // with the new one in doSubmitEditTransaction() and ItemEntity.editTransaction!!!
        setTransactionEditedOld(transactionEntity);

        // to set initial price of the current transaction, depends of the currency
        setTransactionPriceTemp(transactionEntity.gePriceByCurrentCurrency());

        // to set total price of the current transaction, depends of the currency too
        setTransactionTotalTemp(transactionTemp.getTotalPriceByCurrency());
    }

    /**
     * Method for submit "Add Transaction" form
     */
    public void doSubmitAddTransaction() {
        LOGGER.info("PortfolioBacking.doSubmitAddTransaction");

        // checking if all entered values are valid
        if (itemTemp == null || transactionTemp == null
                || transactionTemp.getAmount().compareTo(BigDecimal.ZERO) == 0 || transactionTemp.getAmount() == null
                || isBigDecimalVaildForDB(transactionTemp.getAmount())
                || transactionPriceTemp == null || transactionPriceTemp.compareTo(BigDecimal.ZERO) == 0
                || isBigDecimalVaildForDB(transactionPriceTemp)
                || isBigDecimalVaildForDB(transactionTemp.getAmount().multiply(transactionPriceTemp))) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error on processing your transaction! Some of entered values are not valid!",
                    ""));

        } else if (("SELL").equals(transactionTemp.getType().getType())
                && transactionTemp.getAmount().compareTo(itemTemp.getAmount()) >= 1) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error on processing your transaction! You can't sell more coins than you have!",
                    ""));

        } else {

            // set today date by default
            if (transactionTemp.getBoughtDate() == null) {
                transactionTemp.setBoughtDate(LocalDate.now());
            }

            // recount prices in all currencies by request historical prices by API
            recountTransactionPricesByHistoricalPricesAPI(transactionTemp, transactionPriceTemp);

            // also recount values (net costs, average prices) of item
            itemTemp.addTransaction(transactionTemp);

            // if it's new item in the portfolio
            if (itemTemp.getId() == null) {

                itemTemp.setShowedCurrency(transactionTemp.getBoughtCurrency());

                activeUser.getUser().getPortfolio().addItem(itemTemp);
            }

            // recount values (netcost) of portfolio
            activeUser.getUser().getPortfolio().recountNetCosts();

            // updates whole portfolio entity
            activeUser.setPortfolio(portfolioService.updatePortfolioDB(activeUser.getUser().getPortfolio()));

            // reread items to update and show in porftolio and archive datatables (after updatePortfolioDB!!!)
            portfolioBacking.init();

            // if itemBacking.getSelectedItem().getId() exists, it means operation was come
            // from item's details modal window and we should update itemBacking.selectedItem
            // after update DB and activeUser too
            if (itemBacking.getSelectedItem().getId() != null) {

                for (ItemEntity item : activeUser.getUser().getPortfolio().getItems()) {

                    if (itemBacking.getSelectedItem().getId().equals(item.getId())) {

                        itemBacking.setSelectedItem(item);
                    }
                }
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "The transaction has been processed successfully.",
                    ""));
        }
    }

    /**
     * Method for submit form "Edit Transaction" from modal window ItemDetails:transactions
     * see similar method itemBacking.doSubmitDeleteTransaction(TransactionEntity transactionEntity)
     */
    public void doSubmitEditTransaction() {
        LOGGER.info("PortfolioBacking.doSubmitEditTransaction");

        // checking if all entered values are valid
        if (itemTemp == null || transactionTemp == null
                || transactionTemp.getAmount().compareTo(BigDecimal.ZERO) == 0 || transactionTemp.getAmount() == null
                || isBigDecimalVaildForDB(transactionTemp.getAmount())
                || transactionPriceTemp == null || transactionPriceTemp.compareTo(BigDecimal.ZERO) == 0
                || isBigDecimalVaildForDB(transactionPriceTemp)
                || isBigDecimalVaildForDB(transactionTemp.getAmount().multiply(transactionPriceTemp))
                || (!transactionEditedOld.getType().equals(transactionTemp.getType()))) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error on processing your transaction! Some of entered values are not valid!",
                    ""));

        } else if (("SELL").equals(transactionTemp.getType().getType())
                && transactionTemp.getAmount().compareTo(itemTemp.getAmount()) >= 1) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error on processing your transaction! You can't sell more coins than you have!",
                    ""));

        } else {

            if (itemBacking.getSelectedItem().getTransactions().contains(transactionEditedOld)) {

                // recount prices in all currencies by request historical prices by API
                recountTransactionPricesByHistoricalPricesAPI(transactionTemp, transactionPriceTemp);

                // search a current item index in the ArrayLst of transactions to then replace them
                int index = activeUser.getUser().getPortfolio().getItems().indexOf(itemBacking.getSelectedItem());

                // here we tested transaction && then edit it in the itemEntity, but
                // editing occurs only in this selectedItem instance, and not in the activeUser SessionScoped bean,
                // so we will do it by replace this old item in SessionScoped bean with the new one by do
                // activeUser.getUser().getPortfolio().getItems().set(index, getSelectedItem());
                Boolean isTransactionValid = itemBacking.getSelectedItem().editTransaction(transactionEditedOld, transactionTemp);

                if (isTransactionValid) {

                    // recount values (netcost) of portfolio
                    activeUser.getUser().getPortfolio().recountNetCosts();

                    // replace the item after transaction deleting in the SessionScoped bean activeUser
                    activeUser.getUser().getPortfolio().getItems().set(index, itemBacking.getSelectedItem());

                    // updates whole user entity
                    // activeUser.setUser(userService.updateUserDB(activeUser.getUser()));

                    // updates whole portfolio entity
                    // !!! - not working - causes removing 2 transactions from DB, dunno why !!!
                    activeUser.getUser().setPortfolio(portfolioService.updatePortfolioDB(activeUser.getUser().getPortfolio()));

                    // reSetting itemBacking.selectedItem to keep it actual with DB
                    for (ItemEntity item : activeUser.getUser().getPortfolio().getItems()) {

                        if (itemBacking.getSelectedItem().getId().equals(item.getId())) {

                            itemBacking.setSelectedItem(item);
                        }
                    }

                    // almost reSetting portfolio's datatables-tabViews in case some items was archived or desarchived
                    portfolioBacking.init();

                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "The transaction has been edited successfully.",
                            ""));
                    // exit from the method, thereby don't need to do a few "else"
                    // to return FacesContext.getCurrentInstance().addMessage... depends of situation
                    return;
                }

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Error on editing the transaction!",
                        ""));
                LOGGER.warn("Error on editing a transaction!");
            }
        }
    }

    /*
    * universal method to recount transaction prices in all currencies depends of the bought currency and
    * with respect to the historical prices of USD/EUR/BTC/ETH
    * */
    private void recountTransactionPricesByHistoricalPricesAPI(TransactionEntity transaction, BigDecimal typedPrice) {

        // makes API request to obtain history USD/EUR/BTC/ETH prices
        Map<String, Double> bitcoinHistoricalPrice = ParserAPI.parseBitcoiHistoricalPrice(transaction.getBoughtDate());

        // recounts and sets prices in all currencies of the transaction
        Double coefficientMultiplier;

        switch (transaction.getBoughtCurrency().getCurrency()) {

            case "USD":

                // USD
                transaction.setBoughtPriceUsd(typedPrice);

                // EUR
                coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("USD");

                transaction.setBoughtPriceEur(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // BTC
                coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("USD");

                transaction.setBoughtPriceBtc(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // ETH
                coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("USD");

                transaction.setBoughtPriceEth(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                break;

            case "EUR":

                // USD
                coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("EUR");

                transaction.setBoughtPriceUsd(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // EUR
                transaction.setBoughtPriceEur(typedPrice);

                // BTC
                coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("EUR");

                transaction.setBoughtPriceBtc(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // ETH
                coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("EUR");

                transaction.setBoughtPriceEth(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                break;

            case "BTC":

                // USD
                coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("BTC");

                transaction.setBoughtPriceUsd(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // EUR
                coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("BTC");

                transaction.setBoughtPriceEur(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // BTC
                transaction.setBoughtPriceBtc(typedPrice);

                // ETH
                coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("BTC");

                transaction.setBoughtPriceEth(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                break;

            case "ETH":

                // USD
                coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("ETH");

                transaction.setBoughtPriceUsd(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // EUR
                coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("ETH");

                transaction.setBoughtPriceEur(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // BTC
                coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("ETH");

                transaction.setBoughtPriceBtc(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // ETH
                transaction.setBoughtPriceEth(typedPrice);

                break;
        }
    }

    // check if BigDecimal value corresponds to settings max value
    private boolean isBigDecimalVaildForDB(@NotNull BigDecimal transactionTemp) {

        return transactionTemp.compareTo(new BigDecimal(Settings.STRING_MAX_BIGDECIMAL_VALUE)) >= 1;
    }

    // autocomplete search method (identical to WatchlistBacking)
    public List<CoinEntity> completeCoinTemp(String query) {

        List<CoinEntity> filteredCoins = new ArrayList<>();

        for (CoinEntity coin : applicationContainer.getAllCoinsListing()) {

            if (coin.getName().toLowerCase().startsWith(query.toLowerCase().trim())
                    || coin.getSymbol().toLowerCase().startsWith(query.toLowerCase().trim())) {
                filteredCoins.add(coin);
            }
        }
        return filteredCoins;
    }
}