package brainslug.flow.execution.instance;

public interface FlowInstanceProperty<ValueType> {
  String getKey();

  ValueType getValue();

  boolean isTransient();
}
