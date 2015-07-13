package brainslug.flow.instance;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.Property;

import java.util.Collection;

public interface FlowInstanceProperties<SelfType extends FlowInstanceProperties, PropertyType> {
  SelfType with(String key, Object value);

  SelfType with(Identifier key, Object value);

  SelfType with(Property<?> key, Object value);

  SelfType withAll(SelfType executionProperties);

  PropertyType get(String key);

  <T> FlowInstanceProperty<T> getProperty(String key, Class<T> clazz);

  <T> T getValue(String key, Class<T> clazz);

  <T extends PropertyType> Collection<T> getValues();
}
