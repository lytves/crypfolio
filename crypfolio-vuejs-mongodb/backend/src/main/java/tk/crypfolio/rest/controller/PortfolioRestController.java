package tk.crypfolio.rest.controller;

import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.crypfolio.business.PortfolioService;
import tk.crypfolio.business.UserService;
import tk.crypfolio.common.CurrencyType;
import tk.crypfolio.model.PortfolioEntity;
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
}