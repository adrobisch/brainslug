package brainslug.flow.execution.property;

import brainslug.flow.context.ExecutionProperty;
import brainslug.flow.context.FlowProperties;
import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.Property;
import brainslug.util.Option;

import java.util.*;

public class ExecutionProperties implements FlowProperties<ExecutionProperties, ExecutionProperty<?>> {
  Map<String, ExecutionProperty<?>> properties;

  public ExecutionProperties() {
    this.properties = new HashMap<String, ExecutionProperty<?>>();
  }

  public <T extends ExecutionProperty> ExecutionProperties from(Collection<T> properties) {
    for (ExecutionProperty property : properties) {
      this.properties.put(property.getKey(), property);
    }
    return this;
  }

  @Override
  public ExecutionProperties with(Identifier key, Object value) {
    return with(key.stringValue(), value);
  }

  @Override
  public ExecutionProperties with(Property<?> key, Object value) {
    return with(key.getValue(), value);
  }

  @Override
  public ExecutionProperties with(String key, Object value) {
    properties.put(key, createPropertyFromValue(key, value));
    return this;
  }

  ExecutionProperty<?> createPropertyFromValue(String key, Object value) {
    if (value instanceof Long) {
      return new LongProperty(key, (Long) value);
    } else if (value instanceof Integer) {
      return new IntProperty(key, (Integer) value);
    } else if (value instanceof Double) {
      return new DoubleProperty(key, (Double) value);
    } else if (value instanceof Float) {
      return new FloatProperty(key, (Float) value);
    } else if (value instanceof Boolean) {
      return new BooleanProperty(key, (Boolean) value);
    } else if (value instanceof String) {
      return new StringProperty(key, (String) value);
    } else if (value instanceof Date) {
      return new DateProperty(key, (Date) value);
    } else {
      return new ObjectProperty(key, value);
    }
  }

  @Override
  public ExecutionProperties withAll(ExecutionProperties executionProperties) {
    for (ExecutionProperty executionProperty : executionProperties.getValues()) {
      this.properties.put(executionProperty.getKey(), executionProperty);
    }
    return this;
  }

  @Override
  public <T> T getValue(String key, Class<T> clazz) {
    return (T) Option.of(properties.get(key)).get().getValue();
  }

  @Override
  public <T> ExecutionProperty<T> getProperty(String key, Class<T> clazz) {
    return (ExecutionProperty<T>) properties.get(key);
  }

  @Override
  public ExecutionProperty<?> get(String key) {
    return properties.get(key);
  }

  @Override
  public Collection<ExecutionProperty<?>> getValues() {
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
