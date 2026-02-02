package org.nbreval.spring.cloud.gateway.scripting.groovy.config;

import org.nbreval.spring.cloud.gateway.scripting.groovy.filter.GroovyScriptingFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to enable spring autoconfiguration when thsi library is imported in a Spring
 * Cloud Gateway project.
 */
@Configuration
public class GroovyScriptingAutoConfiguration {

  /**
   * Instance of Groovy factory to enable using in a Spring Cloud Gateway project.
   *
   * @return
   */
  @Bean
  public GroovyScriptingFilterFactory groovyScriptingFilterFactory() {
    return new GroovyScriptingFilterFactory();
  }
}
