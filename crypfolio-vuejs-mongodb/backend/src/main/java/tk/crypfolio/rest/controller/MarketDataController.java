package tk.crypfolio.rest.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.business.ApplicationContainer;
import tk.crypfolio.business.UserService;
import tk.crypfolio.model.ItemEntity;
import tk.crypfolio.model.UserEntity;
import tk.crypfolio.model.UserWatchCoinEntity;
import tk.crypfolio.rest.exception.RestApplicationException;
import tk.crypfolio.rest.filter.Authenticator;
import tk.crypfolio.rest.util.JsonResponseBuild;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static tk.crypfolio.rest.SettingsRest.TOKEN_BEARER_PREFIX;
import static tk.crypfolio.rest.util.AuthenticationTokenService.getUserIdFromJWT;

// "/api" root-path is defined in RestApplication
@Path("/")
public class MarketDataController extends Application {

    private static final Logger LOGGER = LogManager.getLogger(MarketDataController.class);

    // application scoped
    @Inject
    private ApplicationContainer applicationContainer;

    // stateless business
    @Context
    private HttpHeaders httpHeaders;

    // stateless business
    @Inject
    protected UserService userService;

    /**
     * Returns a JSON string with actual market data of all coins that user has en portfolio and watchlist,
     * for actualize data on the frontend
     */
    @Authenticator
    @GET
    @Path("/user-coins-data")
    @Consumes("application/json")
    @Produces("application/json")
    public Response getAllUserCoinsData() throws Exception {

        try {

            // userId is the same Id for user's portfolio
            String userId = getUserIdFromJWT(httpHeaders.getHeaderString(AUTHORIZATION)
                    .substring(TOKEN_BEARER_PREFIX.length()).trim());

            UserEntity userDB = userService.getUserDBById(Long.valueOf(userId));

            // create a set with all coins IDs to be parsed and which actual data have to send to user
            Set<Long> allUserCoinsIds = new HashSet<>();

            // get all user's portfolio coins IDs
            for (ItemEntity item : userDB.getPortfolio().getItems()) {
                allUserCoinsIds.add(item.getCoin().getId());
            }
            // get all user's watchlist coins IDs
            for (UserWatchCoinEntity userWatchCoin : userDB.getUserWatchCoins()) {
                allUserCoinsIds.add(userWatchCoin.getCoinId().getId());
            }

            // a result Map with all actual market coins data, groups by coins ID
            Map<Long, Map<String, Map<String, Double>>> allCoinsByIdData = new HashMap<>();

            for (Long coinId : allUserCoinsIds) {

                // for the first time need to create a Map with coin ID as key and first currency like a Map with data,
                // and then other currencies will add to exists map
                Map<String, Map<String, Double>> coinsByIdInUsd = new HashMap<>();

                for (Map.Entry<Long, Map<String, Double>> pair : applicationContainer.getAllCoinsByTickerInUsd().entrySet()) {

                    if (pair.getKey().equals(coinId)) {
                        coinsByIdInUsd.put("USD", pair.getValue());
                        allCoinsByIdData.put(coinId, coinsByIdInUsd);
                        break;
                    }
                }

                for (Map.Entry<Long, Map<String, Double>> pair : applicationContainer.getAllCoinsByTickerInEur().entrySet()) {

                    if (pair.getKey().equals(coinId)) {
                        allCoinsByIdData.get(coinId).put("EUR", pair.getValue());
                        break;
                    }
                }

                for (Map.Entry<Long, Map<String, Double>> pair : applicationContainer.getAllCoinsByTickerInBtc().entrySet()) {

                    if (pair.getKey().equals(coinId)) {
                        allCoinsByIdData.get(coinId).put("BTC", pair.getValue());
                        break;
                    }
                }

                for (Map.Entry<Long, Map<String, Double>> pair : applicationContainer.getAllCoinsByTickerInEth().entrySet()) {

                    if (pair.getKey().equals(coinId)) {
                        allCoinsByIdData.get(coinId).put("ETH", pair.getValue());
                        break;
                    }
                }
            }

            LOGGER.info("MarketDataController: Successful '/user-coins-data' request");

            // generates response with new authentication token (using portfolio=user ID for Payload)
            return JsonResponseBuild.generateJsonResponse(allCoinsByIdData, Long.valueOf(userId));

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }

    @Authenticator
    @GET
    @Path("/all-coins-list-data")
    @Consumes("application/json")
    @Produces("application/json")
    public Response getAllCoinsListData() throws Exception {

        try {

            // userId is the same Id for user's portfolio
            String userId = getUserIdFromJWT(httpHeaders.getHeaderString(AUTHORIZATION)
                    .substring(TOKEN_BEARER_PREFIX.length()).trim());

            LOGGER.info("MarketDataController: Successful '/all-coins-list-data' request");

            // generates response with new authentication token (using portfolio=user ID for Payload)
            return JsonResponseBuild.generateJsonResponse(applicationContainer.getAllCoinsListing(), Long.valueOf(userId));

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }
}