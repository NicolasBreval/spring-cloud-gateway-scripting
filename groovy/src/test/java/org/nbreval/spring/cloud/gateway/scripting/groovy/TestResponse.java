package org.nbreval.spring.cloud.gateway.scripting.groovy;

import org.junit.jupiter.api.Test;
import org.nbreval.spring.cloud.gateway.scripting.core.config.ScriptingFilterConfig;
import org.nbreval.spring.cloud.gateway.scripting.groovy.filter.GroovyScriptingFilter;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class TestResponse {

  @Test
  void test401Response() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        if (request.getHeader("Authorization") == null) {
            response.consume(401, "Unauthorized")
        }
        request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test").build();
    var exchange = MockServerWebExchange.from(request);

    var result = filter.filter(exchange, ex -> Mono.empty());

    StepVerifier.create(result)
        .expectErrorMatches(
            throwable ->
                throwable instanceof ResponseStatusException e
                    && e.getStatusCode() == HttpStatus.UNAUTHORIZED
                    && "Unauthorized".equals(e.getReason()));
  }
}
