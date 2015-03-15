package brainslug.flow.execution;

import brainslug.flow.Identifier;
import brainslug.flow.context.FlowProperties;
import brainslug.flow.context.ExecutionProperty;
import brainslug.util.Option;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionProperties implements FlowProperties<ExecutionProperty> {
  Map<String, ExecutionProperty> properties;

  public ExecutionProperties() {
    this.properties = new HashMap<String, ExecutionProperty>();
  }

  public FlowProperties<ExecutionProperty> fromList(List<BrainslugProperty> properties) {
    for (BrainslugProperty property : properties) {
      this.properties.put(property.getKey(), property);
    }
    return this;
  }

  @Override
  public FlowProperties<ExecutionProperty> with(Identifier key, Object value) {
    properties.put(key.stringValue(), new BrainslugProperty()
      .withKey(key.stringValue())
      .withObjectValue(value));

    return this;
  }

  @Override
  public FlowProperties<ExecutionProperty> with(String key, Object value) {
    properties.put(key, new BrainslugProperty()
      .withKey(key)
      .withObjectValue(value));

    return this;
  }

  @Override
  public FlowProperties<ExecutionProperty> withAll(FlowProperties<ExecutionProperty> executionProperties) {
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

  public static ExecutionProperties newProperties() {
    return new ExecutionProperties();
  }

  @Override
  public String toString() {
    return "ExecutionProperties{" +
      "properties=" + properties +
      '}';
  }
}
