package brainslug.flow.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapRegistry implements Registry {

  Map<Class<?>, Object> registry = new ConcurrentHashMap<Class<?>, Object>();

  @Override
  public <T> T getService(Class<T> serviceClass) {
    if(registry.get(serviceClass) == null) {
      throw new IllegalStateException("no method with class " + serviceClass + " registered");
    }
    return (T) registry.get(serviceClass);
  }

  @Override
  public <T> void registerService(Class<T> serviceClass, T serviceInstance) {
    if(registry.get(serviceClass) != null) {
      throw new IllegalStateException("method class already registered " + serviceClass);
    }
    registry.put(serviceClass, serviceInstance);
  }
}
