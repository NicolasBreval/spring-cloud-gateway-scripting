package org.nbreval.spring.cloud.gateway.scripting.groovy.script;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.util.HashMap;
import java.util.Map;
import org.nbreval.spring.cloud.gateway.scripting.core.script.ScriptManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyScriptManager extends ScriptManager {

  private static final Logger logger = LoggerFactory.getLogger(GroovyScriptManager.class);

  private final Class<?> scriptClass;

  public GroovyScriptManager(String script) {
    super(script);
    this.scriptClass = new GroovyShell().getClassLoader().parseClass(this.script);
  }

  @Override
  public Object run(Map<String, Object> arguments) throws Exception {
    var instance = (Script) scriptClass.getDeclaredConstructor().newInstance();
    var bindings = new Binding(new HashMap<>(arguments));
    bindings.setVariable("logger", logger);
    instance.setBinding(bindings);
    return instance.run();
  }
}
