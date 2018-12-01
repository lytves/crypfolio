package tk.crypfolio.view;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tk.crypfolio.business.ApplicationContainer;
import tk.crypfolio.business.UserService;
import tk.crypfolio.common.Constants;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.common.Settings;
import tk.crypfolio.model.CoinEntity;
import tk.crypfolio.model.ItemEntity;
import tk.crypfolio.model.TransactionEntity;
import tk.crypfolio.parse.ParserAPI;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
public class PortfolioBacking implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PortfolioBacking.class.getName());

    // application scoped
    @Inject
    private ApplicationContainer applicationContainer;

    // session scoped
    @Inject
    private ActiveUser activeUser;

    // stateless business
    @Inject
    private UserService userService;

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

    @PostConstruct
    public void init() {
        LOGGER.log(Level.WARNING, "PortfolioBacking @PostConstruct");
    }

    @PreDestroy
    public void destroy() {
        LOGGER.log(Level.WARNING, "PortfolioBacking @PreDestroy");
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

                if (transactionTemp.getAmount().compareTo(BigDecimal.ZERO) >= 1) {

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
                                .divide(transactionTemp.getAmount(), 8, BigDecimal.ROUND_HALF_EVEN));

                    } catch (ArithmeticException ex) {
                        LOGGER.log(Level.WARNING, ex.toString());
                    }

                } else if (("price").equals(getLastInputFormFocused())) {

                    try {
                        getTransactionTemp().setAmount(transactionTotalTemp
                                .divide(transactionPriceTemp, 8, BigDecimal.ROUND_HALF_EVEN));
                    } catch (ArithmeticException ex) {
                        LOGGER.log(Level.WARNING, ex.toString());
                    }
                }
                break;
        }
    }

    public void setActualTransactionPriceTemp() {
        transactionPriceTemp = getCoinPrice(itemTemp.getCoin(), transactionTemp.getBoughtCurrency());

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
        transactionPriceTemp = getCoinPrice(itemTemp.getCoin(), transactionTemp.getBoughtCurrency());
    }

    private boolean isBigDecimalVaildForDB(@NotNull BigDecimal transactionTemp) {

        return transactionTemp.compareTo(new BigDecimal(Settings.STRING_MAX_BIGDECIMAL_VALUE)) >= 1;
    }

    /**
     * Method for submit "Add Transaction" form
     *
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
                && transactionTemp.getAmount().compareTo(itemTemp.getAmount()) >= 1){

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
                            .setScale(8, BigDecimal.ROUND_HALF_EVEN));

                    // BTC
                    coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("USD");

                    transactionTemp.setBoughtPriceBtc(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_EVEN));

                    // ETH
                    coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("USD");

                    transactionTemp.setBoughtPriceEth(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_EVEN));

                    break;

                case "EUR":

                    // USD
                    coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("EUR");

                    transactionTemp.setBoughtPriceUsd(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_EVEN));

                    // EUR
                    transactionTemp.setBoughtPriceEur(transactionPriceTemp);

                    // BTC
                    coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("EUR");

                    transactionTemp.setBoughtPriceBtc(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_EVEN));

                    // ETH
                    coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("EUR");

                    transactionTemp.setBoughtPriceEth(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_EVEN));

                    break;

                case "BTC":

                    // USD
                    coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("BTC");

                    transactionTemp.setBoughtPriceUsd(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_EVEN));

                    // EUR
                    coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("BTC");

                    transactionTemp.setBoughtPriceEur(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_EVEN));

                    // BTC
                    transactionTemp.setBoughtPriceBtc(transactionPriceTemp);

                    // ETH
                    coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("BTC");

                    transactionTemp.setBoughtPriceEth(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_EVEN));

                    break;

                case "ETH":

                    // USD
                    coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("ETH");

                    transactionTemp.setBoughtPriceUsd(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_EVEN));

                    // EUR
                    coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("ETH");

                    transactionTemp.setBoughtPriceEur(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_EVEN));

                    // BTC
                    coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("ETH");

                    transactionTemp.setBoughtPriceBtc(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier))
                            .setScale(8, BigDecimal.ROUND_HALF_EVEN));

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

            activeUser.setUser(userService.updateUserDB(activeUser.getUser()));

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

    /**
     * Method which returns BigDecimal value of item's coin total current Market Value
     *
     * @param item - current ItemEntity
     * @return BigDecimal value to be showed and/or get sorted in the column: total market value or 0
     */
    public BigDecimal geItemMarketValue(@NotNull ItemEntity item) {

        try {

            BigDecimal coinMarketPrice = getCoinPrice(item.getCoin(), item.getShowedCurrency());

//            Double coinMarketPrice = coinCurrentData("price", item.getCoin().getId(), item.getShowedCurrency());
            return item.getAmount().multiply(coinMarketPrice).setScale(8, BigDecimal.ROUND_HALF_EVEN);

        } catch (NullPointerException ex) {
            LOGGER.log(Level.WARNING, ex.toString());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Method which returns BigDecimal value of coin actual Market Price
     *
     * @param coin           - specified CoinEntity
     * @param showedCurrency - specified сurrency to show the coin's price
     * @return BigDecimal coin price value or 0
     */
    public BigDecimal getCoinPrice(@NotNull CoinEntity coin, @NotNull CurrencyType showedCurrency) {

/*        switch (showedCurrency.getCurrency()) {

            case "USD":
                return new BigDecimal("1");

            case "EUR":
                return new BigDecimal("2");

            case "BTC":
                return new BigDecimal("3");

            case "ETH":
                return new BigDecimal("4");

        }
        return BigDecimal.ZERO;*/

        try {

            Double coinPrice = coinCurrentData("price", coin.getId(), showedCurrency);
            return BigDecimal.valueOf(coinPrice);

        } catch (NullPointerException ex) {
            LOGGER.log(Level.WARNING, ex.toString());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Method which returns String value of percentage of coin price changed by "percent_change_24hour"
     *
     * @param item - current itemEntity
     * @return String value to show in the column: percentage or 0
     */
    public String getCoinChange24H(@NotNull ItemEntity item) {

        // this conditional is using to avoid "null" values on the add/delete coins,
        // because it provokes NullPointerException
        if (item != null) {

            Double change24h = coinCurrentData("percent_change_24h", item.getCoin().getId(), item.getShowedCurrency());

            if (change24h != null) {

                return String.valueOf(change24h);
            }
        }
        return "0";
    }

    public BigDecimal getItemProfit(@NotNull ItemEntity item) {

        BigDecimal profit = BigDecimal.ZERO;

        switch (item.getShowedCurrency().getCurrency()) {

            case "USD":

                profit = (getCoinPrice(item.getCoin(), item.getShowedCurrency()).multiply(
                        item.getAmount())).subtract(item.getNetCostUsd()).setScale(8, BigDecimal.ROUND_HALF_EVEN);
                break;

            case "EUR":

                profit = (getCoinPrice(item.getCoin(), item.getShowedCurrency()).multiply(
                        item.getAmount())).subtract(item.getNetCostEur()).setScale(8, BigDecimal.ROUND_HALF_EVEN);
                break;

            case "BTC":

                profit = (getCoinPrice(item.getCoin(), item.getShowedCurrency()).multiply(
                        item.getAmount())).subtract(item.getNetCostBtc()).setScale(8, BigDecimal.ROUND_HALF_EVEN);
                break;

            case "ETH":

                profit = (getCoinPrice(item.getCoin(), item.getShowedCurrency()).multiply(
                        item.getAmount())).subtract(item.getNetCostEth()).setScale(8, BigDecimal.ROUND_HALF_EVEN);
                break;
        }
        return profit;
    }

    public String getItemProfitForSortBy(@NotNull ItemEntity item) {

        return String.valueOf(getItemProfit(item));

    }

    public String getItemProfitPercentage(@NotNull ItemEntity item) {

        BigDecimal profitPercentage = BigDecimal.ZERO;

        switch (item.getShowedCurrency().getCurrency()) {

            case "USD":

                profitPercentage = item.getNetCostUsd().divide(new BigDecimal("100"), 8, BigDecimal.ROUND_HALF_EVEN)
                        .multiply(getItemProfit(item)).setScale(8, BigDecimal.ROUND_HALF_EVEN);
                break;

            case "EUR":

                profitPercentage = item.getNetCostEur().divide(new BigDecimal("100"), 8, BigDecimal.ROUND_HALF_EVEN)
                        .multiply(getItemProfit(item)).setScale(8, BigDecimal.ROUND_HALF_EVEN);
                break;

            case "BTC":

                profitPercentage = item.getNetCostBtc().divide(new BigDecimal("100"), 8, BigDecimal.ROUND_HALF_EVEN)
                        .multiply(getItemProfit(item)).setScale(8, BigDecimal.ROUND_HALF_EVEN);
                break;

            case "ETH":

                profitPercentage = item.getNetCostEth().divide(new BigDecimal("100"), 8, BigDecimal.ROUND_HALF_EVEN)
                        .multiply(getItemProfit(item)).setScale(8, BigDecimal.ROUND_HALF_EVEN);
                break;
        }
        return String.valueOf(profitPercentage);
    }

    public String getItemSharePercentage(@NotNull ItemEntity item) {

        BigDecimal sharePercentage = BigDecimal.ZERO;

        switch (activeUser.getUser().getPortfolio().getShowedCurrency().getCurrency()) {

            case "USD":

                try {
                    sharePercentage = geItemMarketValue(item).multiply(new BigDecimal("100"))
                            .divide(activeUser.getUser().getPortfolio().getNetCostUsd(), 8, BigDecimal.ROUND_HALF_EVEN);
                } catch (ArithmeticException ex){
                    LOGGER.log(Level.WARNING, ex.toString());
                }
                break;

            case "EUR":

                try {
                    sharePercentage = geItemMarketValue(item).multiply(new BigDecimal("100"))
                            .divide(activeUser.getUser().getPortfolio().getNetCostEur(), 8, BigDecimal.ROUND_HALF_EVEN);
                } catch (ArithmeticException ex){
                    LOGGER.log(Level.WARNING, ex.toString());
                }
                break;

            case "BTC":

                try {
                    sharePercentage = geItemMarketValue(item).multiply(new BigDecimal("100"))
                            .divide(activeUser.getUser().getPortfolio().getNetCostBtc(), 8, BigDecimal.ROUND_HALF_EVEN);
                } catch (ArithmeticException ex){
                    LOGGER.log(Level.WARNING, ex.toString());
                }
                break;

            case "ETH":

                try {
                    sharePercentage = geItemMarketValue(item).multiply(new BigDecimal("100"))
                            .divide(activeUser.getUser().getPortfolio().getNetCostEth(), 8, BigDecimal.ROUND_HALF_EVEN);
                } catch (ArithmeticException ex){
                    LOGGER.log(Level.WARNING, ex.toString());
                }
                break;
        }

        return String.valueOf(sharePercentage);
    }

    /**
     * Universal method which returns actual coin value by type(data) from @ApplicationScoped bean
     * (identical to WatchlistBacking)
     */
    private Double coinCurrentData(String data, Long id, @NotNull CurrencyType showedCurrency) {

        switch (showedCurrency.getCurrency()) {

            case "USD":

                for (Map.Entry<Long, Map<String, Double>> entry : applicationContainer.getAllCoinsByTickerInUsd().entrySet()) {

                    Long key = entry.getKey();
                    Map<String, Double> value = entry.getValue();

                    if (id.equals(key)) {
                        return value.get(data);
                    }
                }
                break;

            case "EUR":

                for (Map.Entry<Long, Map<String, Double>> entry : applicationContainer.getAllCoinsByTickerInEur().entrySet()) {

                    Long key = entry.getKey();
                    Map<String, Double> value = entry.getValue();

                    if (id.equals(key)) {
                        return value.get(data);
                    }

                }
                break;

            case "BTC":

                for (Map.Entry<Long, Map<String, Double>> entry : applicationContainer.getAllCoinsByTickerInBtc().entrySet()) {

                    Long key = entry.getKey();
                    Map<String, Double> value = entry.getValue();

                    if (id.equals(key)) {
                        return value.get(data);
                    }
                }
                break;

            case "ETH":

                for (Map.Entry<Long, Map<String, Double>> entry : applicationContainer.getAllCoinsByTickerInEth().entrySet()) {

                    Long key = entry.getKey();
                    Map<String, Double> value = entry.getValue();

                    if (id.equals(key)) {
                        return value.get(data);
                    }
                }
                break;
        }
        return 0.0;
    }

    // to sort dataTable columns elements correct in order to numbers (identical to WatchlistBacking)
    public int sortByModel(@NotNull Object obj1, @NotNull Object obj2) {

        try {

            Double id1 = Double.parseDouble((String) obj1);
            Double id2 = Double.parseDouble((String) obj2);

            if (id1 < id2) {
                return -1;
            } else if (id1.equals(id2)) {
                return 0;
            } else {
                return 1;
            }

        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, ex.toString());
        }
        return 0;
    }
}