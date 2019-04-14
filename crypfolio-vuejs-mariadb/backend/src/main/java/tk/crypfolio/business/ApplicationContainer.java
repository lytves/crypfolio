package tk.crypfolio.business;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.model.CoinEntity;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//https://developer.jboss.org/thread/180134 CDI how to startup bean?
@ApplicationScoped
//@Singleton
//@Startup
public class ApplicationContainer {

    private static final Logger LOGGER = LogManager.getLogger(ApplicationContainer.class);

    // best java practice - is preferable to return empty Collection, to avoid NullPointerException
    private List<CoinEntity> allCoinsListing = new ArrayList<>();

    private Map<Long, Map<String, Double>> allCoinsByTickerInUsd = new HashMap<>();

    private Map<Long, Map<String, Double>> allCoinsByTickerInEur = new HashMap<>();

    private Map<Long, Map<String, Double>> allCoinsByTickerInBtc = new HashMap<>();

    private Map<Long, Map<String, Double>> allCoinsByTickerInEth = new HashMap<>();

    private Map<Long, Map<String, Double>> allCoinsByTickerAdditionalData = new HashMap<>();

    private Map<String, Double> globalMarketData = new HashMap<>();

    @PostConstruct
    private void init() {
        LOGGER.info("ApplicationContainer init!");
    }

    public List<CoinEntity> getAllCoinsListing() {
        return allCoinsListing;
    }

    public void setAllCoinsListing(List<CoinEntity> allCoinsListing) {
        this.allCoinsListing = allCoinsListing;
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

    public Map<String, Double> getGlobalMarketData() {
        return globalMarketData;
    }

    public void setGlobalMarketData(Map<String, Double> globalMarketData) {
        this.globalMarketData = globalMarketData;
    }
}