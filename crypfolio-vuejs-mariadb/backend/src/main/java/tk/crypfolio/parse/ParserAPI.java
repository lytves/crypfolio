package tk.crypfolio.parse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tk.crypfolio.common.SettingsParseAPI;
import tk.crypfolio.model.CoinEntity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public abstract class ParserAPI {

    private static final Logger LOGGER = LogManager.getLogger(ParserAPI.class);

    /*
     *  Universal method to launch API request
     * */
    public static String parseAPIByURL(String stringURL, String stringMethod) {

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
                LOGGER.error("Error in parseAPIByURL: {0}", responseCode);
            }

        } catch (IOException ex) {
            LOGGER.error("IOException in parseAPIByURL: {0}", ex.getMessage());
        }
        return inlineString.toString();
    }

    /*
     *  Method to get from previously parsed JSON list of ALL CMC COINS and then save it to DB - testing of Sandbox API
     * */
    public static List<CoinEntity> parseJSONAllCoinsListing(@NotNull String inlineString) {

        ArrayList<CoinEntity> listCoins = new ArrayList<>();

        //JSONParser reads the data from string object and break each data into key value pairs
        JSONParser parserJSON = new JSONParser();

        try {

            JSONObject jsonDataObj = (JSONObject) parserJSON.parse(inlineString);
            JSONArray jsonArray = (JSONArray) jsonDataObj.get("data");

            for (Object object : jsonArray) {

                JSONObject jsonCoinObject = (JSONObject) object;

                Long coinId = ((Number) jsonCoinObject.get("id")).longValue();
                String coinName = (String) jsonCoinObject.get("name");
                String coinSymbol = (String) jsonCoinObject.get("symbol");
                String coinSlug = (String) jsonCoinObject.get("slug");

                CoinEntity coin = new CoinEntity(coinId, coinName, coinSymbol, coinSlug);

                listCoins.add(coin);
            }
        } catch (ParseException | ClassCastException ex) {

            LOGGER.error("Exception in parseAllCoin: {0}", ex.getMessage());
        }
        return listCoins;
    }

    /*
     *  Method to get from previously parsed JSON list of ALL CMC COINS to use actual prices information
     * */
    public static Map<Long, Map<String, Double>> parseAllCoinsInfoByCoinTickerIdForOneCurrency(String inlineString, String currency) {

        Map<Long, Map<String, Double>> coinsMap = new HashMap<>();

        //JSONParser reads the data from string object and break each data into key-value pairs
        JSONParser parserJSON = new JSONParser();

        try {

            JSONObject jsonDataObj = (JSONObject) parserJSON.parse(inlineString);
            JSONArray jsonCoinsArray = (JSONArray) jsonDataObj.get("data");

            for (Object object : jsonCoinsArray) {

                JSONObject jsonCoinObject = (JSONObject) object;

                Long coinId = ((Number) jsonCoinObject.get("id")).longValue();

                JSONObject coinQuotes = (JSONObject) jsonCoinObject.get("quote");

                JSONObject coinQuoteCurrency = (JSONObject) coinQuotes.get(currency);

                Map<String, Double> coinInfoMap = new HashMap<>();

                Double coinPrice, coinPercentChange24H, coinPercentChange7D, coinMarketCap;

                if (coinQuoteCurrency.get("price") != null) {
                    coinPrice = ((Number) coinQuoteCurrency.get("price")).doubleValue();
                } else {
                    coinPrice = Double.valueOf("0.0");
                }

                if (coinQuoteCurrency.get("percent_change_24h") != null) {
                    coinPercentChange24H = ((Number) coinQuoteCurrency.get("percent_change_24h")).doubleValue();
                } else {
                    coinPercentChange24H = Double.valueOf("0.0");
                }

                if (coinQuoteCurrency.get("percent_change_7d") != null) {
                    coinPercentChange7D = ((Number) coinQuoteCurrency.get("percent_change_7d")).doubleValue();
                } else {
                    coinPercentChange7D = Double.valueOf("0.0");
                }

                if (coinQuoteCurrency.get("market_cap") != null) {
                    coinMarketCap = ((Number) coinQuoteCurrency.get("market_cap")).doubleValue();
                } else {
                    coinMarketCap = Double.valueOf("0.0");
                }

                coinInfoMap.put("price", coinPrice);
                coinInfoMap.put("percent_change_24h", coinPercentChange24H);
                coinInfoMap.put("percent_change_7d", coinPercentChange7D);
                coinInfoMap.put("market_cap", coinMarketCap);

                coinsMap.put(coinId, coinInfoMap);
            }
        } catch (ParseException | ClassCastException ex) {
            LOGGER.error("Error in parseAllCoinsInfoByCoinTickerIdForOneCurrency" + ex.toString());
            ex.printStackTrace();
        }
        return coinsMap;
    }

    /*
     * Method to parse BITCOIN historical data from Cryptocompare API
     * is used to recount bought prices in transactions
     * */
    public static Map<String, Double> parseBitcoiHistoricalPrice(LocalDate localDate) {

        Map<String, Double> bitcoinHistoricalPrice = new HashMap<>();

        // we will always compare with bitcoin, therefore it price is always 1.0 here
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

        LOGGER.info("parseBitcoiHistoricalPrice urlRequest " + urlRequest);

        String inlineString = parseAPIByURL(urlRequest.toString(), "GET");

        if (!inlineString.trim().isEmpty()) {

            //JSONParser reads the data from string object and break each data into key-value pairs
            JSONParser parserJSON = new JSONParser();

            try {

                JSONObject jsonMainDataObj = (JSONObject) parserJSON.parse(inlineString);

                JSONObject jsonBTCObject = (JSONObject) jsonMainDataObj.get("BTC");

                if (jsonBTCObject != null) {

                    for (Object pricePair : jsonBTCObject.keySet()) {

                        String priceCurrency = (String) pricePair;

                        Double priceValue;

                        if (jsonBTCObject.get(priceCurrency) != null) {
                            priceValue = ((Number) jsonBTCObject.get(priceCurrency)).doubleValue();
                        } else {
                            priceValue = Double.valueOf("0.0");
                        }

                        bitcoinHistoricalPrice.put(priceCurrency, priceValue);
                    }
                }
            } catch (ClassCastException | ParseException ex) {

                LOGGER.error("Exception in parseCoinByCoinsTickerId" + ex);
            }
        }
        return bitcoinHistoricalPrice;
    }

    /*
     *  Method to get from previously parsed JSON list of ALL CMC COINS and take additional coin's data
     * */
    public static Map<Long, Map<String, Double>> parseAllCoinsAdditonalDataByCoinTicker(String inlineString) {

        Map<Long, Map<String, Double>> coinsMap = new HashMap<>();

        //JSONParser reads the data from string object and break each data into key-value pairs
        JSONParser parserJSON = new JSONParser();

        try {

            JSONObject jsonDataObj = (JSONObject) parserJSON.parse(inlineString);
            JSONArray jsonCoinsArray = (JSONArray) jsonDataObj.get("data");

            for (Object object : jsonCoinsArray) {

                JSONObject jsonCoinObject = (JSONObject) object;

                Long coinId = ((Number) jsonCoinObject.get("id")).longValue();

                Map<String, Double> coinInfoMap = new HashMap<>();

                Double coinCirculatingSupply, coinTotalSupply, coinCmcRank;

                if (jsonCoinObject.get("circulating_supply") != null) {
                    coinCirculatingSupply = ((Number) jsonCoinObject.get("circulating_supply")).doubleValue();
                } else {
                    coinCirculatingSupply = Double.valueOf("0.0");
                }

                if (jsonCoinObject.get("total_supply") != null) {
                    coinTotalSupply = ((Number) jsonCoinObject.get("total_supply")).doubleValue();
                } else {
                    coinTotalSupply = Double.valueOf("0.0");
                }

                if (jsonCoinObject.get("cmc_rank") != null) {
                    coinCmcRank = ((Number) jsonCoinObject.get("cmc_rank")).doubleValue();
                } else {
                    coinCmcRank = Double.valueOf("0.0");
                }

                coinInfoMap.put("circulating_supply", coinCirculatingSupply);
                coinInfoMap.put("total_supply", coinTotalSupply);
                coinInfoMap.put("cmc_rank", coinCmcRank);

                coinsMap.put(coinId, coinInfoMap);
            }

        } catch (Exception ex) {
            LOGGER.error("Error in parseAllCoinsAdditonalDataByCoinTicker - " + ex.toString());
        }
        return coinsMap;
    }

    public static Map<String, Double> parseGlobalMarketDataCMC(String inlineString) {

        Map<String, Double> globalMarketData = new HashMap<>();

        //JSONParser reads the data from string object and break each data into key-value pairs
        JSONParser parserJSON = new JSONParser();

        try {

            JSONObject jsonDataObj = (JSONObject) parserJSON.parse(inlineString);

            if (jsonDataObj != null) {

                Double totalMarketCapUsd, total24hVolumeUsd, bitcoinPercentageOfMarketCap;

                if (jsonDataObj.get("total_market_cap_usd") != null) {
                    totalMarketCapUsd = ((Number) jsonDataObj.get("total_market_cap_usd")).doubleValue();
                } else {
                    totalMarketCapUsd = Double.valueOf("0.0");
                }

                if (jsonDataObj.get("total_24h_volume_usd") != null) {
                    total24hVolumeUsd = ((Number) jsonDataObj.get("total_24h_volume_usd")).doubleValue();
                } else {
                    total24hVolumeUsd = Double.valueOf("0.0");
                }

                if (jsonDataObj.get("bitcoin_percentage_of_market_cap") != null) {
                    bitcoinPercentageOfMarketCap = ((Number) jsonDataObj.get("bitcoin_percentage_of_market_cap")).doubleValue();
                } else {
                    bitcoinPercentageOfMarketCap = Double.valueOf("0.0");
                }

                globalMarketData.put("total_market_cap_usd", totalMarketCapUsd);
                globalMarketData.put("total_24h_volume_usd", total24hVolumeUsd);
                globalMarketData.put("bitcoin_percentage_of_market_cap", bitcoinPercentageOfMarketCap);

                System.out.println(globalMarketData);
            }

        } catch (
                Exception ex) {
            LOGGER.error("Error in parseGlobalMarketDataCMC - " + ex.toString());
        }

        return globalMarketData;
    }
}