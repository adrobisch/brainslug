package brainslug.flow.model.expression;

public class Expression<T> {
  T value;

  public Expression(T expression) {
    this.value = expression;
  }

  public T getValue() {
    return value;
  }

  @Override
  public String toString() {
    if (value != null) {
      return value.toString();
    }
    return "";
  }
}
