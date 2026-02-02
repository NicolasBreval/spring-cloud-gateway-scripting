package org.nbreval.spring.cloud.gateway.scripting.groovy.filter;

import java.io.IOException;
import org.nbreval.spring.cloud.gateway.scripting.core.config.ScriptingFilterConfig;
import org.nbreval.spring.cloud.gateway.scripting.core.exception.HttpResponseWrapperException;
import org.nbreval.spring.cloud.gateway.scripting.core.filter.AbstractScriptingFilterFactory;
import org.nbreval.spring.cloud.gateway.scripting.core.script.ScriptManager;
import org.nbreval.spring.cloud.gateway.scripting.groovy.script.GroovyScriptManager;
import org.springframework.http.HttpStatus;

/**
 * Implementation of {@link AbstractScriptingFilterFactory} with a {@link ScriptManager} which
 * process requests using Groovy language.
 */
public class GroovyScriptingFilterFactory
    extends AbstractScriptingFilterFactory<ScriptingFilterConfig> {

  /**
   * Instance of {@link GroovyScriptManager}, stored to don't create it each time that the manager
   * is invoked.
   */
  private GroovyScriptManager scriptManager;

  @Override
  protected ScriptManager getScriptManager(ScriptingFilterConfig config)
      throws HttpResponseWrapperException {

    if (scriptManager == null) {
      try {
        scriptManager = new GroovyScriptManager(config.getScript());
      } catch (IOException e) {
        throw new HttpResponseWrapperException(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error obtaining script from configuration", e);
      }
    }

    return scriptManager;
  }

  @Override
  public String name() {
    return "GroovyScripting";
  }
}
