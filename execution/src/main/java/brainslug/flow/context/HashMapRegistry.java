package brainslug.flow.context;

import java.util.HashMap;

public class HashMapRegistry implements Registry {

  HashMap<Class<?>, Object> registry = new HashMap<Class<?>, Object>();

  @Override
  public <T> T getService(Class<T> serviceClass) {
    if(registry.get(serviceClass) == null) {
      throw new IllegalStateException("no service with class " + serviceClass + " registered");
    }
    return (T) registry.get(serviceClass);
  }

  @Override
  public <T> void registerService(Class<T> serviceClass, T serviceInstance) {
    if(registry.get(serviceClass) != null) {
      throw new IllegalStateException("service class already registered " + serviceClass);
    }
    registry.put(serviceClass, serviceInstance);
  }
}
