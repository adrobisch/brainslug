package brainslug.flow.instance;

public interface FlowInstanceProperty<ValueType> {
  String getKey();

  ValueType getValue();

  boolean isTransient();
}
