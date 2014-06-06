package brainslug.flow.execution;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ExecutionProperties {
  Map<Object, Object> properties;

  public ExecutionProperties() {
    properties = new HashMap<Object, Object>();
  }

  public ExecutionProperties put(Object key, Object value) {
    properties.put(key, value);
    return this;
  }

  public ExecutionProperties putAll(ExecutionProperties executionProperties) {
    this.properties.putAll(executionProperties.properties);
    return this;
  }

  public Object get(Object key) {
    return properties.get(key);
  }

  public Collection<Object> values() {
    return properties.values();
  }

  public static ExecutionProperties with(Object key, Object value) {
    return new ExecutionProperties().put(key, value);
  }
}
