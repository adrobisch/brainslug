package brainslug.flow.context;

public interface ExecutionProperty {
  Object getObjectValue();

  String getKey();

  <T> T as(Class<T> clazz);
}
