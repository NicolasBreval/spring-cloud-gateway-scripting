package org.nbreval.spring.cloud.gateway.scripting.core.util.auth;

import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Map;

/** Util class used to operate with JWT */
public class TokenManager {

  /**
   * Parses a base-64 JWT into a Map of claims.
   *
   * @param token String JWT to obtain its claims.
   * @return The token's claims as a Map object.
   * @throws ParseException If is not possible to parse the token.
   */
  public static Map<String, Object> getTokenClaims(String token) throws ParseException {
    var jwt = SignedJWT.parse(token);
    return jwt.getJWTClaimsSet().getClaims();
  }
}
