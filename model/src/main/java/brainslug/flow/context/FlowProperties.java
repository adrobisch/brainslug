package brainslug.flow.context;

import brainslug.flow.definition.Identifier;

import java.util.Collection;

public interface FlowProperties<PropertyType> {
  FlowProperties<PropertyType> with(String key, Object value);

  FlowProperties<PropertyType> with(Identifier key, Object value);

  FlowProperties<PropertyType> withAll(FlowProperties<PropertyType> executionProperties);

  <T> T getValue(String key, Class<T> clazz);

  PropertyType get(String key);

  <T> ExecutionProperty<T> getProperty(String key, Class<T> clazz);

  <T extends PropertyType> Collection<T> getValues();
}
