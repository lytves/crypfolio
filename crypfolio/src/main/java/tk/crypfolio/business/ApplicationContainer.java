package tk.crypfolio.business;

import tk.crypfolio.model.CoinEntity;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//https://developer.jboss.org/thread/180134 CDI how to startup bean?
@ApplicationScoped
@Singleton
@Startup
public class ApplicationContainer {

    // stateless business
    @Inject
    private CoinService coinService;

    private List<CoinEntity> allCoinsListing;

    private Map<Long, Map<String, Double>> allCoinsByTickerInUsd = new HashMap<>();

    private Map<Long, Map<String, Double>> allCoinsByTickerInEur = new HashMap<>();

    private Map<Long, Map<String, Double>> allCoinsByTickerInBtc = new HashMap<>();

    private Map<Long, Map<String, Double>> allCoinsByTickerInEth = new HashMap<>();

    private Map<Long, Map<String, Double>> allCoinsByTickerAdditionalData = new HashMap<>();

    @PostConstruct
    private void init() {

        System.out.println("ApplicationContainer init!");
        this.allCoinsListing = coinService.getAllCoinsDB();

    }

    public List<CoinEntity> getAllCoinsListing() {
        return allCoinsListing;
    }

    public Map<Long, Map<String, Double>> getAllCoinsByTickerInUsd() {
        return allCoinsByTickerInUsd;
    }

    public void setAllCoinsByTickerInUsd(Map<Long, Map<String, Double>> allCoinsByTickerInUsd) {
        this.allCoinsByTickerInUsd = allCoinsByTickerInUsd;
    }

    public Map<Long, Map<String, Double>> getAllCoinsByTickerInEur() {
        return allCoinsByTickerInEur;
    }

    public void setAllCoinsByTickerInEur(Map<Long, Map<String, Double>> allCoinsByTickerInEur) {
        this.allCoinsByTickerInEur = allCoinsByTickerInEur;
    }

    public Map<Long, Map<String, Double>> getAllCoinsByTickerInBtc() {
        return allCoinsByTickerInBtc;
    }

    public void setAllCoinsByTickerInBtc(Map<Long, Map<String, Double>> allCoinsByTickerInBtc) {
        this.allCoinsByTickerInBtc = allCoinsByTickerInBtc;
    }

    public Map<Long, Map<String, Double>> getAllCoinsByTickerInEth() {
        return allCoinsByTickerInEth;
    }

    public void setAllCoinsByTickerInEth(Map<Long, Map<String, Double>> allCoinsByTickerInEth) {
        this.allCoinsByTickerInEth = allCoinsByTickerInEth;
    }

    public Map<Long, Map<String, Double>> getAllCoinsByTickerAdditionalData() {
        return allCoinsByTickerAdditionalData;
    }

    public void setAllCoinsByTickerAdditionalData(Map<Long, Map<String, Double>> allCoinsByTickerAdditionalData) {
        this.allCoinsByTickerAdditionalData = allCoinsByTickerAdditionalData;
    }
}