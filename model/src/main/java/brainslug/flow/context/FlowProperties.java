package brainslug.flow.context;

import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.Property;

import java.util.Collection;

public interface FlowProperties<SelfType extends FlowProperties, PropertyType> {
  SelfType with(String key, Object value);

  SelfType with(Identifier key, Object value);

  SelfType with(Property<?> key, Object value);

  SelfType withAll(SelfType executionProperties);

  <T> T getValue(String key, Class<T> clazz);

  PropertyType get(String key);

  <T> ExecutionProperty<T> getProperty(String key, Class<T> clazz);

  <T extends PropertyType> Collection<T> getValues();
}
