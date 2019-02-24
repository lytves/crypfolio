package tk.crypfolio.rest.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.rest.util.AuthenticationTokenService;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static tk.crypfolio.rest.SettingsRest.TOKEN_BEARER_PREFIX;

@Authenticator
@Provider
@Priority(Priorities.AUTHENTICATION)
public class ApiAuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger(ApiAuthenticationFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        LOGGER.info("Enter to ApiAuthenticationFilter with request URI: " + requestContext.getUriInfo().getAbsolutePath());

        // Get the Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);

            // it's important to return statement here!! after requestContext.abortWith(...).build()
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader.substring(TOKEN_BEARER_PREFIX.length()).trim();

        LOGGER.warn("ApiAuthenticationFilter: found AUTHORIZATION: " + token);

        try {
            // Validate the token
            validateToken(token);
            LOGGER.info("ApiAuthenticationFilter passed successfully!");

        } catch (Exception ex) {
            abortWithUnauthorized(requestContext);
            LOGGER.error("Handling by ApiAuthenticationFilter: " + ex.getMessage());
            return;
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(TOKEN_BEARER_PREFIX.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .header(AUTHORIZATION, "Unauthorized")
                .build());
        LOGGER.warn("ApiAuthenticationFilter abort With Unauthorized error!");
    }

    private void validateToken(String token) throws Exception {
        // Check if the token was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
        AuthenticationTokenService.isTokenValid(token);
    }
}