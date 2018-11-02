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

    private static final Logger logger = Logger.getLogger(WatchlistBacking.class.getName());

    private CoinEntity coinTemp;

    private CoinEntity coin;

    private CurrencyType currency;

    private CurrencyType[] currencies;

    // session scoped
    @Inject
    private ActiveUser activeUser;

    // application scoped
    @Inject
    private ApplicationContainer applicationContainer;

    // stateless business
    @Inject
    private UserService userService;

    @PostConstruct
    public void init() {

        System.out.println("WatchlistBacking init");
    }

    @PreDestroy
    public void destroy() {

        System.out.println("WatchlistBacking destroy");

        setCoin(null);
        setCoinTemp(null);
        setCurrency(null);
    }

    public List<CoinEntity> completeCoinTemp(String query) {

        List<CoinEntity> filteredCoins = new ArrayList<>();

        for (CoinEntity coin : applicationContainer.getAllCoinsListing()) {

            if (coin.getName().toLowerCase().startsWith(query.toLowerCase())
                    || coin.getSymbol().toLowerCase().startsWith(query.toLowerCase())) {
                filteredCoins.add(coin);
//                filteredCoins.add(coin.getId().intValue(), coin);
            }
        }
        return filteredCoins;
    }

    public void doSubmitAddWatchCoin() {

        System.out.println("***coin: " + coin);
        Boolean isCoinInWatchList = false;

        if (coin != null) {

            for (UserWatchCoinEntity userWatchCoinEntity : activeUser.getUser().getUserWatchCoins())

                if (userWatchCoinEntity.getCoinId().equals(coin)) {

                    isCoinInWatchList = true;
                    break;
                }

            if (!isCoinInWatchList) {

                activeUser.getUser().addWatchCoin(coin, currency);

                activeUser.setUser(userService.updateUserDB(activeUser.getUser()));

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "The coin has been added successully to your watchlist.",
                        ""));

            } else {

                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "The coin already is in your watchlist.",
                        ""));
            }

        } else {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Error adding the coin to watchlist!",
                    ""));
        }
    }

    public void setCoinToSubmit() {
        this.coin = this.coinTemp;
    }

    public String getAutocompleteCoinName(CoinEntity coinTemp) {

        if (coinTemp != null) {

            return coinTemp.getName() + " (" + coinTemp.getSymbol() + ")";
        }

        return null;
    }

    public CoinEntity getCoinTemp() {
        return this.coinTemp;
    }

    public void setCoinTemp(CoinEntity coinTemp) {
        this.coinTemp = coinTemp;
    }

    public CoinEntity getCoin() {
        return coin;
    }

    public void setCoin(CoinEntity coin) {
        this.coin = coin;
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

    public String getWatchCoinPrice(Long id, CurrencyType showedCurrency) {

        Double price = coinCurrentData("price", id, showedCurrency);

        if (price != null) {
            return String.valueOf(price);
        }

        return "0";
    }

    public String getWatchCoinChange24H(Long id, CurrencyType showedCurrency) {

        Double change24h = coinCurrentData("percent_change_24h", id, showedCurrency);

        if (change24h != null) {
            return String.valueOf(change24h);
        }

        return "0";

    }

    public String getWatchCoinChange7D(Long id, CurrencyType showedCurrency) {

        Double change7d = coinCurrentData("percent_change_7d", id, showedCurrency);

        if (change7d != null) {

            return String.valueOf(change7d);
        }

        return "0";

    }

    public String getWatchCoinMarketCap(Long id, CurrencyType showedCurrency) {

        Double marketCap = coinCurrentData("market_cap", id, showedCurrency);

        if (marketCap != null) {
            return String.valueOf(marketCap);
        }

        return "0";

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
        }
    }

    public void changeWatchCoinCurrency(UserWatchCoinEntity userWatchCoin) {

        if (activeUser.getUser().getUserWatchCoins().contains(userWatchCoin)) {

            System.out.println("new userWatchCoin: " + userWatchCoin.toString());

            int index = activeUser.getUser().getUserWatchCoins().indexOf(userWatchCoin);


            System.out.println("before DB userWatchCoin: " + activeUser.getUser().getUserWatchCoins().get(index));

            userService.updateUserWatchCoinDB(userWatchCoin);

            System.out.println("after DB userWatchCoin: " +  activeUser.getUser().getUserWatchCoins().get(index));


//            PrimeFaces pf = PrimeFaces.current();
//            if (pf.isAjaxRequest()) {
//                pf.ajax().update(":dataTableWV:@row(0)");
//            }

        } else {

            logger.log(Level.INFO, "no");

        }
    }

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
        return null;
    }

    // to sort dataTable columns elements correct in order to numbers
    public int sortByModel(Object obj1, Object obj2) {

        Double id1 = Double.parseDouble((String) obj1);
        Double id2 = Double.parseDouble((String) obj2);

        if (id1 < id2) {
            return -1;
        } else if (id1.equals(id2)) {
            return 0;
        } else {
            return 1;
        }
    }
}

