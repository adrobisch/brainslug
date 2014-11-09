package brainslug.flow.context;

import java.util.Collection;

public interface ExecutionProperties {
  ExecutionProperties put(String key, Object value);

  ExecutionProperties putAll(ExecutionProperties executionProperties);

  <T> T get(String key, Class<T> clazz);

  ExecutionProperty get(String key);

  <P> P getProperty(Class<P> type);

  Collection<ExecutionProperty> getValues();
}
