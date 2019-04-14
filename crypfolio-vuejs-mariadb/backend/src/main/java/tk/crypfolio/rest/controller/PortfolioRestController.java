package tk.crypfolio.rest.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.crypfolio.business.CoinService;
import tk.crypfolio.business.PortfolioService;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.common.Settings;
import tk.crypfolio.common.TransactionType;
import tk.crypfolio.model.CoinEntity;
import tk.crypfolio.model.ItemEntity;
import tk.crypfolio.model.PortfolioEntity;
import tk.crypfolio.model.TransactionEntity;
import tk.crypfolio.parse.ParserAPI;
import tk.crypfolio.rest.exception.RestApplicationException;
import tk.crypfolio.rest.filter.Authenticator;
import tk.crypfolio.rest.util.JsonResponseBuild;
import tk.crypfolio.util.LocalDateAdapter;
import tk.crypfolio.util.LocalDateTimeAdapter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static tk.crypfolio.rest.SettingsRest.TOKEN_BEARER_PREFIX;
import static tk.crypfolio.rest.util.AuthenticationTokenService.getUserIdFromJWT;

// "/api" root-path is defined in RestApplication
@Path("/")
@RequestScoped
public class PortfolioRestController extends Application {

    private static final Logger LOGGER = LogManager.getLogger(PortfolioRestController.class);

    // stateless business
    @Context
    private HttpHeaders httpHeaders;

    @Inject
    private PortfolioService portfolioService;

    @Inject
    private CoinService coinService;

    @Authenticator
    @PUT
    @Path("/portfolio-currency")
    @Consumes("application/json")
    @Produces("application/json")
    public Response setPortfolioShowedCurrency(String jsonString) throws Exception {

        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            String currency = (String) jsonObject.get("currency");

            // userId is the same Id for user's portfolio
            String userId = getUserIdFromJWT(httpHeaders.getHeaderString(AUTHORIZATION)
                    .substring(TOKEN_BEARER_PREFIX.length()).trim());

            PortfolioEntity portfolioDB = portfolioService.getPortfolioDBById(Long.valueOf(userId));

            // check if there is a portfolio and currency value is valid CurrencyType
            if (portfolioDB != null && EnumUtils.isValidEnum(CurrencyType.class, currency)) {

                portfolioDB.setShowedCurrency(CurrencyType.valueOf(currency));

                portfolioService.updatePortfolioDB(portfolioDB);

                LOGGER.info("PortfolioRestController: Successful '/portfolio-currency' request");

                // generates response with new authentication token (using portfolio ID for Payload)
                return JsonResponseBuild.generateJsonResponse(null, portfolioDB.getId());
            }

            throw new BadRequestException("There is no portfolio with requested ID or passed currency isn't valid");

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }

    @Authenticator
    @PUT
    @Path("/portfolio-item-currency")
    @Consumes("application/json")
    @Produces("application/json")
    public Response setItemShowedCurrency(String jsonString) throws Exception {

        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            Long coinId = ((Number) jsonObject.get("coinId")).longValue();
            String currency = (String) jsonObject.get("currency");

            // userId is the same Id for user's portfolio
            String userId = getUserIdFromJWT(httpHeaders.getHeaderString(AUTHORIZATION)
                    .substring(TOKEN_BEARER_PREFIX.length()).trim());

            PortfolioEntity portfolioDB = portfolioService.getPortfolioDBById(Long.valueOf(userId));

            // check if coinId is valid, that is to say that in DB exists a coin with this ID
            CoinEntity coinDB = coinService.getAllCoinsDB().stream().filter(coin -> coinId.equals(coin.getId()))
                    .findFirst().orElse(null);

            if (portfolioDB != null && coinDB != null) {

                // look up an item with passed coinId
                ItemEntity itemDB = portfolioDB.getItems().stream()
                        .filter(itemEntity -> coinId.equals(itemEntity.getCoin().getId()))
                        .findFirst()
                        .orElse(null);

                // check if there is a coin in user's watchlist and currency value is valid CurrencyType
                if (itemDB != null && EnumUtils.isValidEnum(CurrencyType.class, currency)) {

                    itemDB.setShowedCurrency(CurrencyType.valueOf(currency));

                    portfolioService.updatePortfolioDB(portfolioDB);

                    LOGGER.info("PortfolioRestController: Successful '/portfolio-item-currency' request");

                    // generates response with new authentication token (using user ID for Payload)
                    return JsonResponseBuild.generateJsonResponse(null, portfolioDB.getId());
                }
            }

            throw new BadRequestException("There is no item in user's portfolio with requested coin ID or passed currency isn't valid");

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }

    @Authenticator
    @POST
    @Path("/portfolio-add-transaction")
    @Consumes("application/json")
    @Produces("application/json")
    public Response addTransaction(String jsonString) throws Exception {

        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            Long transCoinId = ((Number) jsonObject.get("transCoinId")).longValue();
            CurrencyType transCurrency = CurrencyType.valueOf(((String) jsonObject.get("transCurrency")).toUpperCase());
            TransactionType transType = TransactionType.valueOf(((String) jsonObject.get("transType")).toUpperCase());
            BigDecimal transAmount = new BigDecimal((String) jsonObject.get("transAmount"));
            BigDecimal transTempPrice = new BigDecimal((String) jsonObject.get("transPrice"));
            LocalDate transDate = LocalDate.parse((String) jsonObject.get("transDate"));
            String transComment = (String) jsonObject.get("transComment");

            // userId is the same Id for user's portfolio
            String userId = getUserIdFromJWT(httpHeaders.getHeaderString(AUTHORIZATION)
                    .substring(TOKEN_BEARER_PREFIX.length()).trim());

            PortfolioEntity portfolioDB = portfolioService.getPortfolioDBById(Long.valueOf(userId));

            // check if coinId is valid, that is to say that in DB exists a coin with this ID
            CoinEntity coinDB = coinService.getAllCoinsDB().stream().filter(coin -> transCoinId.equals(coin.getId()))
                    .findFirst().orElse(null);

            if (portfolioDB != null && coinDB != null) {

                ItemEntity itemDB = portfolioDB.getItems().stream().filter(item -> item.getCoin().getId().equals(transCoinId))
                        .findFirst().orElse(null);

                if (itemDB == null) {
                    itemDB = new ItemEntity(coinDB);
                }

                // checking if all received transaction values are valid
                if (transAmount.compareTo(BigDecimal.ZERO) == 0
                        || isBigDecimalVaildForDB(transAmount)
                        || isBigDecimalVaildForDB(transTempPrice)
                        || isBigDecimalVaildForDB(transAmount.multiply(transTempPrice))) {

                    throw new BadRequestException("Passed transaction parameters  aren't valid");
                } else if (("SELL").equals(transType.getType())
                        && transAmount.compareTo(itemDB.getAmount()) >= 1) {
                    // generates response with new authentication token (using portfolio ID for Payload)
                    return JsonResponseBuild.generateJsonResponse(null, portfolioDB.getId(),
                            400, "Error on processing your transaction! You can't sell more coins than you have!");
                } else {

                    TransactionEntity newTransaction = new TransactionEntity(transAmount, transDate, transCurrency);
                    newTransaction.setComment(transComment);
                    newTransaction.setType(transType);

                    // recount prices in all currencies by request historical prices by API
                    recountTransactionPricesByHistoricalPricesAPI(newTransaction, transTempPrice);

                    // also recount values (net costs, average prices) of item
                    itemDB.addTransaction(newTransaction);

                    // if it's new item in the portfolio
                    if (itemDB.getId() == null) {

                        itemDB.setShowedCurrency(newTransaction.getBoughtCurrency());

                        portfolioDB.addItem(itemDB);
                    }

                    // recount values (netcost) of portfolio
                    portfolioDB.recountNetCosts();

                    // updates whole portfolio entity
                    portfolioDB = portfolioService.updatePortfolioDB(portfolioDB);

                    itemDB = portfolioDB.getItems().stream().filter(
                            itemEntity -> itemEntity.getCoin().getId().equals(transCoinId)
                    ).findFirst().orElse(null);

                    if (itemDB != null && itemDB.getCoin().getId() != null) {

                        LOGGER.info("PortfolioRestController: Successful '/portfolio-add-transaction' request");

                        JsonObject portfolioNetCosts = new JsonObject();

                        portfolioNetCosts.addProperty("netCostUsd", portfolioDB.getNetCostUsd());
                        portfolioNetCosts.addProperty("netCostEur", portfolioDB.getNetCostEur());
                        portfolioNetCosts.addProperty("netCostBtc", portfolioDB.getNetCostBtc());
                        portfolioNetCosts.addProperty("netCostEth", portfolioDB.getNetCostEth());

                        JsonObject jsonToSend = new JsonObject();

                        jsonToSend.add("portfolioNetCosts", portfolioNetCosts);

                        // Convert object to JSON element
                        Gson gsonBuilder = new GsonBuilder()
                                .excludeFieldsWithoutExposeAnnotation()
                                .disableHtmlEscaping()
                                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                .create();
                        jsonToSend.add("actualizedItem", gsonBuilder.toJsonTree(itemDB));

                        // generates response with new authentication token (using portfolio ID for Payload)
                        return JsonResponseBuild.generateJsonResponse(jsonToSend, portfolioDB.getId());
                    }
                }
            }

            throw new BadRequestException("There is no user with requested ID or passed transaction parameters aren't valid");

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }

    @Authenticator
    @POST
    @Path("/portfolio-edit-transaction")
    @Consumes("application/json")
    @Produces("application/json")
    public Response editTransaction(String jsonString) throws Exception {

        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            Long transId = ((Number) jsonObject.get("transId")).longValue();
            CurrencyType transCurrency = CurrencyType.valueOf(((String) jsonObject.get("transCurrency")).toUpperCase());
            TransactionType transType = TransactionType.valueOf(((String) jsonObject.get("transType")).toUpperCase());
            BigDecimal transAmount = new BigDecimal((String) jsonObject.get("transAmount"));
            BigDecimal transTempPrice = new BigDecimal((String) jsonObject.get("transPrice"));
            LocalDate transDate = LocalDate.parse((String) jsonObject.get("transDate"));
            String transComment = (String) jsonObject.get("transComment");
            Long itemId = ((Number) jsonObject.get("itemId")).longValue();

            // userId is the same Id for user's portfolio
            String userId = getUserIdFromJWT(httpHeaders.getHeaderString(AUTHORIZATION)
                    .substring(TOKEN_BEARER_PREFIX.length()).trim());

            PortfolioEntity portfolioDB = portfolioService.getPortfolioDBById(Long.valueOf(userId));

            if (portfolioDB != null) {

                ItemEntity itemDB = portfolioDB.getItems().stream().filter(item -> item.getId().equals(itemId))
                        .findFirst().orElse(null);

                if (itemDB != null) {

                    TransactionEntity transactionEditedOld = itemDB.getTransactions().stream().filter(
                            trans -> trans.getId().equals(transId)).findFirst().orElse(null);

                    if (transactionEditedOld != null) {

                        // checking if all received transaction values are valid
                        if (transAmount.compareTo(BigDecimal.ZERO) == 0
                                || !transactionEditedOld.getType().equals(transType)
                                || isBigDecimalVaildForDB(transAmount)
                                || isBigDecimalVaildForDB(transTempPrice)
                                || isBigDecimalVaildForDB(transAmount.multiply(transTempPrice))) {

                            return JsonResponseBuild.generateJsonResponse(null, portfolioDB.getId(),
                                    400, "Error on processing your transaction! Some of entered values are not valid!");

                        } else if (("SELL").equals(transType.getType())
                                && transAmount.compareTo(itemDB.getAmount().add(transactionEditedOld.getAmount())) >= 1) {

                            // generates response with new authentication token (using portfolio ID for Payload)
                            return JsonResponseBuild.generateJsonResponse(null, portfolioDB.getId(),
                                    400, "Error on processing your transaction! You can't sell more coins than you have!");
                        } else {

                            // to clone exists edited transaction!!! important to have all foreign keys equals
                            TransactionEntity newTransaction = SerializationUtils.clone(transactionEditedOld);

                            newTransaction.setAmount(transAmount);
                            newTransaction.setBoughtDate(transDate);
                            newTransaction.setBoughtCurrency(transCurrency);
                            newTransaction.setComment(transComment);

                            // recount prices in all currencies by request historical prices by API
                            recountTransactionPricesByHistoricalPricesAPI(newTransaction, transTempPrice);

                            // also recount values (net costs, average prices) of item
                            Boolean isTransactionValid = itemDB.editTransaction(transactionEditedOld, newTransaction);

                            if (isTransactionValid) {

                                // recount values (netcost) of portfolio
                                portfolioDB.recountNetCosts();

                                // updates whole portfolio entity
                                portfolioDB = portfolioService.updatePortfolioDB(portfolioDB);

                                itemDB = portfolioDB.getItems().stream().filter(item -> item.getId().equals(itemId))
                                        .findFirst().orElse(null);

                                if (itemDB != null) {

                                    LOGGER.info("PortfolioRestController: Successful '/portfolio-edit-transaction' request");

                                    JsonObject portfolioNetCosts = new JsonObject();

                                    portfolioNetCosts.addProperty("netCostUsd", portfolioDB.getNetCostUsd());
                                    portfolioNetCosts.addProperty("netCostEur", portfolioDB.getNetCostEur());
                                    portfolioNetCosts.addProperty("netCostBtc", portfolioDB.getNetCostBtc());
                                    portfolioNetCosts.addProperty("netCostEth", portfolioDB.getNetCostEth());

                                    JsonObject jsonToSend = new JsonObject();

                                    jsonToSend.add("portfolioNetCosts", portfolioNetCosts);

                                    // Convert object to JSON element
                                    Gson gsonBuilder = new GsonBuilder()
                                            .excludeFieldsWithoutExposeAnnotation()
                                            .disableHtmlEscaping()
                                            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                            .create();
                                    jsonToSend.add("actualizedItem", gsonBuilder.toJsonTree(itemDB));

                                    // generates response with new authentication token (using portfolio ID for Payload)
                                    return JsonResponseBuild.generateJsonResponse(jsonToSend, portfolioDB.getId());
                                }
                            }
                        }
                    }
                }
            }

            throw new BadRequestException("There is no user with requested ID or passed transaction parameters aren't valid");

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }

    @Authenticator
    @DELETE
    @Path("/portfolio-delete-transaction")
    @Consumes("application/json")
    @Produces("application/json")
    public Response deleteTransaction(String jsonString) throws Exception {

        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            Long itemId = ((Number) jsonObject.get("itemId")).longValue();
            Long transId = ((Number) jsonObject.get("transId")).longValue();

            // userId is the same Id for user's portfolio
            String userId = getUserIdFromJWT(httpHeaders.getHeaderString(AUTHORIZATION)
                    .substring(TOKEN_BEARER_PREFIX.length()).trim());

            PortfolioEntity portfolioDB = portfolioService.getPortfolioDBById(Long.valueOf(userId));

            if (portfolioDB != null) {

                ItemEntity itemDB = portfolioDB.getItems().stream().filter(item -> item.getId().equals(itemId))
                        .findFirst().orElse(null);

                if (itemDB != null) {

                    TransactionEntity transactionEntity = itemDB.getTransactions().stream()
                            .filter(trans -> trans.getId().equals(transId)).findFirst().orElse(null);

                    if (transactionEntity != null) {

                        Boolean isTransactionValid = itemDB.removeTransaction(transactionEntity);

                        if (isTransactionValid) {

                            // recount values (netcost) of portfolio
                            portfolioDB.recountNetCosts();

                            // updates whole portfolio entity
                            portfolioDB = portfolioService.updatePortfolioDB(portfolioDB);

                            LOGGER.info("PortfolioRestController: Successful '/portfolio-delete-transaction' request");

                            JsonObject portfolioNetCosts = new JsonObject();

                            portfolioNetCosts.addProperty("netCostUsd", portfolioDB.getNetCostUsd());
                            portfolioNetCosts.addProperty("netCostEur", portfolioDB.getNetCostEur());
                            portfolioNetCosts.addProperty("netCostBtc", portfolioDB.getNetCostBtc());
                            portfolioNetCosts.addProperty("netCostEth", portfolioDB.getNetCostEth());

                            JsonObject jsonToSend = new JsonObject();

                            jsonToSend.add("portfolioNetCosts", portfolioNetCosts);

                            // Convert object to JSON element
                            Gson gsonBuilder = new GsonBuilder()
                                    .excludeFieldsWithoutExposeAnnotation()
                                    .disableHtmlEscaping()
                                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                    .create();
                            jsonToSend.add("actualizedItem", gsonBuilder.toJsonTree(itemDB));

                            // generates response with new authentication token (using portfolio ID for Payload)
                            return JsonResponseBuild.generateJsonResponse(jsonToSend, portfolioDB.getId());
                        }
                    }
                }
            }

            throw new BadRequestException("There is no user with requested ID or passed transaction parameters aren't valid");

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }

    @Authenticator
    @DELETE
    @Path("/portfolio-delete-item/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response deleteItem(@PathParam("id") String id) throws Exception {

        Long itemToDelId = Long.valueOf(id);

        try {

            // userId is the same Id for user's portfolio
            String userId = getUserIdFromJWT(httpHeaders.getHeaderString(AUTHORIZATION)
                    .substring(TOKEN_BEARER_PREFIX.length()).trim());

            PortfolioEntity portfolioDB = portfolioService.getPortfolioDBById(Long.valueOf(userId));

            ItemEntity itemDB = null;

            if (portfolioDB != null) {

                itemDB = portfolioDB.getItems().stream().filter(i -> i.getId().equals(itemToDelId))
                        .findFirst().orElse(null);
            }

            // search if the item with passed Id is in the user's portfolio
            if (itemDB != null) {

                portfolioDB.removeItem(itemDB);

                portfolioDB.recountNetCosts();

                // updates whole portfolio entity
                portfolioDB = portfolioService.updatePortfolioDB(portfolioDB);

                JsonObject portfolioNetCosts = new JsonObject();

                portfolioNetCosts.addProperty("netCostUsd", portfolioDB.getNetCostUsd());
                portfolioNetCosts.addProperty("netCostEur", portfolioDB.getNetCostEur());
                portfolioNetCosts.addProperty("netCostBtc", portfolioDB.getNetCostBtc());
                portfolioNetCosts.addProperty("netCostEth", portfolioDB.getNetCostEth());

                JsonObject jsonToSend = new JsonObject();

                jsonToSend.add("portfolioNetCosts", portfolioNetCosts);

                LOGGER.info("PortfolioRestController: Successful '/portfolio-delete-item/{id}' request");

                // generates response with new authentication token (using user ID for Payload)
                return JsonResponseBuild.generateJsonResponse(jsonToSend, portfolioDB.getId());
            }

            throw new BadRequestException("There is no user or the item with requested Id isn't in user's portfolio!");

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }

    // utility methods
    // check if BigDecimal value corresponds to settings max value
    private boolean isBigDecimalVaildForDB(@NotNull BigDecimal transactionTemp) {

        return transactionTemp.compareTo(new BigDecimal(Settings.STRING_MAX_BIGDECIMAL_VALUE)) >= 1;
    }

    /*
     * universal method to recount transaction prices in all currencies depends of the bought currency and
     * with respect to the historical prices of USD/EUR/BTC/ETH
     * */
    private void recountTransactionPricesByHistoricalPricesAPI(TransactionEntity transaction, BigDecimal typedPrice) {

        // makes API request to obtain history USD/EUR/BTC/ETH prices
        Map<String, Double> bitcoinHistoricalPrice = ParserAPI.parseBitcoiHistoricalPrice(transaction.getBoughtDate());

        // recounts and sets prices in all currencies of the transaction
        Double coefficientMultiplier;

        switch (transaction.getBoughtCurrency().getCurrency()) {

            case "USD":

                // USD
                transaction.setBoughtPriceUsd(typedPrice);

                // EUR
                coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("USD");

                transaction.setBoughtPriceEur(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // BTC
                coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("USD");

                transaction.setBoughtPriceBtc(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // ETH
                coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("USD");

                transaction.setBoughtPriceEth(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                break;

            case "EUR":

                // USD
                coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("EUR");

                transaction.setBoughtPriceUsd(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // EUR
                transaction.setBoughtPriceEur(typedPrice);

                // BTC
                coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("EUR");

                transaction.setBoughtPriceBtc(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // ETH
                coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("EUR");

                transaction.setBoughtPriceEth(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                break;

            case "BTC":

                // USD
                coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("BTC");

                transaction.setBoughtPriceUsd(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // EUR
                coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("BTC");

                transaction.setBoughtPriceEur(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // BTC
                transaction.setBoughtPriceBtc(typedPrice);

                // ETH
                coefficientMultiplier = bitcoinHistoricalPrice.get("ETH") / bitcoinHistoricalPrice.get("BTC");

                transaction.setBoughtPriceEth(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                break;

            case "ETH":

                // USD
                coefficientMultiplier = bitcoinHistoricalPrice.get("USD") / bitcoinHistoricalPrice.get("ETH");

                transaction.setBoughtPriceUsd(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // EUR
                coefficientMultiplier = bitcoinHistoricalPrice.get("EUR") / bitcoinHistoricalPrice.get("ETH");

                transaction.setBoughtPriceEur(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // BTC
                coefficientMultiplier = bitcoinHistoricalPrice.get("BTC") / bitcoinHistoricalPrice.get("ETH");

                transaction.setBoughtPriceBtc(typedPrice.multiply(BigDecimal.valueOf(coefficientMultiplier))
                        .setScale(8, BigDecimal.ROUND_HALF_DOWN));

                // ETH
                transaction.setBoughtPriceEth(typedPrice);

                break;
        }
    }
}