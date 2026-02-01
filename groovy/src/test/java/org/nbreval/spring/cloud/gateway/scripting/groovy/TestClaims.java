package org.nbreval.spring.cloud.gateway.scripting.groovy;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.nbreval.spring.cloud.gateway.scripting.core.config.ScriptingFilterConfig;
import org.nbreval.spring.cloud.gateway.scripting.groovy.filter.GroovyScriptingFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

public class TestClaims {

  @Test
  void testDirectClaim() throws JOSEException {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        if (request.getClaim("sub") == "test") {
            request.setHeader("X-Result", "OK")
        } else {
            request.setHeader("X-Result", "FAIL")
        }
        request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request =
        MockServerHttpRequest.get("/api/test")
            .header("Authorization", "Bearer %s".formatted(getTestToken()))
            .build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getHeaders().get("X-Result")).isEqualTo(List.of("OK"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testMultiLevelClaim() throws JOSEException {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        if (request.getClaim("user_context.id") == "user-123") {
            request.setHeader("X-Result", "OK")
        } else {
            request.setHeader("X-Result", "FAIL")
        }
        request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request =
        MockServerHttpRequest.get("/api/test")
            .header("Authorization", "Bearer %s".formatted(getTestToken()))
            .build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getHeaders().get("X-Result")).isEqualTo(List.of("OK"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testMultiMultiLevelClaim() throws JOSEException {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        if (request.getClaim("user_context.profile.theme") == "dark" && request.getClaim("user_context.profile.language") == "es") {
            request.setHeader("X-Result", "OK")
        } else {
            request.setHeader("X-Result", "FAIL")
        }
        request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request =
        MockServerHttpRequest.get("/api/test")
            .header("Authorization", "Bearer %s".formatted(getTestToken()))
            .build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getHeaders().get("X-Result")).isEqualTo(List.of("OK"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testMultiLevelArrayClaim() throws JOSEException {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        if (request.getClaim("user_context.groups.1") == "editor") {
            request.setHeader("X-Result", "OK")
        } else {
            request.setHeader("X-Result", "FAIL")
        }
        request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request =
        MockServerHttpRequest.get("/api/test")
            .header("Authorization", "Bearer %s".formatted(getTestToken()))
            .build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getHeaders().get("X-Result")).isEqualTo(List.of("OK"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  /**
   * Util method to generate a JWT with some claims to make easy to check all claims-based tests
   *
   * @return The JWT generated, as a sting.
   * @throws JOSEException If occurs any exception during token generation.
   */
  private String getTestToken() throws JOSEException {
    var userData =
        Map.of(
            "id", "user-123",
            "profile",
                Map.of(
                    "theme", "dark",
                    "language", "es"),
            "groups", List.of("admin", "editor"));

    var claimsSet =
        new JWTClaimsSet.Builder()
            .subject("test")
            .claim("user_context", userData)
            .claim("metadata", Map.of("version", "1.0"))
            .build();

    var signer = new MACSigner("8c423e0120437e570427de7a1235d5a57f0091c558ea8f6fd4ae595a351fda12");
    var signedJwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    signedJwt.sign(signer);

    return signedJwt.serialize();
  }
}
