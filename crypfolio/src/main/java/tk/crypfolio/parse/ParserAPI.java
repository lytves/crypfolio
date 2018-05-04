package tk.crypfolio.parse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tk.crypfolio.common.SettingsParseAPI;
import tk.crypfolio.model.CoinEntity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParserAPI {

    private final Logger logger = Logger.getLogger(ParserAPI.class.getName());

    /*
     *  Universal method to launch API request
     * */
    private String parseAPIByURL(String stringURL, String stringMethod) {

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
     * Method to receive ALL COINS list from CoinMarketCap API
     * */
    public List<CoinEntity> parseAllCoin() {

        ArrayList<CoinEntity> listCoins = new ArrayList<>();

        String inlineString = parseAPIByURL(SettingsParseAPI.ALL_COINS_LIST, "GET");

        if (inlineString != null && !inlineString.trim().isEmpty()) {

            //JSONParser reads the data from string object and break each data into key value pairs
            JSONParser parserJSON = new JSONParser();

            try {

                JSONArray jsonArray = (JSONArray) parserJSON.parse(inlineString);

                for (Object jsonObject : jsonArray) {

                    //Get the index of the JSON objects and print the values as per the index
                    JSONObject jsonObj = (JSONObject) jsonObject;

                    String coinName = (String) jsonObj.get("name");
                    String coinSymbol = (String) jsonObj.get("symbol");
                    String coinApiId = (String) jsonObj.get("id");

                    CoinEntity coin = new CoinEntity(coinName, coinSymbol, coinApiId);

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
}
