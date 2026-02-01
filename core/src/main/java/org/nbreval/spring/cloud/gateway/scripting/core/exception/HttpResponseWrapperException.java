package org.nbreval.spring.cloud.gateway.scripting.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Exception used to wraps another exception and assign it an HTTP error code and a message. It's
 * usefull to force return a different HTTP error code for each exception.
 */
public class HttpResponseWrapperException extends Exception {
  @NonNull private final HttpStatus status;
  private final String message;
  private final Throwable origin;

  public HttpResponseWrapperException(
      @NonNull HttpStatus status, String message, Throwable origin) {
    this.status = status;
    this.message = message;
    this.origin = origin;
  }

  /**
   * Returns the current exception as a Mono error to be returned as an HTTP error.
   *
   * @return The Mono error with the content of exception.
   */
  public Mono<Void> getAsMonoError() {
    return Mono.error(new ResponseStatusException(status, message, origin));
  }
}
