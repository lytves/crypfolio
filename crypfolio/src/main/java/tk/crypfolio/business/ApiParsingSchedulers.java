package tk.crypfolio.business;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import tk.crypfolio.DAO.AbstractDAOFactory;
import tk.crypfolio.DAO.CoinDAO;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.common.SettingsDB;
import tk.crypfolio.common.SettingsParseAPI;
import tk.crypfolio.model.CoinEntity;
import tk.crypfolio.parse.ParserAPI;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//https://developer.jboss.org/thread/180134 CDI how to startup bean?
//@ApplicationScoped
@Singleton
@Startup
public class ApiParsingSchedulers {

    private static final Logger LOGGER = LogManager.getLogger(ApiParsingSchedulers.class);


    @Inject
    private ApplicationContainer applicationContainer;

//    https://stackoverflow.com/questions/14402068/ejb-schedule-wait-until-method-completed
//    this type of Timer is managed by the ApplicationServer and not by the application-owner

    @PostConstruct
    private void init() {
        LOGGER.info( "ApiParsingSchedulers start!");
    }

//    https://docs.oracle.com/javaee/6/tutorial/doc/bnboy.html - Using the Timer Service

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void parseAllCoinsOnSandboxCMC() {

        try {

            StringBuilder urlRequest = new StringBuilder();

            // https://sandbox-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?start=1&limit=5000&convert=USD,EUR,BTC,ETH
            // &CMC_PRO_API_KEY=PUT_YOUR_CMC_SANDBOX_API_KEY_HERE
            urlRequest.append(SettingsParseAPI.CMC_SANDBOX_ALL_COINS_FOUR_CURRENCIES)
                    .append(SettingsParseAPI.CMC_SANDBOX_API_KEY);

            LOGGER.info("parseAllCoinsOnSandboxCMC.urlRequest: " + urlRequest);

            String inlineString = ParserAPI.parseAPIByURL(urlRequest.toString(), "GET");

            if (!(inlineString.trim()).isEmpty()) {

                // save ALL CMC COINS, from previously parsed JSON, to DB - testing of the Sandbox API
                updateCoinsTableDB(inlineString);

                // save ALL CMC COINS, from previously parsed JSON, to Map separated by CurrencyType - testing of the Sandbox API
                parseJSONAllCoinsToMapByCurrency(inlineString);

                // save additional data of ALL CMC COINS, from previously parsed JSON, to Map - testing of the Sandbox API
                parseJSONAllCoinsAdditionalDataToMap(inlineString);
            }
        } catch (Exception ex) {
            LOGGER.warn( "ApiParsingSchedulers.parseAllCoinsSandboxCMC exception - " + ex.toString());
        }
    }

    /*
     *  Method to get from previously parsed JSON list of ALL CMC COINS and then save it to DB - testing of Sandbox API
     * */
    private void updateCoinsTableDB(@NotNull String inlineString) {

        // get all CMC coins
        ArrayList<CoinEntity> listCoins = (ArrayList<CoinEntity>) ParserAPI.parseJSONAllCoinsListing(inlineString);

        AbstractDAOFactory myFactory = AbstractDAOFactory.getDAOFactory(SettingsDB.APP_DB_TYPE);

        CoinDAO cDAO = myFactory.getCoinDAO();

        for (CoinEntity coin : listCoins) {
            cDAO.createCoin(coin);
        }
    }

    /*
     *  Method to get previously parsed list of ALL CMC COINS and order it by CurrencyType and then
     *  put it to Map<String, Map<Long, Map<String, Double>>> in the @ApplicationScoped bean ApplicationContainer
     * */
    private void parseJSONAllCoinsToMapByCurrency(@NotNull String inlineString) {

        List<String> currenciesNames = Stream.of(CurrencyType.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        for (String currency : currenciesNames) {

            switch (currency) {

                case "USD":

                    applicationContainer.setAllCoinsByTickerInUsd(
                            ParserAPI.parseAllCoinsInfoByCoinTickerIdForOneCurrency(inlineString, CurrencyType.USD.getCurrency()));
                    break;

                case "EUR":

                    applicationContainer.setAllCoinsByTickerInEur(
                            ParserAPI.parseAllCoinsInfoByCoinTickerIdForOneCurrency(inlineString, CurrencyType.EUR.getCurrency()));
                    break;

                case "BTC":

                    applicationContainer.setAllCoinsByTickerInBtc(
                            ParserAPI.parseAllCoinsInfoByCoinTickerIdForOneCurrency(inlineString, CurrencyType.BTC.getCurrency()));
                    break;

                case "ETH":

                    applicationContainer.setAllCoinsByTickerInEth(
                            ParserAPI.parseAllCoinsInfoByCoinTickerIdForOneCurrency(inlineString, CurrencyType.ETH.getCurrency()));
                    break;
            }
        }
    }

    /*
     *  Method to get previously parsed list of ALL CMC COINS and then save additional data,
     *  put it to Map<Long, Map<String, Double>> in the @ApplicationScoped bean ApplicationContainer
     * */
    private void parseJSONAllCoinsAdditionalDataToMap(@NotNull String inlineString) {

        applicationContainer.setAllCoinsByTickerAdditionalData(ParserAPI.parseAllCoinsAdditonalDataByCoinTicker(inlineString));
    }
}