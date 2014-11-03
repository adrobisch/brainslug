package brainslug.flow.execution;

import brainslug.util.Option;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionProperties {
  Map<String, ExecutionProperty> properties;

  public ExecutionProperties() {
    properties = new HashMap<String, ExecutionProperty>();
  }

  public ExecutionProperties fromList(List<ExecutionProperty> properties) {
    for (ExecutionProperty property : properties) {
      this.properties.put(property.getKey(), property);
    }
    return this;
  }

  public ExecutionProperties put(String key, Object value) {
    properties.put(key, new ExecutionProperty()
      .withKey(key)
      .withObjectValue(value));

    return this;
  }

  public ExecutionProperties putAll(ExecutionProperties executionProperties) {
    this.properties.putAll(executionProperties.properties);
    return this;
  }

  public <T> T get(String key, Class<T> clazz) {
    return Option.of(properties.get(key)).get().as(clazz);
  }

  public ExecutionProperty get(String key) {
    return Option.of(properties.get(key)).get();
  }

  public <P> P getProperty(Class<P> type) {
    P result = null;
    int typeCount = 0;
    for (ExecutionProperty object : getValues()) {

      if(object.getObjectValue() != null && object.getObjectValue().getClass().isAssignableFrom(type)) {
        result = (P) object.getObjectValue();
        typeCount++;
      }
    }
    if (typeCount == 0) {
      throw new IllegalArgumentException(String.format("no property of type %s exists", type));
    }else if(typeCount > 1) {
      throw new IllegalArgumentException(String.format("multiple properties of type %s exist", type));
    }
    return result;
  }

  public Collection<ExecutionProperty> getValues() {
    return properties.values();
  }

  public static ExecutionProperties with(String key, Object value) {
    return new ExecutionProperties().put(key, value);
  }

  @Override
  public String toString() {
    return "ExecutionProperties{" +
      "properties=" + properties +
      '}';
  }
}
