package tk.crypfolio.rest.exception;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestApplicationExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = LogManager.getLogger(RestApplicationExceptionHandler.class);

    @Override
    public Response toResponse(Exception ex) {

        String errorMessage = ex.getMessage() == null ? "" : ex.getMessage();

        // Create new JSON Object
        JsonObject jsonResponseStatus = new JsonObject();
        JsonObject jsonResponse = new JsonObject();

        jsonResponseStatus.addProperty("timestamp", System.currentTimeMillis());
        jsonResponseStatus.addProperty("error_code", Response.Status.BAD_REQUEST.getStatusCode());
        jsonResponseStatus.addProperty("error_message", errorMessage);


        jsonResponse.add("status", jsonResponseStatus);

        LOGGER.error("Handling by RestApplicationExceptionHandler: " + ex.getMessage());
        ex.printStackTrace();
        return Response.status(Response.Status.BAD_REQUEST)
                .type("application/json").entity(jsonResponse.toString()).build();
    }
}