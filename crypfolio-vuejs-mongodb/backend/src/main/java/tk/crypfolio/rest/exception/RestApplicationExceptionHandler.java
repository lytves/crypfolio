package tk.crypfolio.rest.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.Json;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestApplicationExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = LogManager.getLogger(RestApplicationExceptionHandler.class);

    @Override
    public Response toResponse(Exception ex) {

        String errorMessage = ex.getMessage() == null ? "" : ex.getMessage();

        String jsonStatusContent = Json.createObjectBuilder()
                .add("timestamp", System.currentTimeMillis())
                .add("error_code", Response.Status.BAD_REQUEST.getStatusCode())
                .add("error_message", errorMessage)
                .build()
                .toString();

        String jsonResponseObject = Json.createObjectBuilder()
                .add("status", jsonStatusContent)
                .build()
                .toString();

        LOGGER.error("Handling by RestApplicationExceptionHandler: " + ex.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).type("application/json").entity(jsonResponseObject).build();
    }
}