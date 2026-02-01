package org.nbreval.spring.cloud.gateway.scripting.core.script;

import java.util.Map;

/** Abstract class used to implement different objects to run scripts with a initial context. */
public abstract class ScriptManager {

  /** Script to run later */
  protected final String script;

  public ScriptManager(String script) {
    this.script = script;
  }

  /**
   * Runs the script content using a map of variables as script arguments.
   *
   * @param arguments Arguments map, the keys are the names of argument that will be called in
   *     script, and values are the argument values mapped inside script.
   * @return The object returned by the script.
   * @throws Exception If the script code produces an exception.
   */
  public abstract Object run(Map<String, Object> arguments) throws Exception;
}
