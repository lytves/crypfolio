package tk.crypfolio.rest.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import tk.crypfolio.business.UserService;
import tk.crypfolio.model.PortfolioEntity;
import tk.crypfolio.model.UserEntity;
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

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static tk.crypfolio.rest.SettingsRest.TOKEN_BEARER_PREFIX;
import static tk.crypfolio.rest.util.AuthenticationTokenService.getUserIdFromJWT;

// "/api" root-path is defined in RestApplication
@Path("/")
@RequestScoped
public class UserRestController extends Application {

    private static final Logger LOGGER = LogManager.getLogger(UserRestController.class);

    // stateless business
    @Context
    private HttpHeaders httpHeaders;

    @Inject
    protected UserService userService;

    @POST
    @Path("/registration")
    @Consumes("application/json")
    @Produces("application/json")
    public Response userRegistration(String jsonString) throws Exception {

        UserEntity user = new UserEntity(new PortfolioEntity());

        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            user.setEmail((String) jsonObject.get("email"));
            user.getPortfolio().setName(((String) jsonObject.get("portfolioName")));

            String password = (String) jsonObject.get("password");

            UserEntity userDB = userService.doRegisterDB(user, password);

            if (userDB != null) {

                LOGGER.info("UserRestController: Successful '/userRegistration' request");

                // generates response without authentication token!!!
                return JsonResponseBuild.generateJsonResponse(null, null);
            }

            throw new RestApplicationException("Invalid user register credentials!");

        } catch (NullPointerException ex) {

            throw new RestApplicationException("NullPointerException - Invalid user register credentials!");
        }
    }

    @POST
    @Path("/authentication")
    @Consumes("application/json")
    @Produces("application/json")
    public Response getAuthenticationToken(String jsonString) throws Exception {

        UserEntity user = new UserEntity(new PortfolioEntity());

        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            user.setEmail((String) jsonObject.get("email"));

            String password = (String) jsonObject.get("password");

            UserEntity userDB = userService.doLoginDB(user, password);

            if (userDB != null && userDB.getIsEmailVerified()) {

                LOGGER.info("UserRestController: Successful '/authentication' request");

                // generates only response with 1st authentication token (using user ID for Payload)
                return JsonResponseBuild.generateJsonResponse(null, userDB.getId());
            }

            throw new RestApplicationException("Invalid user login credentials!");

        } catch (NullPointerException ex) {

            throw new RestApplicationException("Invalid user login credentials!");
        }
    }

    @Authenticator
    @GET
    @Path("/user")
    @Produces("application/json")
    public Response getUser() throws Exception {

        try {

            String userId = getUserIdFromJWT(httpHeaders.getHeaderString(AUTHORIZATION).substring(TOKEN_BEARER_PREFIX.length()).trim());

            UserEntity userDB = userService.getUserDBById(Long.valueOf(userId));

            if (userDB != null && userDB.getIsEmailVerified()) {

                LOGGER.info("UserRestController: Successful '/user' request");

                // generates response with new authentication token (using user ID for Payload)
                return JsonResponseBuild.generateJsonResponse(userDB, userDB.getId());
            }

            throw new BadRequestException("There is no user with requested ID or user email wasn't verified yet!");

        } catch (Exception ex) {

            throw new RestApplicationException(ex.getMessage());
        }
    }

    @POST
    @Path("/reset-password/request")
    @Consumes("application/json")
    @Produces("application/json")
    public Response resetUserPasswordRequest(String jsonString) throws Exception {

        UserEntity user = new UserEntity();
        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            user.setEmail((String) jsonObject.get("email"));

            Boolean wasSentResetPasswordEmail = userService.setUserResetPasswordCodeDB(user);

            if (wasSentResetPasswordEmail) {

                LOGGER.info("UserRestController: Successful '/reset-pass-request' request");

                // generates response without authentication token!!!
                return JsonResponseBuild.generateJsonResponse(null, null);
            }

            throw new RestApplicationException("Cannot reset password! User with this email doesn't exist!");

        } catch (NullPointerException ex) {

            throw new RestApplicationException("NullPointerException - Invalid user email!");
        }
    }

    @POST
    @Path("/reset-password")
    @Consumes("application/json")
    @Produces("application/json")
    public Response setNewPassword(String jsonString) throws Exception {

        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            String code = (String) jsonObject.get("code");

            String password = (String) jsonObject.get("password");

            UserEntity userDB = userService.setUserNewPasswordDB(code, password);

            if (userDB != null) {

                LOGGER.info("UserRestController: Successful '/set-new-pass-request' request");

                // generates only response with 1st authentication token (using user ID for Payload)
                return JsonResponseBuild.generateJsonResponse(null, userDB.getId());
            }

            throw new RestApplicationException("Invalid new password setting request!");

        } catch (NullPointerException ex) {

            throw new RestApplicationException("Invalid code/password to setting new password request!");
        }
    }

    @POST
    @Path("/verify-email/request")
    @Consumes("application/json")
    @Produces("application/json")
    public Response resendVerificationEmailRequest(String jsonString) throws Exception {

        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            String email = (String) jsonObject.get("email");

            UserEntity userDB = userService.getUserDBByEmail(email);

            if (userDB != null && !userDB.getIsEmailVerified()) {

                userService.sendConfEmailDB(userDB);
                LOGGER.info("UserRestController: Successful '/reset-pass-request' request");

                // generates response without authentication token!!!
                return JsonResponseBuild.generateJsonResponse(null, null);
            }

            throw new RestApplicationException("Cannot resend verification email!" +
                    " User with this email doesn't exist or his email is already verified!");

        } catch (
                NullPointerException ex) {

            throw new RestApplicationException("NullPointerException - Invalid user email!");
        }
    }

    @POST
    @Path("/verify-email")
    @Consumes("application/json")
    @Produces("application/json")
    public Response serEmailVerification(String jsonString) throws Exception {

        //JSONParser reads the data from string object and break each data into their key value pairs
        JSONParser parserJSON = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parserJSON.parse(jsonString);

            String code = (String) jsonObject.get("code");

            if (code != null) {

                UserEntity userDB = userService.doConfirmEmailDB(code);

                if (userDB != null) {

                    LOGGER.info("UserRestController: Successful '/email-verification' request");

                    // generates only response with 1st authentication token (using user ID for Payload)
                    return JsonResponseBuild.generateJsonResponse(null, userDB.getId());
                }
            }

            throw new RestApplicationException("Invalid email verification request!");

        } catch (NullPointerException ex) {

            throw new RestApplicationException("Invalid code to verify an email!");
        }
    }
}