package tk.crypfolio.business;


import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.parse.ParserAPI;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//https://developer.jboss.org/thread/180134 CDI how to startup bean?
//@ApplicationScoped
@Singleton
@Startup
public class ApiParsingSchedulers {

    private static final Logger logger = Logger.getLogger(ApiParsingSchedulers.class.getName());


    @Inject
    private ApplicationContainer applicationContainer;

    @PostConstruct
    private void init() {
        logger.log(Level.INFO, "ApiParsingSchedulers start!");
    }

    /**
     * A Timer instance is injected
     * automatically by the container
     */
    // it's not each 10min, it's mean each 10min of each hour, e.g. 10, 20, 30, 40, 50, 00
    //TODO return it to 10 min
    @Schedule(minute = "*/30", hour = "*", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void parseAllCoinsByTickerId() {

        try {

            List<String> currenciesNames = Stream.of(CurrencyType.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());

            for (String currency : currenciesNames) {

                switch (currency) {

                    case "EUR":

                        Map<String, Map<Long, Map<String, Double>>> currenciesCoinsMap = ParserAPI.parseAllCoinsInfoByCoinTickerIdForTwoCurrencies(CurrencyType.EUR.getCurrency());


                        for (Map.Entry<String, Map<Long, Map<String, Double>>> entry : currenciesCoinsMap.entrySet()) {

                            String key = entry.getKey();
                            Map<Long, Map<String, Double>> value = entry.getValue();

                            if (key.equals("EUR")) {

                                applicationContainer.setAllCoinsByTickerInEur(value);

                            } else if (key.equals("USD")) {

                                applicationContainer.setAllCoinsByTickerInUsd(value);
                            }
                        }

                        break;

                    case "BTC":
                        applicationContainer.setAllCoinsByTickerInBtc(
                                ParserAPI.parseAllCoinsInfoByCoinTickerIdForOneCurrency(CurrencyType.BTC.getCurrency()));
                        break;

                    case "ETH":
                        applicationContainer.setAllCoinsByTickerInEth(
                                ParserAPI.parseAllCoinsInfoByCoinTickerIdForOneCurrency(CurrencyType.ETH.getCurrency()));
                        break;
                }
            }

        } catch (Exception e) {
            logger.log(Level.INFO, "ApiParsingSchedulers exception", e);
        }
    }
}
