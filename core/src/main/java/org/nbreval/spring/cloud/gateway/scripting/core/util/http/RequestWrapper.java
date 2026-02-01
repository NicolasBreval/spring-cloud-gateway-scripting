package org.nbreval.spring.cloud.gateway.scripting.core.util.http;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.nbreval.spring.cloud.gateway.scripting.core.util.auth.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Wrapper object used to make easy interact with requests from scripts, and limit the access to it.
 */
public class RequestWrapper {

  /** Logger object used to show some information in application's log. */
  private static final Logger log = LoggerFactory.getLogger(RequestWrapper.class);

  private static final Pattern numberPattern = Pattern.compile("\\d+");

  /** The actual request entity. */
  private @NonNull ServerHttpRequest request;

  /** Claims obtained from authorization header. */
  private Map<String, Object> claims;

  public RequestWrapper(@NonNull ServerHttpRequest request) {
    this.request = request;
    this.claims = null;
  }

  /**
   * Obtains all headers of the request, as an {@link HttpHeaders} object.
   *
   * @return All headers from request.
   */
  public HttpHeaders getHeaders() {
    return request.getHeaders();
  }

  /**
   * Obtains the values of an specific header.
   *
   * @param header Key of header to obtain.
   * @return The value of the required header, or null if not exists.
   */
  public List<String> getHeader(String header) {
    return getHeaders().get(header);
  }

  /**
   * Obtains the first value of an specific header. It's usefull when it's guaranteed there is only
   * a value for the header.
   *
   * @param header Key of header to obtain.
   * @return The value of the required header, or null if not exists.
   */
  public String getFirstHeader(@NonNull String header) {
    return getHeaders().getFirst(header);
  }

  /**
   * Adds, or overwrites, a header on the request.
   *
   * @param key The key of the header to set.
   * @param values The value of the header to set.
   */
  public void setHeader(@NonNull String key, @NonNull String... values) {
    request = request.mutate().header(key, values).build();
  }

  /**
   * Removes a header on request.
   *
   * @param key Key of header to remove.
   */
  public void removeHeader(@NonNull String key) {
    request =
        request
            .mutate()
            .headers(
                requestHeaders -> {
                  requestHeaders.remove(key);
                })
            .build();
  }

  /**
   * Obtains all claims from JWT in authorization header. This method parses the JWT only the first
   * time when is invoked and stores the claims map in the property {@link RequestWrapper#claims},
   * because the claims parsing is a expensive operation, so, the next time is invoked, returns the
   * previously parsed claims.
   *
   * @return
   */
  public Map<String, Object> getClaims() {
    if (claims == null) {
      var authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        try {
          this.claims = TokenManager.getTokenClaims(authHeader.replaceFirst("Bearer\\s", ""));
        } catch (ParseException e) {
          log.debug("Unable to read claims from authorization header", e);
          this.claims = new HashMap<>();
        }
      }

      return this.claims;
    } else {
      return claims;
    }
  }

  /**
   * Obtains single claim from request's JWT.
   *
   * @param path List of claims map keys, separated by dots. It allows to extract the value of a
   *     multi-level claim.
   * @return The value of the required claim, or null if not exists.
   */
  public Object getClaim(String path) {
    var parts = path.split("\\.");
    var claims = getClaims();
    Object value = claims.get(parts[0]);

    for (int i = 1; i < parts.length; i++) {
      var key = parts[i];

      if (value != null) {
        if (numberPattern.matcher(key).matches() && value instanceof List list) {
          value = list.get(Integer.parseInt(key));
        } else if (value instanceof Map subClaims) {
          value = subClaims.get(key);
        } else {
          value = null;
        }
      }
    }

    return value;
  }

  /**
   * Obtains all query params from request's path.
   *
   * @return A multi-value map with all query params from request's path.
   */
  public MultiValueMap<String, String> getQueryParams() {
    return request.getQueryParams();
  }

  /**
   * Obtains all values of an specific query param.
   *
   * @param queryParam Key of query param to extract.
   * @return The values of the required query param, as list.
   */
  public List<String> queryQueryParam(String queryParam) {
    return getQueryParams().get(queryParam);
  }

  /**
   * Obtains the first value of all possible ones of an specific query param.
   *
   * @param queryParam The key of query param to obtain.
   * @return The first value of the required query param.
   */
  public String getFirstQueryParam(@NonNull String queryParam) {
    return getQueryParams().getFirst(queryParam);
  }

  /**
   * Adds, or overwrites, a query param on the request.
   *
   * @param key Key of the query param to set.
   * @param values Values of the query param to set.
   */
  public void setQueryParam(@NonNull String key, @NonNull Object... values) {
    var modified =
        UriComponentsBuilder.fromUri(request.getURI())
            .replaceQueryParam(key, values)
            .build(true)
            .toUri();

    request = request.mutate().uri(modified).build();
  }

  /**
   * Removes a query param from request's path.
   *
   * @param key Key of query param to remove.
   */
  public void removeQueryParam(@NonNull String key) {
    var modified =
        UriComponentsBuilder.fromUri(request.getURI()).replaceQueryParam(key).build(true).toUri();

    request = request.mutate().uri(modified).build();
  }

  /**
   * Sets the request of a {@link ServerWebExchange} with the current wrapped.
   *
   * @param exchange The {@link ServerWebExchange} to mutate.
   * @return The same {@link ServerWebExchange} with its request modified with current one.
   */
  public ServerWebExchange murateExchange(@NonNull ServerWebExchange exchange) {
    return exchange.mutate().request(request).build();
  }
}
