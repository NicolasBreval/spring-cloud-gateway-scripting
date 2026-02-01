package org.nbreval.spring.cloud.gateway.scripting.groovy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.nbreval.spring.cloud.gateway.scripting.core.config.ScriptingFilterConfig;
import org.nbreval.spring.cloud.gateway.scripting.groovy.filter.GroovyScriptingFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

public class TestHeaderOperations {

  @Test
  void testAddHeader() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.setHeader("X-TestHeader", "A")
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getHeaders().get("X-TestHeader")).isEqualTo(List.of("A"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testAddMultiValuedHeader() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.setHeader("X-TestHeader", "A", "B", "C")
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getHeaders().get("X-TestHeader"))
              .isEqualTo(List.of("A", "B", "C"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testSetHeader() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.setHeader("X-TestHeader", "B")
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test").header("X-TestHeader", "A").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getHeaders().get("X-TestHeader")).isEqualTo(List.of("B"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testSetMultiValuedHeader() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.setHeader("X-TestHeader", "D", "E", "F")
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request =
        MockServerHttpRequest.get("/api/test").header("X-TestHeader", "A", "B", "C").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getHeaders().get("X-TestHeader"))
              .isEqualTo(List.of("D", "E", "F"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testRemoveHeader() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.removeHeader("X-TestHeader")
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test").header("X-TestHeader", "A").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getHeaders().get("X-TestHeader")).isNull();
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testAddHeaderKeepsOriginalHeaders() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.setHeader("X-TestHeader", "B")
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request =
        MockServerHttpRequest.get("/api/test")
            .header("X-OldHeader-A", "A")
            .header("X-OldHeader-B", "A", "B", "C")
            .build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getHeaders().get("X-TestHeader")).isEqualTo(List.of("B"));
          assertThat(ex.getRequest().getHeaders().get("X-OldHeader-A")).isEqualTo(List.of("A"));
          assertThat(ex.getRequest().getHeaders().get("X-OldHeader-B"))
              .isEqualTo(List.of("A", "B", "C"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testSetHeaderKeepsOriginalHeaders() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.setHeader("X-TestHeader", "B")
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request =
        MockServerHttpRequest.get("/api/test")
            .header("X-TestHeader", "A")
            .header("X-OldHeader-A", "A")
            .header("X-OldHeader-B", "A", "B", "C")
            .build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getHeaders().get("X-TestHeader")).isEqualTo(List.of("B"));
          assertThat(ex.getRequest().getHeaders().get("X-OldHeader-A")).isEqualTo(List.of("A"));
          assertThat(ex.getRequest().getHeaders().get("X-OldHeader-B"))
              .isEqualTo(List.of("A", "B", "C"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testRemoveHeaderKeepsOriginalHeaders() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.removeHeader("X-TestHeader")
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request =
        MockServerHttpRequest.get("/api/test")
            .header("X-TestHeader", "A")
            .header("X-OldHeader-A", "A")
            .header("X-OldHeader-B", "A", "B", "C")
            .build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getHeaders().get("X-TestHeader")).isNull();
          assertThat(ex.getRequest().getHeaders().get("X-OldHeader-A")).isEqualTo(List.of("A"));
          assertThat(ex.getRequest().getHeaders().get("X-OldHeader-B"))
              .isEqualTo(List.of("A", "B", "C"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }
}
