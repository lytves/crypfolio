package tk.crypfolio.view;

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
public class TransactionsBacking implements Serializable {

    private static final Logger LOGGER = LogManager.getLogger(TransactionsBacking.class);

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
        if (transactionPriceTemp != null) setTransactionPriceTemp(null);
        if (transactionTotalTemp != null) setTransactionTotalTemp(null);
    }

    public void createItemTemp() {
        LOGGER.info("PortfolioBacking.createItemTemp");

        // if itemTemp is still == null, it means that user have used autocomplete to choose coin
        // and vice versa itemTemp != null, means user have chosen from the list of existing items,
        // coz on select from the list of existing items works ... selection="#{transactionsBacking.itemTemp}"
        if (getItemTemp() == null && getCoinTemp() != null) {

            setItemTemp(new ItemEntity(coinTemp));

            for (ItemEntity item : activeUser.getUser().getPortfolio().getItems()) {

                if (item.getCoin().getId().equals(coinTemp.getId())) {
                    itemTemp = item;
                    break;
                }
            }

            // user have chosen a coin from the list of existing items: ... selection="#{transactionsBacking.itemTemp}"
        } else if (getItemTemp() != null) {
            // also should do this to update condition in the jsf-view: rendered="#{transactionsBacking.coinTemp eq null}
            setCoinTemp(itemTemp.getCoin());

            // user have clicked "add transaction" from item's details,
            // so in the itemBacking we have itemEntity in the selectedItem attribute
        } else if (itemBacking.getSelectedItem().getId() != null) {

            setItemTemp(itemBacking.getSelectedItem());

            // also should do this to update condition in the jsf-view: rendered="#{transactionsBacking.coinTemp eq null}
            setCoinTemp(itemBacking.getSelectedItem().getCoin());
        }

        setTransactionTemp(new TransactionEntity());

        // to set initial item's coin price
        setTransactionPriceTemp(portfolioBacking.getCoinPrice(itemTemp.getCoin(), transactionTemp.getBoughtCurrency()));
    }

    private boolean isBigDecimalVaildForDB(@NotNull BigDecimal transactionTemp) {

        return transactionTemp.compareTo(new BigDecimal(Settings.STRING_MAX_BIGDECIMAL_VALUE)) >= 1;
    }

    /**
     * Method for submit "Add Transaction" form
     */
    public void doSubmitAddTransaction() {

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

            // makes API request to obtain history USD/EUR/BTC/ETH prices
            Map<String, Double> bitcoinHistoricalPrice = ParserAPI.parseBitcoiHistoricalPrice(transactionTemp.getBoughtDate());

            // recounts and sets prices in all currencies of the transaction
            Double coefficientMultiplier;

            switch (transactionTemp.getBoughtCurrency().getCurrency()) {

                case "USD":

                    // USD
                    transactionTemp.setBoughtPriceUsd(transactionPriceTemp);

                    // EUR
                    coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("USD");

                    transactionTemp.setBoughtPriceEur(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                    // BTC
                    coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("USD");

                    transactionTemp.setBoughtPriceBtc(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                    // ETH
                    coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("USD");

                    transactionTemp.setBoughtPriceEth(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                    break;

                case "EUR":

                    // USD
                    coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("EUR");

                    transactionTemp.setBoughtPriceUsd(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                    // EUR
                    transactionTemp.setBoughtPriceEur(transactionPriceTemp);

                    // BTC
                    coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("EUR");

                    transactionTemp.setBoughtPriceBtc(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                    // ETH
                    coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("EUR");

                    transactionTemp.setBoughtPriceEth(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                    break;

                case "BTC":

                    // USD
                    coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("BTC");

                    transactionTemp.setBoughtPriceUsd(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                    // EUR
                    coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("BTC");

                    transactionTemp.setBoughtPriceEur(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                    // BTC
                    transactionTemp.setBoughtPriceBtc(transactionPriceTemp);

                    // ETH
                    coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("BTC");

                    transactionTemp.setBoughtPriceEth(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                    break;

                case "ETH":

                    // USD
                    coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("ETH");

                    transactionTemp.setBoughtPriceUsd(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                    // EUR
                    coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("ETH");

                    transactionTemp.setBoughtPriceEur(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                    // BTC
                    coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("ETH");

                    transactionTemp.setBoughtPriceBtc(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                    // ETH
                    transactionTemp.setBoughtPriceEth(transactionPriceTemp);

                    break;
            }

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

            LOGGER.error("activeUser.getUser().getPortfolio().getItems(): " + activeUser.getUser().getPortfolio().getItems().toString());
        }
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