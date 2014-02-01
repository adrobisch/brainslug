package brainslug.flow.context;

public interface Registry {
  <T> T getService(Class<T> serviceClass);
  <T> void registerService(Class<T> serviceClass, T serviceInstance);
}
