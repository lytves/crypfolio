package tk.crypfolio.rest.controller;

import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.crypfolio.business.ApplicationContainer;
import tk.crypfolio.business.PortfolioService;
import tk.crypfolio.business.UserService;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.model.UserEntity;
import tk.crypfolio.model.UserWatchCoinEntity;
import tk.crypfolio.rest.exception.RestApplicationException;
import tk.crypfolio.rest.filter.Authenticator;
import tk.crypfolio.rest.util.JsonResponseBuild;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static tk.crypfolio.rest.SettingsRest.TOKEN_BEARER_PREFIX;
import static tk.crypfolio.rest.util.AuthenticationTokenService.getUserIdFromJWT;

// "/api" root-path is defined in RestApplication
@Path("/")
public class WatchListRestController extends Application {

    private static final Logger LOGGER = LogManager.getLogger(WatchListRestController.class);

    // application scoped
    @Inject
    private ApplicationContainer applicationContainer;

    // stateless business
    @Context
    private HttpHeaders httpHeaders;

    // stateless business
    @Inject
    protected UserService userService;

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

            throw new BadRequestException("There is no coin in user's watchlist with requested ID or passed currency isn't valid");

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }
}