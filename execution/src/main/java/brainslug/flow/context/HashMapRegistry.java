package brainslug.flow.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapRegistry implements Registry {

  Map<Class<?>, Object> classesRegistry = new ConcurrentHashMap<Class<?>, Object>();
  Map<String, Object> nameRegistry = new ConcurrentHashMap<String, Object>();

  @Override
  public <T> T getService(Class<T> serviceClass) {
    if(classesRegistry.get(serviceClass) == null) {
      throw new IllegalStateException("no service with class " + serviceClass + " registered");
    }
    return (T) classesRegistry.get(serviceClass);
  }

  @Override
  public <T> void registerService(Class<T> serviceClass, T serviceInstance) {
    if(classesRegistry.get(serviceClass) != null) {
      throw new IllegalStateException("service class already registered " + serviceClass);
    }
    classesRegistry.put(serviceClass, serviceInstance);
  }

  @Override
  public <T> T getService(String name, Class<T> clazz) {
    if(nameRegistry.get(name) == null) {
      throw new IllegalStateException("no service with name " + name + " registered");
    }
    return (T) nameRegistry.get(name);
  }

  @Override
  public Object getService(String name) {
    return getService(name, Object.class);
  }

  @Override
  public <T> void registerService(String name, T serviceInstance) {
    if(nameRegistry.get(name) != null) {
      throw new IllegalStateException("service already registered " + name);
    }
    nameRegistry.put(name, serviceInstance);
  }

}
