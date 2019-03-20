package tk.crypfolio.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.crypfolio.business.CoinService;
import tk.crypfolio.business.PortfolioService;
import tk.crypfolio.business.UserService;
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

import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static tk.crypfolio.rest.SettingsRest.TOKEN_BEARER_PREFIX;
import static tk.crypfolio.rest.util.AuthenticationTokenService.getUserIdFromJWT;

// "/api" root-path is defined in RestApplication
@Path("/")
public class PortfolioRestController extends Application {

    private static final Logger LOGGER = LogManager.getLogger(PortfolioRestController.class);

    // stateless business
    @Context
    private HttpHeaders httpHeaders;

    // stateless business
    @Inject
    protected UserService userService;

    // stateless business
    @Inject
    protected PortfolioService portfolioService;

    // stateless business
    @Inject
    protected CoinService coinService;

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
                if (transAmount.compareTo(BigDecimal.ZERO) == 0 || isBigDecimalVaildForDB(transAmount)
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
                    PortfolioEntity newPortfolio = portfolioService.updatePortfolioDB(portfolioDB);

                    itemDB = newPortfolio.getItems().stream().filter(
                            itemEntity -> itemEntity.getCoin().getId().equals(transCoinId)
                    ).findFirst().orElse(null);

                    if (itemDB != null && itemDB.getCoin().getId() != null) {

                        LOGGER.info("PortfolioRestController: Successful '/portfolio-add-transaction' request");

                        ObjectMapper mapper = new ObjectMapper();

                        String portfolioNetCosts = Json.createObjectBuilder()
                                .add("netCostUsd", portfolioDB.getNetCostUsd())
                                .add("netCostEur", portfolioDB.getNetCostEur())
                                .add("netCostBtc", portfolioDB.getNetCostBtc())
                                .add("netCostEth", portfolioDB.getNetCostEth())
                                .build()
                                .toString();

                        // form a JSON object consists of new portfolio NET costs data && actual actualized item
                        String jsonToSend = Json.createObjectBuilder()
                                .add("portfolioNetCosts", portfolioNetCosts)
                                .add("actualizedItem", mapper.writeValueAsString(itemDB))
                                .build()
                                .toString();

                        // generates response with new authentication token (using portfolio ID for Payload)
                        return JsonResponseBuild.generateJsonResponse(jsonToSend, portfolioDB.getId());
                    }
                }
            }

            throw new BadRequestException("There is no user with requested ID or passed transaction parameters  aren't valid");

        } catch (
                Exception ex) {

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