package brainslug.flow.execution.property;

import brainslug.flow.instance.FlowInstanceProperty;
import brainslug.flow.instance.FlowInstanceProperties;
import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.Property;

import java.util.*;

public class ExecutionProperties implements FlowInstanceProperties<ExecutionProperties, FlowInstanceProperty<?>> {
  Map<String, FlowInstanceProperty<?>> properties;

  public ExecutionProperties() {
    this.properties = new HashMap<String, FlowInstanceProperty<?>>();
  }

  public <T extends FlowInstanceProperty> ExecutionProperties from(Collection<T> properties) {
    for (FlowInstanceProperty property : properties) {
      this.properties.put(property.getKey(), property);
    }
    return this;
  }

  @Override
  public ExecutionProperties with(Identifier key, Object value) {
    return with(key.stringValue(), value);
  }

  @Override
  public ExecutionProperties with(Identifier key, Object value, boolean isTransient) {
    return with(key.stringValue(), value, isTransient);
  }

  @Override
  public ExecutionProperties with(Property<?> key, Object value) {
    return with(key.getValue(), value);
  }

  @Override
  public ExecutionProperties with(Property<?> key, Object value, boolean isTransient) {
    return with(key.getValue(), value, isTransient);
  }

  @Override
  public ExecutionProperties with(String key, Object value) {
    properties.put(key, createPropertyFromValue(key, value, false));
    return this;
  }

  @Override
  public ExecutionProperties with(String key, Object value, boolean isTransient) {
    properties.put(key, createPropertyFromValue(key, value, isTransient));
    return this;
  }

  FlowInstanceProperty<?> createPropertyFromValue(String key, Object value, boolean isTransient) {
    return newPropertyByType(key, value).setTransient(isTransient);
  }

  private AbstractProperty<?> newPropertyByType(String key, Object value) {
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
    for (FlowInstanceProperty executionProperty : executionProperties.values()) {
      this.properties.put(executionProperty.getKey(), executionProperty);
    }
    return this;
  }

  @Override
  public <T> T value(String key, Class<T> clazz) {
    FlowInstanceProperty<?> property = properties.get(key);

    if (property == null) {
      throw new IllegalArgumentException("property '" + key + "' does not exist in execution properties");
    }

    return (T) properties.get(key).getValue();
  }

  @Override
  public <T> T value(Identifier id, Class<T> clazz) {
    return value(id.stringValue(), clazz);
  }

  @Override
  public <T> T value(Property<T> property) {
    return value(property.getValue(), property.getValueClass());
  }

  @Override
  public <T> FlowInstanceProperty<T> get(String key, Class<T> clazz) {
    return (FlowInstanceProperty<T>) properties.get(key);
  }

  @Override
  public <T> FlowInstanceProperty<T> get(Identifier id, Class<T> clazz) {
    return get(id.stringValue(), clazz);
  }

  @Override
  public <T> FlowInstanceProperty<T> get(Property<?> property, Class<T> clazz) {
    return get(property.getValue(), clazz);
  }

  @Override
  public FlowInstanceProperty<?> get(String key) {
    return properties.get(key);
  }

  @Override
  public FlowInstanceProperty<?> get(Identifier id) {
    return get(id.stringValue());
  }

  @Override
  public FlowInstanceProperty<?> get(Property<?> property) {
    return get(property.getValue());
  }

  @Override
  public Collection<FlowInstanceProperty<?>> values() {
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
