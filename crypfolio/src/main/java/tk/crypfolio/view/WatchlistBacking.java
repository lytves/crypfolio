package tk.crypfolio.view;

import tk.crypfolio.business.ApplicationContainer;
import tk.crypfolio.business.UserService;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.model.CoinEntity;
import tk.crypfolio.model.UserWatchCoinEntity;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class WatchlistBacking implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(WatchlistBacking.class.getName());

    // application scoped
    @Inject
    private ApplicationContainer applicationContainer;

    // session scoped
    @Inject
    private ActiveUser activeUser;

    // stateless business
    @Inject
    private UserService userService;

    // is used to temporary save autocompleted chosen coin
    private CoinEntity coinTemp;

    private CurrencyType currency;

    private CurrencyType[] currencies;

    @PostConstruct
    public void init() {
        LOGGER.log(Level.WARNING, "WatchlistBacking @PostConstruct");
        // to set in watch-add-coin modal window value "by default"
        this.currency = CurrencyType.USD;
    }

    @PreDestroy
    public void destroy() {
        LOGGER.log(Level.WARNING, "WatchlistBacking @PreDestroy");
    }

    public CoinEntity getCoinTemp() {
        return this.coinTemp;
    }

    public void setCoinTemp(CoinEntity coinTemp) {
        this.coinTemp = coinTemp;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    public CurrencyType[] getCurrencies() {
        return CurrencyType.values();
    }

    public ActiveUser getActiveUser() {
        return activeUser;
    }

    public void watchCoinAddFormReset(){
        LOGGER.log(Level.WARNING, "WatchlistBacking.watchCoinAddFormReset");
        // executing every time when watch-add-coin modal window is closed
        // or Reset button pushed
        setCoinTemp(null);
    }

    // autocomplete search method (identical to PortfolioBacking)
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

    public void doSubmitAddWatchCoin() {

        Boolean isCoinAlreadyInWatchList = false;

        if (coinTemp != null) {

            for (UserWatchCoinEntity userWatchCoinEntity : activeUser.getUser().getUserWatchCoins())

                if (userWatchCoinEntity.getCoinId().equals(coinTemp)) {

                    isCoinAlreadyInWatchList = true;
                    break;
                }

            if (!isCoinAlreadyInWatchList) {

                activeUser.getUser().addWatchCoin(coinTemp, currency);

                activeUser.setUser(userService.updateUserDB(activeUser.getUser()));

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "The coin has been successfully added to your watchlist.",
                        ""));

            } else {

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "The coin is already in your watchlist.",
                        ""));
            }

        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Error adding the coin to watchlist!",
                    ""));
            LOGGER.log(Level.WARNING, "Error adding the coin to watchlist!");

        }
    }

    public void doSubmitDeleteWatchCoin(UserWatchCoinEntity userWatchCoinEntity) {

        if (userWatchCoinEntity != null && activeUser.getUser().getUserWatchCoins().contains(userWatchCoinEntity)) {

            activeUser.getUser().removeWatchCoin(userWatchCoinEntity);

            activeUser.setUser(userService.updateUserDB(activeUser.getUser()));

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "The coin has been deleted from your watchlist successully.",
                    ""));
        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Error deleting coin from watchlist",
                    ""));
            LOGGER.log(Level.WARNING, "Error deleting coin from watchlist!");

        }
    }

    public void changeWatchCoinCurrency(UserWatchCoinEntity userWatchCoin) {

        if (activeUser.getUser().getUserWatchCoins().contains(userWatchCoin)) {

            userService.updateUserWatchCoinDB(userWatchCoin);

        } else {

            LOGGER.log(Level.WARNING, "Error on change of watch coin currency");

        }
    }

    /**
     * Method which returns String value of coin price
     *
     * @param id             - coin ID
     * @param showedCurrency - coin current watch Currency, enum
     * @return String value to show in the column: price or 0
     */
    public String getWatchCoinPrice(Long id, CurrencyType showedCurrency) {

        // this conditional is using to avoid "null" values on the add/delete coins,
        // because it provokes NullPointerException
        if (id != null && showedCurrency != null) {

            Double price = getCoinCurrentData("price", id, showedCurrency);

            if (price != null) {
                return String.valueOf(price);
            }
        }

        return "0";
    }

    /**
     * Method which returns String value of percentage of coin price changed by "percent_change_24hour"
     *
     * @param id             - coin ID
     * @param showedCurrency - coin current watch Currency, enum
     * @return String value to show in the column: percentage or 0
     */
    public String getWatchCoinChange24H(Long id, CurrencyType showedCurrency) {

        // this conditional is using to avoid "null" values on the add/delete coins,
        // because it provokes NullPointerException
        if (id != null && showedCurrency != null) {

            Double change24h = getCoinCurrentData("percent_change_24h", id, showedCurrency);

            if (change24h != null) {

                return String.valueOf(change24h);
            }
        }

        return "0";

    }

    /**
     * Method which returns String value of percentage of coin price changed by "percent_change_7d"
     *
     * @param id             - coin ID
     * @param showedCurrency - coin current watch Currency, enum
     * @return String value to show in the column: percentage or 0
     */
    public String getWatchCoinChange7D(Long id, CurrencyType showedCurrency) {

        // this conditional is using to avoid "null" values on the add/delete coins,
        // because it provokes NullPointerException
        if (id != null && showedCurrency != null) {

            Double change7d = getCoinCurrentData("percent_change_7d", id, showedCurrency);

            if (change7d != null) {

                return String.valueOf(change7d);
            }
        }

        return "0";

    }

    /**
     * Method which returns String value of coin market_cap
     *
     * @param id             - coin ID
     * @param showedCurrency - coin current watch Currency, enum
     * @return String value to show in the column: value or 0
     */
    public String getWatchCoinMarketCap(Long id, CurrencyType showedCurrency) {

        // this conditional is using to avoid "null" values on the add/delete coins,
        // because it provokes NullPointerException
//        if (id != null && showedCurrency != null) {

            Double marketCap = getCoinCurrentData("market_cap", id, showedCurrency);

            if (marketCap != null) {
                return String.valueOf(marketCap);
            }
//        }

        return "0";

    }

    /**
     * Universal method which returns actual coin value by type(data) from @ApplicationScoped bean
     * (identical to PortfolioBacking)
     */
    private Double getCoinCurrentData(String data, Long id, CurrencyType showedCurrency) {

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

    // to sort dataTable columns elements correct in order to numbers
    public int sortByModel(Object obj1, Object obj2) {

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

        } catch (NumberFormatException ex){
            LOGGER.log(Level.WARNING, ex.toString());
        }
        return 0;
    }
}

