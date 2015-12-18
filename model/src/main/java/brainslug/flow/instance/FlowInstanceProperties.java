package brainslug.flow.instance;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.Property;

import java.util.Collection;

public interface FlowInstanceProperties<SelfType extends FlowInstanceProperties, PropertyType> {
  SelfType with(String key, Object value);

  SelfType with(String key, Object value, boolean isTransient);

  SelfType with(Identifier key, Object value);

  SelfType with(Identifier key, Object value, boolean isTransient);

  SelfType with(Property<?> key, Object value);

  SelfType with(Property<?> key, Object value, boolean isTransient);

  SelfType withAll(SelfType executionProperties);

  PropertyType get(String key);

  PropertyType get(Identifier id);

  PropertyType get(Property<?> property);

  <T> FlowInstanceProperty<T> get(String key, Class<T> clazz);

  <T> FlowInstanceProperty<T> get(Identifier key, Class<T> clazz);

  <T> FlowInstanceProperty<T> get(Property<?> property, Class<T> clazz);

  <T> T value(String key, Class<T> clazz);

  <T> T value(Identifier id, Class<T> clazz);

  <T> T value(Property<T> property);

  <T extends PropertyType> Collection<T> values();
}
