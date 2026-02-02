package org.nbreval.spring.cloud.gateway.scripting.core.filter;

import java.util.Map;
import org.nbreval.spring.cloud.gateway.scripting.core.config.ScriptingFilterConfig;
import org.nbreval.spring.cloud.gateway.scripting.core.exception.HttpResponseWrapperException;
import org.nbreval.spring.cloud.gateway.scripting.core.script.ScriptManager;
import org.nbreval.spring.cloud.gateway.scripting.core.util.function.ThrowableBiConsumer;
import org.nbreval.spring.cloud.gateway.scripting.core.util.http.RequestWrapper;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Abstract class used to implement cutsom script-based gateway filters. This filter obtains the
 * request and process it using a {@link ScriptManager}, then, mutates the exchange with the
 * modified request obtained as script result.
 */
public abstract class AbstractScriptingFilterFactory
    extends AbstractGatewayFilterFactory<ScriptingFilterConfig> {

  public AbstractScriptingFilterFactory() {
    super(ScriptingFilterConfig.class);
  }

  @Override
  public GatewayFilter apply(ScriptingFilterConfig config) {
    return (exchange, chain) -> {
      try {
        var wrappedRequest = new RequestWrapper(exchange.getRequest());

        var scriptManager = getScriptManager(config);

        var result =
            scriptManager.run(
                Map.of(
                    "request",
                    wrappedRequest,
                    "response",
                    (ThrowableBiConsumer<Integer, String, HttpResponseWrapperException>)
                        (code, message) -> {
                          throw new HttpResponseWrapperException(
                              HttpStatus.valueOf(code), message, null);
                        }));

        if (result instanceof RequestWrapper modifiedWrappedRequest) {
          return chain.filter(modifiedWrappedRequest.murateExchange(exchange));
        } else {
          throw new HttpResponseWrapperException(
              HttpStatus.INTERNAL_SERVER_ERROR,
              "The return object of the script is not valid",
              null);
        }
      } catch (HttpResponseWrapperException e) {
        return e.getAsMonoError();
      } catch (Exception e) {
        return Mono.error(
            new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request", e));
      }
    };
  }

  /**
   * Obtains the script manager used to modify the request, based on the config.
   *
   * @param config Configuration object used to generate the {@link ScriptManager}.
   * @return The {@link ScriptManager} generated based on filter configuration.
   * @throws HttpResponseWrapperException If any error occurs during script manager generation.
   */
  protected abstract ScriptManager getScriptManager(ScriptingFilterConfig config)
      throws HttpResponseWrapperException;
}
