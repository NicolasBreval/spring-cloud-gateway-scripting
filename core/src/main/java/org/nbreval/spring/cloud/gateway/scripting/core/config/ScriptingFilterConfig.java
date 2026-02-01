package org.nbreval.spring.cloud.gateway.scripting.core.config;

import java.io.IOException;
import org.nbreval.spring.cloud.gateway.scripting.core.util.file.FileManager;
import org.nbreval.spring.cloud.gateway.scripting.core.util.validation.PathValidator;

/**
 * Configuration required for {@link
 * org.nbreval.spring.cloud.gateway.scripting.core.filter.AbstractScriptingFilter}
 */
public class ScriptingFilterConfig {

  /** Contains the path, or content, of the script to be used to filter requests. */
  private String scriptOrPath;

  public ScriptingFilterConfig() {}

  public ScriptingFilterConfig(String scriptOrPath) {
    this.scriptOrPath = scriptOrPath;
  }

  public String getScript() throws IOException {
    if (PathValidator.isValidRegularPath(scriptOrPath)) {
      return FileManager.getRegularFileContentAsText(scriptOrPath);
    } else if (PathValidator.isValidClasspathPath(scriptOrPath)) {
      return FileManager.getResourceFileContentAsText(scriptOrPath.replaceFirst("classpath:", ""));
    } else {
      return scriptOrPath;
    }
  }
}
