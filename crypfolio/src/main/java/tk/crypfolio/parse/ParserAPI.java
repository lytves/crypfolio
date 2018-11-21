package tk.crypfolio.parse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.common.SettingsParseAPI;
import tk.crypfolio.model.CoinEntity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ParserAPI {

    private static final Logger LOGGER = Logger.getLogger(ParserAPI.class.getName());

    /*
     *  Universal method to launch API request
     * */
    private static String parseAPIByURL(String stringURL, String stringMethod) {

        //inlineString will store the JSON data streamed in string format
        StringBuilder inlineString = new StringBuilder();

        try {
            URL url = new URL(stringURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(stringMethod);
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {

                Scanner sc = new Scanner(url.openStream());

                while (sc.hasNext()) {
                    inlineString.append(sc.nextLine());
                }

                sc.close();

            } else {
                LOGGER.log(Level.WARNING, "Error in parseAPIByURL: {0}", responseCode);
            }

        } catch (java.io.IOException e) {
            LOGGER.log(Level.WARNING, "IOException in parseAPIByURL: {0}", e.getMessage());
        }

        return inlineString.toString();
    }


    /*
     * Method to receive ALL COINS list from CoinMarketCap API v2
     * */
    public static List<CoinEntity> parseAllCoin() {

        ArrayList<CoinEntity> listCoins = new ArrayList<>();

        String inlineString = parseAPIByURL(SettingsParseAPI.CMC_ALL_COINS_LIST, "GET");

        if (!inlineString.trim().isEmpty()) {

            //JSONParser reads the data from string object and break each data into key value pairs
            JSONParser parserJSON = new JSONParser();

            try {

                JSONObject jsonDataObj = (JSONObject) parserJSON.parse(inlineString);
                JSONArray jsonArray = (JSONArray) jsonDataObj.get("data");

                for (Object jsonObject : jsonArray) {

                    //Get the index of the JSON objects and print the values as per the index
                    JSONObject jsonObj = (JSONObject) jsonObject;

                    Long coinId = (Long) jsonObj.get("id");
                    String coinName = (String) jsonObj.get("name");
                    String coinSymbol = (String) jsonObj.get("symbol");
                    String coinSlug = (String) jsonObj.get("website_slug");

                    CoinEntity coin = new CoinEntity(coinId, coinName, coinSymbol, coinSlug);

                    listCoins.add(coin);
                }

            } catch (ParseException e) {
                LOGGER.log(Level.WARNING, "Error in parseAllCoin: {0}", e.getMessage());

            } catch (ClassCastException e) {
                try {

                    JSONObject jsonObject = (JSONObject) parserJSON.parse(inlineString);
                    String error = (String) jsonObject.get("error");

                    LOGGER.log(Level.WARNING, "Error in parseAllCoin: {0}", error);

                } catch (ParseException ex) {
                    LOGGER.log(Level.WARNING, "Error in parseAllCoin: {0}", ex.getMessage());
                }
            }
        }
        return listCoins;
    }

    /*
     * Method to parse actual Coin data from CoinMarketCap API v2
     * */
    public static Map<Long, Map<String, Double>> parseAllCoinsInfoByCoinTickerIdForOneCurrency(String currency)
            throws InterruptedException {

        Map<Long, Map<String, Double>> coinsMap = new HashMap<>();

        String stringCoinsObject = parseCoinByCoinsTickerId(currency);

        //JSONParser reads the data from string object and break each data into key-value pairs
        JSONParser parserJSON = new JSONParser();

        try {

            JSONObject jsonCoinsObj = (JSONObject) parserJSON.parse(stringCoinsObject);

            for (Object coinKey : jsonCoinsObj.keySet()) {

                String coinKeyString = (String) coinKey;
                Long coinKeyLong = Long.parseLong((String) coinKey);

                // loop to get the dynamic key
                JSONObject coinObject = (JSONObject) jsonCoinsObj.get(coinKeyString);

                JSONObject coinQuotes = (JSONObject) coinObject.get("quotes");

                JSONObject coinQuoteCurrency = (JSONObject) coinQuotes.get(currency);

                Map<String, Double> coinInfoMap = new HashMap<>();

                Double coinPrice = null, coinPercentChange24H = null,
                        coinPercentChange7D = null, coinMarketCap = null;

                if (coinQuoteCurrency.get("price") != null) {

                    coinPrice = ((Number) coinQuoteCurrency.get("price")).doubleValue();
                }

                if (coinQuoteCurrency.get("percent_change_24h") != null) {

                    coinPercentChange24H = ((Number) coinQuoteCurrency.get("percent_change_24h")).doubleValue();
                }

                if (coinQuoteCurrency.get("percent_change_7d") != null) {

                    coinPercentChange7D = ((Number) coinQuoteCurrency.get("percent_change_7d")).doubleValue();

                }

                if (coinQuoteCurrency.get("market_cap") != null) {

                    coinMarketCap = ((Number) coinQuoteCurrency.get("market_cap")).doubleValue();

                }

                coinInfoMap.put("price", coinPrice);
                coinInfoMap.put("percent_change_24h", coinPercentChange24H);
                coinInfoMap.put("percent_change_7d", coinPercentChange7D);
                coinInfoMap.put("market_cap", coinMarketCap);

                coinsMap.put(coinKeyLong, coinInfoMap);

            }

        } catch (ParseException | ClassCastException e) {
            LOGGER.log(Level.WARNING, "Error in parseAllCoinsInfoByCoinTickerIdForOneCurrency", e);
        }

        return coinsMap;
    }

    /*
     * Method to parse actual Coin data from CoinMarketCap API v2
     * */
    public static Map<String, Map<Long, Map<String, Double>>> parseAllCoinsInfoByCoinTickerIdForTwoCurrencies(String currency)
            throws InterruptedException {

        Map<String, Map<Long, Map<String, Double>>> currenciesCoinsMap = new HashMap<>();
        Map<Long, Map<String, Double>> coinsFirstCurrencyMap = new HashMap<>();
        Map<Long, Map<String, Double>> coinsUsdMap = new HashMap<>();

        String stringCoinsObject = parseCoinByCoinsTickerId(currency);

        //JSONParser reads the data from string object and break each data into key-value pairs
        JSONParser parserJSON = new JSONParser();

        try {

            JSONObject jsonCoinsObj = (JSONObject) parserJSON.parse(stringCoinsObject);

            for (Object coinKey : jsonCoinsObj.keySet()) {

                String coinKeyString = (String) coinKey;
                Long coinKeyLong = Long.parseLong((String) coinKey);

                // loop to get the dynamic key
                JSONObject coinObject = (JSONObject) jsonCoinsObj.get(coinKeyString);

                JSONObject coinQuotes = (JSONObject) coinObject.get("quotes");

                // *** Parsing: currency ***
                JSONObject coinQuoteCurrency = (JSONObject) coinQuotes.get(currency);

                coinsFirstCurrencyMap.put(coinKeyLong, parseCoinInfoData(coinQuoteCurrency));

                // *** Parsing: USD ***
                JSONObject coinQuoteUSD = (JSONObject) coinQuotes.get(CurrencyType.USD.getCurrency());

                coinsUsdMap.put(coinKeyLong, parseCoinInfoData(coinQuoteUSD));

            }

        } catch (ParseException | ClassCastException e) {
            LOGGER.log(Level.WARNING, "Error in parseAllCoinsInfoByCoinTickerIdForOneCurrency", e);
        }


        currenciesCoinsMap.put(currency, coinsFirstCurrencyMap);
        currenciesCoinsMap.put(CurrencyType.USD.getCurrency(), coinsUsdMap);

        return currenciesCoinsMap;

    }

    private static Map<String, Double> parseCoinInfoData(JSONObject coinQuoteCurrency) {

        Map<String, Double> coinInfoMap = new HashMap<>();

        Double coinPrice = null, coinPercentChange24H = null,
                coinPercentChange7D = null, coinMarketCap = null;

        if (coinQuoteCurrency.get("price") != null) {

            coinPrice = ((Number) coinQuoteCurrency.get("price")).doubleValue();
        }

        if (coinQuoteCurrency.get("percent_change_24h") != null) {

            coinPercentChange24H = ((Number) coinQuoteCurrency.get("percent_change_24h")).doubleValue();
        }

        if (coinQuoteCurrency.get("percent_change_7d") != null) {

            coinPercentChange7D = ((Number) coinQuoteCurrency.get("percent_change_7d")).doubleValue();

        }

        if (coinQuoteCurrency.get("market_cap") != null) {

            coinMarketCap = ((Number) coinQuoteCurrency.get("market_cap")).doubleValue();

        }

        coinInfoMap.put("price", coinPrice);
        coinInfoMap.put("percent_change_24h", coinPercentChange24H);
        coinInfoMap.put("percent_change_7d", coinPercentChange7D);
        coinInfoMap.put("market_cap", coinMarketCap);

        return coinInfoMap;

    }

    private static String parseCoinByCoinsTickerId(String currency) throws InterruptedException {

        StringBuilder stringCoinsObject = new StringBuilder("{");

        StringBuilder urlRequest = new StringBuilder();

        Boolean nextParse = true;
        int i = 1;

        while (nextParse) {

            //          https://api.coinmarketcap.com/v2/ticker/?start=1&limit=100&convert=EUR
            urlRequest.append(SettingsParseAPI.CMC_COIN_BY_TICKER_ID)
                    .append("?start=")
                    .append(i)
                    .append("&limit=100&convert=")
                    .append(currency);

            LOGGER.log(Level.WARNING, "parseCoinByCoinsTickerId urlRequest " + urlRequest);

            String inlineString = parseAPIByURL(urlRequest.toString(), "GET");

            if (!inlineString.trim().isEmpty()) {

                //JSONParser reads the data from string object and break each data into key-value pairs
                JSONParser parserJSON = new JSONParser();

                try {

                    JSONObject jsonMainDataObj = (JSONObject) parserJSON.parse(inlineString);
                    JSONObject jsonCoinsObject = (JSONObject) jsonMainDataObj.get("data");

                    if (jsonCoinsObject != null) {

                        for (Object coinKey : jsonCoinsObject.keySet()) {

                            String coinKeyString = (String) coinKey;

                            // loop to get the dynamic key
                            JSONObject coinObject = (JSONObject) jsonCoinsObject.get(coinKeyString);

                            stringCoinsObject.append("\"");
                            stringCoinsObject.append(coinKeyString);
                            stringCoinsObject.append("\"");
                            stringCoinsObject.append(":");
                            stringCoinsObject.append(coinObject);
                            stringCoinsObject.append(",");
                        }
                    }
                } catch (ClassCastException | ParseException ex) {

                    LOGGER.log(Level.WARNING, "Error in parseCoinByCoinsTickerId", ex);

                }

                urlRequest.setLength(0);

            } else {

                nextParse = false;
                stringCoinsObject.deleteCharAt(stringCoinsObject.length() - 1);
                stringCoinsObject.append("}");
            }

            i += 100;
            TimeUnit.SECONDS.sleep(3);
        }

        return stringCoinsObject.toString();
    }


    /*
     * Method to parse BITCOIN historical data from Cryptocompare API
     * is used to recount bought prices in transactions
     * */
    public static Map<String, Double> parseBitcoiHistoricalPrice(LocalDate localDate) {

        Map<String, Double> bitcoinHistoricalPrice = new HashMap<>();
        bitcoinHistoricalPrice.put("BTC", 1.0);

        StringBuilder urlRequest = new StringBuilder();

        // https://min-api.cryptocompare.com/data/pricehistorical?fsym=BTC&tsyms=USD,EUR,ETH&ts=1529020800
        urlRequest.append(SettingsParseAPI.CC_COIN_HISTORICAL_DATA)
                .append("BTC")
                .append("&tsyms=USD,EUR,ETH")
                .append("&ts=")
                // look to the end of the day by UTC timezone, it's final value by the day in the Cryptocompare
                // if value is more than .now() is showed today actual price value
                .append(localDate.atTime(23, 59, 59).atZone(ZoneId.of("Europe/Oslo")).toEpochSecond());

        String inlineString = parseAPIByURL(urlRequest.toString(), "GET");

        LOGGER.log(Level.WARNING, "parseBitcoiHistoricalPrice urlRequest " + urlRequest);

        if (!inlineString.trim().isEmpty()) {

            //JSONParser reads the data from string object and break each data into key-value pairs
            JSONParser parserJSON = new JSONParser();

            try {

                JSONObject jsonMainDataObj = (JSONObject) parserJSON.parse(inlineString);

                JSONObject jsonBTCObject = (JSONObject) jsonMainDataObj.get("BTC");

                if (jsonBTCObject != null) {

                    for (Object pricePair: jsonBTCObject.keySet() ){

                        String priceCurrency = (String) pricePair;
                        Double priceValue = (Double) jsonBTCObject.get(priceCurrency);

                        bitcoinHistoricalPrice.put(priceCurrency, priceValue);
                    }

                }
            } catch (ClassCastException | ParseException ex) {

                LOGGER.log(Level.WARNING, "Exception in parseCoinByCoinsTickerId" + ex);

            }
        }

        return bitcoinHistoricalPrice;
    }
}
