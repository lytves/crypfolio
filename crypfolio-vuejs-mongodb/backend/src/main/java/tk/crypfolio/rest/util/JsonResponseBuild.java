package tk.crypfolio.rest.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tk.crypfolio.rest.exception.RestApplicationException;

import javax.json.Json;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public abstract class JsonResponseBuild {

    public static Response generateJsonResponse(Object obj, Long userId) throws RestApplicationException {

        return generateJsonResponse(obj, userId, null, null);

    }

    public static Response generateJsonResponse(Object obj, Long userId, Integer error, String errorMessage) throws RestApplicationException {

        ObjectMapper mapper = new ObjectMapper();

        try {

            String jsonInString = "success";

            if (obj != null) {
                // Convert object to JSON string
                jsonInString = mapper.writeValueAsString(obj);
            }

            if (error == null) {
                error = 0;
            }

            if (errorMessage == null) {
                errorMessage = "";
            }

            String jsonStatusContent = Json.createObjectBuilder()
                    .add("timestamp", System.currentTimeMillis())
                    .add("error_code", error)
                    .add("error_message", errorMessage)
                    .build()
                    .toString();

            String jsonResponseObject = Json.createObjectBuilder()
                    .add("status", jsonStatusContent)
                    .add("data", jsonInString)
                    .build()
                    .toString();

            String token = "";

            if (userId != null) {
                // here we should always generate new JWT-token for user send to. It's will make longer user authorization
                token = AuthenticationTokenService.buildAuthentificationJWT(String.valueOf(userId));
            }

            return Response.status(Response.Status.OK).header(AUTHORIZATION, token)
                    .type("application/json").entity(jsonResponseObject).build();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RestApplicationException("Error parsing object!");
        }
    }
}