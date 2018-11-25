package tk.crypfolio.view;

import tk.crypfolio.business.ApplicationContainer;
import tk.crypfolio.business.UserService;
import tk.crypfolio.common.Constants;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.model.CoinEntity;
import tk.crypfolio.model.ItemEntity;
import tk.crypfolio.model.TransactionEntity;
import tk.crypfolio.parse.ParserAPI;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
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

    // it's using a price temporary coz we steel don't know
    // which transaction currency will used like main
    // so can't write to direct to some paraTransactionEntity property
    private BigDecimal transactionPriceTemp;

    // see comment above p.s.: or won't use a separate variable, don't know yet))
    private BigDecimal transactionTotalTemp;

    // is used to show current date in the Date input placeholder
    private String datePlaceholder;

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

    /*
     * * * * * * * * * * * * * * * * * * * * * Bean's methods * * * * * * * * * * * * * * * * * * * * *
     * */
    public String getDatePlaceholder() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(Constants.datePattern, Constants.mainLocale));
    }

    public void getItemAllAmount() {
        transactionTemp.setAmount(itemTemp.getAmount());
    }

    /*test_____test
     * test_____test
     * test_____test
     * test_____test
     * test_____test
     * test_____test
     * */
    public void test() {
        LOGGER.log(Level.WARNING, "test_____test");
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

    public void doSubmitAddTransaction() {

        if (itemTemp != null && transactionTemp != null) {

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
                    transactionTemp.setBoughtPriceEur(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier)));

                    // BTC
                    coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("USD");
                    transactionTemp.setBoughtPriceBtc(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier)));

                    // ETH
                    coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("USD");
                    transactionTemp.setBoughtPriceEth(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier)));

                    break;

                case "EUR":

                    // USD
                    coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("EUR");
                    transactionTemp.setBoughtPriceUsd(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier)));

                    // EUR
                    transactionTemp.setBoughtPriceEur(transactionPriceTemp);

                    // BTC
                    coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("EUR");
                    transactionTemp.setBoughtPriceBtc(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier)));

                    // ETH
                    coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("EUR");
                    transactionTemp.setBoughtPriceEth(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier)));

                    break;

                case "BTC":

                    // USD
                    coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("BTC");
                    transactionTemp.setBoughtPriceUsd(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier)));

                    // EUR
                    coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("BTC");
                    transactionTemp.setBoughtPriceEur(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier)));

                    // BTC
                    transactionTemp.setBoughtPriceBtc(transactionPriceTemp);

                    // ETH
                    coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("BTC");
                    transactionTemp.setBoughtPriceEth(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier)));

                    break;

                case "ETH":

                    // USD
                    coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("ETH");
                    transactionTemp.setBoughtPriceUsd(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier)));

                    // EUR
                    coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("ETH");
                    transactionTemp.setBoughtPriceEur(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier)));

                    // BTC
                    coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("ETH");
                    transactionTemp.setBoughtPriceBtc(transactionPriceTemp.multiply(BigDecimal.valueOf(coefficientMultiplier)));

                    // ETH
                    transactionTemp.setBoughtPriceEth(transactionPriceTemp);

                    break;
            }

            itemTemp.addTransaction(transactionTemp);

            if (itemTemp.getId() == null) {
                itemTemp.setShowedCurrency(transactionTemp.getBoughtCurrency());
                activeUser.getUser().getPortfolio().addItem(itemTemp);
            }

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

    // TODO to implement portfolio archive ------->  move to TransactionBacking ???!!!!!!!
    public void doSubmitMoveCoinToArchive() {

    }

    /**
     * Method which returns BigDecimal value of item's coin total current Market Value
     *
     * @param item - current ItemEntity
     * @return BigDecimal value to be showed and/or get sorted in the column: total market value or 0
     */
    public BigDecimal geItemMarketValue(ItemEntity item) {

        try {

//            Double marketValue = 1.09;
            Double marketValue = coinCurrentData("price", item.getCoin().getId(), item.getShowedCurrency());
            return item.getAmount().multiply(BigDecimal.valueOf(marketValue));

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
    public BigDecimal getCoinPrice(CoinEntity coin, CurrencyType showedCurrency) {

        try {

            Double coinPrice = coinCurrentData("price", coin.getId(), showedCurrency);
            return BigDecimal.valueOf(coinPrice);

        } catch (NullPointerException ex) {
            LOGGER.log(Level.WARNING, ex.toString());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Universal method which returns actual coin value by type(data) from @ApplicationScoped bean
     * (identical to WatchlistBacking)
     */
    private Double coinCurrentData(String data, Long id, CurrencyType showedCurrency) {

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
}