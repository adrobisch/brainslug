package brainslug.flow.context;

public interface ExecutionProperty<ValueType> {
  ValueType getValue();

  String getKey();

  <T> T as(Class<T> clazz);
}
