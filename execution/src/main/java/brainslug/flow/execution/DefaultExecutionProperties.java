package brainslug.flow.execution;

import brainslug.flow.context.ExecutionProperties;
import brainslug.flow.context.ExecutionProperty;
import brainslug.util.Option;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultExecutionProperties implements ExecutionProperties {
  Map<String, ExecutionProperty> properties;

  public DefaultExecutionProperties() {
    properties = new HashMap<String, ExecutionProperty>();
  }

  public ExecutionProperties fromList(List<DefaultExecutionProperty> properties) {
    for (DefaultExecutionProperty property : properties) {
      this.properties.put(property.getKey(), property);
    }
    return this;
  }

  @Override
  public ExecutionProperties put(String key, Object value) {
    properties.put(key, new DefaultExecutionProperty()
      .withKey(key)
      .withObjectValue(value));

    return this;
  }

  @Override
  public ExecutionProperties putAll(ExecutionProperties executionProperties) {
    for (ExecutionProperty executionProperty : executionProperties.getValues()) {
      this.properties.put(executionProperty.getKey(), executionProperty);
    }
    return this;
  }

  @Override
  public <T> T get(String key, Class<T> clazz) {
    return Option.of(properties.get(key)).get().as(clazz);
  }

  @Override
  public ExecutionProperty get(String key) {
    return Option.of(properties.get(key)).get();
  }

  @Override
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

  @Override
  public Collection<ExecutionProperty> getValues() {
    return properties.values();
  }

  public static ExecutionProperties with(String key, Object value) {
    return new DefaultExecutionProperties().put(key, value);
  }

  @Override
  public String toString() {
    return "ExecutionProperties{" +
      "properties=" + properties +
      '}';
  }
}
