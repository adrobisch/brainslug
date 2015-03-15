package brainslug.flow.context;

import brainslug.flow.Identifier;

import java.util.Collection;

public interface FlowProperties<PropertyType> {
  FlowProperties<PropertyType> with(String key, Object value);

  FlowProperties<PropertyType> with(Identifier key, Object value);

  FlowProperties<PropertyType> withAll(FlowProperties<PropertyType> executionProperties);

  <T> T get(String key, Class<T> clazz);

  PropertyType get(String key);

  <P> P getProperty(Class<P> type);

  Collection<PropertyType> getValues();
}
