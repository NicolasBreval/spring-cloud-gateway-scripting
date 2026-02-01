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

public class TestQueryParamsOperations {

  @Test
  void testAddQueryParam() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.setQueryParam("a", 1)
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getQueryParams().get("a")).isEqualTo(List.of("1"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testAddMultiValuedQueryParams() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.setQueryParam("a", 1, 2, 3)
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getQueryParams().get("a")).isEqualTo(List.of("1", "2", "3"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testSetQueryParam() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.setQueryParam("a", 1)
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test?a=0").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getQueryParams().get("a")).isEqualTo(List.of("1"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testSetMultiValuedQueryParam() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.setQueryParam("a", 1, 2, 3)
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test?a=0").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getQueryParams().get("a")).isEqualTo(List.of("1", "2", "3"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testRemoveQueryParam() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.removeQueryParam("a")
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test?a=0").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getQueryParams().get("a")).isNull();
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testRemoveMultiValuedQueryParam() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.removeQueryParam("a")
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test?a=0,1,2,3,4").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getQueryParams().get("a")).isNull();
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testAddQueryParamsKeepsOriginalQueryParams() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.setQueryParam("a", 1)
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test?b=2&c=3").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getQueryParams().get("a")).isEqualTo(List.of("1"));
          assertThat(ex.getRequest().getQueryParams().get("b")).isEqualTo(List.of("2"));
          assertThat(ex.getRequest().getQueryParams().get("c")).isEqualTo(List.of("3"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testSetQueryParamKeepsOriginalQueryParams() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.setQueryParam("a", 1)
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test?a=0&b=2&c=3").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getQueryParams().get("a")).isEqualTo(List.of("1"));
          assertThat(ex.getRequest().getQueryParams().get("b")).isEqualTo(List.of("2"));
          assertThat(ex.getRequest().getQueryParams().get("c")).isEqualTo(List.of("3"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }

  @Test
  void testRemoveQueryParamKeepsOriginalQueryParams() {
    var filterFactory = new GroovyScriptingFilter();
    var filterConfig =
        new ScriptingFilterConfig(
            """
        request.removeQueryParam("a")
        return request
        """);

    var filter = filterFactory.apply(filterConfig);

    var request = MockServerHttpRequest.get("/api/test?a=0&b=2&c=3").build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain =
        (ex) -> {
          assertThat(ex.getRequest().getQueryParams().get("a")).isNull();
          assertThat(ex.getRequest().getQueryParams().get("b")).isEqualTo(List.of("2"));
          assertThat(ex.getRequest().getQueryParams().get("c")).isEqualTo(List.of("3"));
          return Mono.empty();
        };

    filter.filter(exchange, chain).block();
  }
}
