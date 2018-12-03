package tk.crypfolio.view;

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
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class TransactionsBacking implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TransactionsBacking.class.getName());

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

    // is used to show current date in the Date input placeholder
    private String datePlaceholder;

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
    public String getDatePlaceholder() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(Constants.datePattern, Constants.mainLocale));
    }

    public void getItemAllAmount() {
        transactionTemp.setAmount(itemTemp.getAmount());

        autoRecalculateTransactionInputData("amount");
    }

    // autocalculating add-item-transaction form variables
    public void autoRecalculateTransactionInputData(String changedInputForm) {
        LOGGER.log(Level.WARNING, "autoRecalculateTransactionInputData__________________________");

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
                        LOGGER.log(Level.WARNING, ex.toString());
                    }

                } else if (("price").equals(getLastInputFormFocused())) {

                    try {
                        getTransactionTemp().setAmount(transactionTotalTemp
                                .divide(transactionPriceTemp, 8, BigDecimal.ROUND_HALF_DOWN));
                    } catch (ArithmeticException ex) {
                        LOGGER.log(Level.WARNING, ex.toString());
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
        LOGGER.log(Level.WARNING, "PortfolioBacking.transactionAddFormReset");
        // executing every time when add-transaction modal window is closed
        // or Reset button pushed
        if (coinTemp != null) setCoinTemp(null);
        if (itemTemp != null) setItemTemp(null);
        if (transactionTemp != null) setTransactionTemp(null);
        if (transactionPriceTemp != null) setTransactionPriceTemp(null);
        if (transactionTotalTemp != null) setTransactionTotalTemp(null);
    }

    public void createItemTemp() {
        LOGGER.log(Level.WARNING, "PortfolioBacking.createItemTemp");

        itemTemp = new ItemEntity(coinTemp);

        for (ItemEntity item : activeUser.getUser().getPortfolio().getItems()) {

            if (item.getCoin().getId().equals(coinTemp.getId())) {
                itemTemp = item;
                break;
            }
        }

        transactionTemp = new TransactionEntity();

        // to set initial item's coin price
        transactionPriceTemp = portfolioBacking.getCoinPrice(itemTemp.getCoin(), transactionTemp.getBoughtCurrency());
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
                || (transactionTemp.getAmount() == null || isBigDecimalVaildForDB(transactionTemp.getAmount()))
                || (transactionPriceTemp == null || isBigDecimalVaildForDB(transactionPriceTemp))
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

            portfolioBacking.init();

            activeUser.setPortfolio(portfolioService.updatePortfolioDB(activeUser.getUser().getPortfolio()));

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