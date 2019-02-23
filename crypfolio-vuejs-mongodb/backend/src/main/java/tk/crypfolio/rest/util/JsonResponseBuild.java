package tk.crypfolio.rest.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tk.crypfolio.rest.exception.RestApplicationException;

import javax.json.Json;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public abstract class JsonResponseBuild {

    public static Response generateJsonResponse(Object obj, Long userId) throws RestApplicationException {

        ObjectMapper mapper = new ObjectMapper();

        try {

            String jsonInString = "success";

            if (obj != null) {
                // Convert object to JSON string
                jsonInString = mapper.writeValueAsString(obj);
            }

            String jsonStatusContent = Json.createObjectBuilder()
                    .add("timestamp", System.currentTimeMillis())
                    .add("error_code", 0)
                    .add("error_message", "")
                    .build()
                    .toString();

            String jsonResponseObject = Json.createObjectBuilder()
                    .add("status", jsonStatusContent)
                    .add("data", jsonInString)
                    .build()
                    .toString();

            String token = "unathourized";
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