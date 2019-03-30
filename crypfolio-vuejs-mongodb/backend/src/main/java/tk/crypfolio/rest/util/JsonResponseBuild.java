package tk.crypfolio.rest.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import tk.crypfolio.rest.exception.RestApplicationException;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public abstract class JsonResponseBuild {

    public static Response generateJsonResponse(Object obj, Long userId) throws RestApplicationException {

        return generateJsonResponse(obj, userId, null, null);

    }

    public static Response generateJsonResponse(Object obj, Long userId, Integer error, String errorMessage) throws RestApplicationException {

        // Create new JSON Object
        JsonObject jsonResponseStatus = new JsonObject();
        JsonObject jsonResponse = new JsonObject();

        if (error == null) {
            error = 0;
        }

        if (errorMessage == null) {
            errorMessage = "";
        }

        jsonResponseStatus.addProperty("timestamp", System.currentTimeMillis());
        jsonResponseStatus.addProperty("error_code", error);
        jsonResponseStatus.addProperty("error_message", errorMessage);

        jsonResponse.add("status", jsonResponseStatus);

        try {
            String jsonInString = "success";

            if (obj != null) {
                Gson gsonBuilder = new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .disableHtmlEscaping()
                        .create();
                jsonResponse.add("data", gsonBuilder.toJsonTree(obj));
            } else {
                jsonResponse.addProperty("data", jsonInString);
            }

            String token = "";

            if (userId != null) {
                // here we should always generate new JWT-token for user send to. It's will make longer user authorization
                token = AuthenticationTokenService.buildAuthentificationJWT(String.valueOf(userId));
            }

            return Response.status(Response.Status.OK).header(AUTHORIZATION, token)
                    .type("application/json").entity(jsonResponse.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RestApplicationException("Error parsing object!");
        }
    }
}