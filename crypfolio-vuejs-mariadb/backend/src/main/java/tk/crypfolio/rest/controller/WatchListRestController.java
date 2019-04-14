package tk.crypfolio.rest.controller;

import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.crypfolio.business.CoinService;
import tk.crypfolio.business.UserService;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.model.CoinEntity;
import tk.crypfolio.model.UserEntity;
import tk.crypfolio.model.UserWatchCoinEntity;
import tk.crypfolio.rest.exception.RestApplicationException;
import tk.crypfolio.rest.filter.Authenticator;
import tk.crypfolio.rest.util.JsonResponseBuild;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static tk.crypfolio.rest.SettingsRest.TOKEN_BEARER_PREFIX;
import static tk.crypfolio.rest.util.AuthenticationTokenService.getUserIdFromJWT;

// "/api" root-path is defined in RestApplication
@Path("/")
@RequestScoped
public class WatchListRestController extends Application {

    private static final Logger LOGGER = LogManager.getLogger(WatchListRestController.class);

    // stateless business
    @Context
    private HttpHeaders httpHeaders;

    @Inject
    protected UserService userService;

    @Inject
    protected CoinService coinService;

    @Authenticator
    @PUT
    @Path("/watchlist-coin-currency")
    @Consumes("application/json")
    @Produces("application/json")
    public Response setCoinShowedCurrency(String jsonString) throws Exception {

        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            Long coinId = ((Number) jsonObject.get("coinId")).longValue();
            String currency = (String) jsonObject.get("currency");

            // userId is the same Id for user's portfolio
            String userId = getUserIdFromJWT(httpHeaders.getHeaderString(AUTHORIZATION)
                    .substring(TOKEN_BEARER_PREFIX.length()).trim());

            UserEntity userDB = userService.getUserDBById(Long.valueOf(userId));

            if (userDB != null) {

                UserWatchCoinEntity userWatchCoin = userDB.getUserWatchCoins().stream()
                        .filter(userWatchCoinEntity -> coinId.equals(userWatchCoinEntity.getCoinId().getId()))
                        .findFirst()
                        .orElse(null);

                // check if there is a coin in user's watchlist and currency value is valid CurrencyType
                if (userWatchCoin != null && EnumUtils.isValidEnum(CurrencyType.class, currency)) {

                    userWatchCoin.setShowedCurrency(CurrencyType.valueOf(currency));

                    userService.updateUserDB(userDB);

                    LOGGER.info("WatchListRestController: Successful '/watchlist-coin-currency' request");

                    // generates response with new authentication token (using user ID for Payload)
                    return JsonResponseBuild.generateJsonResponse(null, userDB.getId());
                }
            }

            throw new BadRequestException("There is no coin in user's watchlist with requested ID or passed currency isn't valid");

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }

    @Authenticator
    @PUT
    @Path("/watchlist-add-new-coin")
    @Consumes("application/json")
    @Produces("application/json")
    public Response addNewWatchlistCoin(String jsonString) throws Exception {

        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            Long coinId = ((Number) jsonObject.get("coinId")).longValue();
            String currency = (String) jsonObject.get("currency");

            // userId is the same Id for user's portfolio
            String userId = getUserIdFromJWT(httpHeaders.getHeaderString(AUTHORIZATION)
                    .substring(TOKEN_BEARER_PREFIX.length()).trim());

            UserEntity userDB = userService.getUserDBById(Long.valueOf(userId));

            if (userDB != null) {

                List<CoinEntity> allCoinsDB = coinService.getAllCoinsDB();

                // check if coinId is valid, that is to say that in DB exists a coin with this ID
                CoinEntity coinDB = allCoinsDB.stream().filter(coin -> coinId.equals(coin.getId()))
                        .findFirst()
                        .orElse(null);

                // check if there is a coin in user's watchlist and currency value is valid CurrencyType
                if (coinDB != null && EnumUtils.isValidEnum(CurrencyType.class, currency)) {

                    // if coin isn't yet in the user's watchlist
                    if (userDB.getUserWatchCoins().stream()
                            .filter(userWatchCoinEntity -> coinId.equals(userWatchCoinEntity.getCoinId().getId()))
                            .findFirst().orElse(null) == null) {

                        userDB.addWatchCoin(coinDB, CurrencyType.valueOf(currency));

                        userService.updateUserDB(userDB);

                        LOGGER.info("WatchListRestController: Successful '/watchlist-add-new-coin' request");

                        // generates response with new authentication token (using user ID for Payload)
                        return JsonResponseBuild.generateJsonResponse(null, userDB.getId());

                    } else {

                        LOGGER.warn("WatchListRestController: Request '/watchlist-add-new-coin' - coin is already in watchlist!");

                        // generates response with new authentication token (using user ID for Payload)
                        return JsonResponseBuild.generateJsonResponse(null, userDB.getId(),
                                400, "The coin is already in the watchlist!");
                    }
                }
            }

            throw new BadRequestException("Error on searching user, coin, or passed currency isn't valid");

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }

    @Authenticator
    @DELETE
    @Path("/watchlist-delete-coin/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response deleteWatchlistCoin(@PathParam("id") String id) throws Exception {

        Long coinToDelId = Long.valueOf(id);

        try {

            // userId is the same Id for user's portfolio
            String userId = getUserIdFromJWT(httpHeaders.getHeaderString(AUTHORIZATION)
                    .substring(TOKEN_BEARER_PREFIX.length()).trim());

            UserEntity userDB = userService.getUserDBById(Long.valueOf(userId));

            if (userDB != null) {

                // check if coinId is valid, that is to say that in DB exists a coin with this ID
                UserWatchCoinEntity userWatchCoinEntity = userDB.getUserWatchCoins().stream()
                        .filter(coin -> coin.getCoinId().getId().equals(coinToDelId))
                        .findFirst()
                        .orElse(null);

                if (userWatchCoinEntity != null) {

                    userDB.removeWatchCoin(userWatchCoinEntity);

                    userService.updateUserDB(userDB);

                    LOGGER.info("WatchListRestController: Successful '/watchlist-delete-coin/{id}' request");

                    // generates response with new authentication token (using user ID for Payload)
                    return JsonResponseBuild.generateJsonResponse(null, Long.valueOf(userId));
                }
            }

            throw new BadRequestException("There is no user or the coin with requested Id isn't in user's watchlist!");

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }
}