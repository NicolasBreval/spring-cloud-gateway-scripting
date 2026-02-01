package org.nbreval.spring.cloud.gateway.scripting.groovy.script;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.util.Map;
import org.nbreval.spring.cloud.gateway.scripting.core.script.ScriptManager;

public class GroovyScriptManager extends ScriptManager {

  private final Class<?> scriptClass;

  public GroovyScriptManager(String script) {
    super(script);
    this.scriptClass = new GroovyShell().getClassLoader().parseClass(this.script);
  }

  @Override
  public Object run(Map<String, Object> arguments) throws Exception {
    var instance = (Script) scriptClass.getDeclaredConstructor().newInstance();
    instance.setBinding(new Binding(arguments));
    return instance.run();
  }
}
