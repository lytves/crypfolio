package tk.crypfolio.rest.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.time.DateUtils;
import tk.crypfolio.rest.exception.RestApplicationException;

import java.util.Date;

import static tk.crypfolio.rest.SettingsRest.*;

public abstract class AuthenticationTokenService {

    public static String buildAuthentificationJWT(String id) throws RestApplicationException {

        try {
            Algorithm algorithm = Algorithm.HMAC256(SUPER_SECRET_KEY);

            Date date = new Date();
//            Date newDate = DateUtils.addMonths(date, 1);
            Date newDate = DateUtils.addDays(date, TOKEN_DURATION_DAYS);

            return JWT.create()
                    .withIssuer(ISSUER_INFO)
                    .withClaim("sub", id)
                    .withIssuedAt(date)
                    .withExpiresAt(newDate)
                    .sign(algorithm);

        } catch (JWTCreationException ex) {
            throw new RestApplicationException("Invalid Signing configuration. Couldn't convert Claims!");
        }
    }

    public static String getUserIdFromJWT(String token) throws RestApplicationException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SUPER_SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("sub").asString();

        } catch (JWTVerificationException ex) {
            throw new RestApplicationException("JWT token verification failed!");
        }
    }

    public static void isTokenValid(String token) throws RestApplicationException {

        try {
            Algorithm algorithm = Algorithm.HMAC256(SUPER_SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();

            // when verifying a token the time validation occurs automatically!
            DecodedJWT jwt = verifier.verify(token);

        } catch (JWTVerificationException ex) {
            throw new RestApplicationException("JWT token verification failed!");
        }
    }
}