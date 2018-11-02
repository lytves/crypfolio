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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ParserAPI {

    private static final Logger logger = Logger.getLogger(ParserAPI.class.getName());

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
                logger.log(Level.WARNING, "Error in parseAPIByURL: {0}", responseCode);
            }

        } catch (java.io.IOException e) {
            logger.log(Level.WARNING, "IOException in parseAPIByURL: {0}", e.getMessage());
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
                logger.log(Level.WARNING, "Error in parseAllCoin: {0}", e.getMessage());

            } catch (ClassCastException e) {
                try {

                    JSONObject jsonObject = (JSONObject) parserJSON.parse(inlineString);
                    String error = (String) jsonObject.get("error");

                    logger.log(Level.WARNING, "Error in parseAllCoin: {0}", error);

                } catch (ParseException ex) {
                    logger.log(Level.WARNING, "Error in parseAllCoin: {0}", ex.getMessage());
                }
            }
        }
        return listCoins;
    }

    /*
     * Method to receive allCoinsByCoinTickerId from CoinMarketCap API v2
     * */
/*    @SuppressWarnings("unchecked")
    public static Map parseCoinByCoinsTickerIdUsdPlusEur() throws InterruptedException {

        Map<CurrencyType, Map<Long, Map<String, Double>>> coinParsedInfoMap = new HashMap<>();

        StringBuilder urlRequest = new StringBuilder();

        Boolean nextParse = true;
        int i = 1;

        while (nextParse) {

//          https://api.coinmarketcap.com/v2/ticker/?start=1&limit=100&convert=EUR
            urlRequest.append(SettingsParseAPI.CMC_COIN_BY_TICKER_ID)
                    .append("?start=")
                    .append(i)
                    .append("&limit=100&convert=EUR");

            System.out.println("urlRequest: " + urlRequest);

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
                            Long coinKeyLong = Long.valueOf((String) coinKey);

                            // loop to get the dynamic key
                            JSONObject coinObject = (JSONObject) jsonCoinsObject.get(coinKeyString);

                            Long coinIdLong = (Long) coinObject.get("id");
                            System.out.println("id" + (Long) coinObject.get("id"));

                            JSONObject coinQuotes = (JSONObject) coinObject.get("quotes");

                            JSONObject coinQuoteCurrency = (JSONObject) coinQuotes.get(currency.toString());

                            if (currency.equals(CurrencyType.EUR.getCurrency())) {

                                Map<CurrencyType, Map<Long, Map<String, Double>>> coinParsedInfoMap = new HashMap<>();

                                //CurrencyType.EUR parsing
                                Map<String, Double> coinEurInfoMap = new HashMap<>();

                                coinsMap.put(coinKeyLong, coinInfoMap);

                                coinEurInfoMap.put("price", (Double) coinQuoteCurrency.get("price"));
                                coinEurInfoMap.put("percent_change_24h", (Double) coinQuoteCurrency.get("percent_change_24h"));
                                coinEurInfoMap.put("percent_change_7d", (Double) coinQuoteCurrency.get("percent_change_7d"));
                                coinEurInfoMap.put("market_cap", (Double) coinQuoteCurrency.get("market_cap"));

                                coinParsedInfoMap.put(CurrencyType.EUR, coinEurInfoMap);

                                //CurrencyType.USD parsing
                                Map<String, Double> coinUsdInfoMap = new HashMap<>();

                                coinQuoteCurrency = (JSONObject) coinQuotes.get(CurrencyType.USD.getCurrency());

                                coinUsdInfoMap.put("price", (Double) coinQuoteCurrency.get("price"));
                                coinUsdInfoMap.put("percent_change_24h", (Double) coinQuoteCurrency.get("percent_change_24h"));
                                coinUsdInfoMap.put("percent_change_7d", (Double) coinQuoteCurrency.get("percent_change_7d"));
                                coinUsdInfoMap.put("market_cap", (Double) coinQuoteCurrency.get("market_cap"));


                            } else {

                                Map<Long, Map<String, Double>> mapCoinParsedInfo = new HashMap<>();

                            }


                            coinInfoMap.put("price", (Double) coinQuoteCurrency.get("price"));
                            coinInfoMap.put("percent_change_24h", (Double) coinQuoteCurrency.get("percent_change_24h"));
                            coinInfoMap.put("percent_change_7d", (Double) coinQuoteCurrency.get("percent_change_7d"));
                            coinInfoMap.put("market_cap", (Double) coinQuoteCurrency.get("market_cap"));

//                            System.out.println("price" + (Double) coinQuoteCurrency.get("price"));
//                            System.out.println("percent_change_24h" + (Double) coinQuoteCurrency.get("percent_change_24h"));
//                            System.out.println("percent_change_7d" + (Double) coinQuoteCurrency.get("percent_change_7d"));
//                            System.out.println("market_cap" + (Double) coinQuoteCurrency.get("market_cap"));


                            mapCoinParsedInfo.put(coinKeyLong, coinInfoMap);

                        }

                        i += 100;
                        System.out.println("i += 100: " + i);
                    }

                } catch (ClassCastException | ParseException ex) {

                    logger.log(Level.WARNING, "Error in parseCoinByCoinsTickerId: {0}", ex);

                }

                urlRequest.setLength(0);


            } else {

                nextParse = false;
            }

            TimeUnit.SECONDS.sleep(5);

        }

        return mapCoinParsedInfo;
    }*/


/*    public static Map<Long, Map<String, Double>> parseCoinByCoinsTickerId(String currency) throws InterruptedException {

        Map<Long, Map<String, Double>> mapCoinParsedInfo = new HashMap<>();

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

            System.out.println("urlRequest: " + urlRequest);

            String inlineString = parseAPIByURL(urlRequest.toString(), "GET");

            if (!inlineString.trim().isEmpty()) {

                //JSONParser reads the data from string object and break each data into key-value pairs
                JSONParser parserJSON = new JSONParser();

                try {

                    JSONObject jsonMainDataObj = (JSONObject) parserJSON.parse(inlineString);
                    JSONObject jsonCoinsObject = (JSONObject) jsonMainDataObj.get("data");

                    if (jsonCoinsObject != null) {

                        for (Object coinKey : jsonCoinsObject.keySet()) {

                            String coinKeyString = (String) coiна ЧернnKey;
                            Long coinKeyLong = Long.valueOf((String) coinKey);

                            // loop to get the dynamic key
                            JSONObject coinObject = (JSONObject) jsonCoinsObject.get(coinKeyString);

                            System.out.println("id " + coinObject.get("id"));

                            JSONObject coinQuotes = (JSONObject) coinObject.get("quotes");

                            JSONObject coinQuoteCurrency = (JSONObject) coinQuotes.get(currency);

                            Map<String, Double> coinInfoMap = new HashMap<>();

                            coinInfoMap.put("price", (Double) coinQuoteCurrency.get("price"));
                            coinInfoMap.put("percent_change_24h", (Double) coinQuoteCurrency.get("percent_change_24h"));
                            coinInfoMap.put("percent_change_7d", (Double) coinQuoteCurrency.get("percent_change_7d"));
                            coinInfoMap.put("market_cap", (Double) coinQuoteCurrency.get("market_cap"));

                            mapCoinParsedInfo.put(coinKeyLong, coinInfoMap);
                        }

                        i += 100;
                        System.out.println("i += 100: " + i);
                    }

                } catch (ClassCastException | ParseException ex) {

                    logger.log(Level.WARNING, "Error in parseCoinByCoinsTickerId", ex);

                }

                urlRequest.setLength(0);


            } else {

                nextParse = false;
            }

            TimeUnit.SECONDS.sleep(5);

        }

        return mapCoinParsedInfo;
    }*/

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
            logger.log(Level.WARNING, "Error in parseAllCoinsInfoByCoinTickerIdForOneCurrency", e);
        }

        return coinsMap;
    }


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
            logger.log(Level.WARNING, "Error in parseAllCoinsInfoByCoinTickerIdForOneCurrency", e);
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

            System.out.println("urlRequest: " + urlRequest);

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

                    logger.log(Level.WARNING, "Error in parseCoinByCoinsTickerId", ex);

                }

                urlRequest.setLength(0);

            } else {

                nextParse = false;
                stringCoinsObject.deleteCharAt(stringCoinsObject.length() - 1);
                stringCoinsObject.append("}");
            }

            i += 100;
            TimeUnit.SECONDS.sleep(5);
        }

        return stringCoinsObject.toString();
    }
}
