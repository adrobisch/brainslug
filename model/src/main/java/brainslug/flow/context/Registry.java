package brainslug.flow.context;

public interface Registry {
  <T> T getService(String name, Class<T> clazz);
  Object getService(String name);
  <T> T getService(Class<T> serviceClass);

  <T> void registerService(String name, T serviceInstance);
  <T> void registerService(Class<T> serviceClass, T serviceInstance);
}
