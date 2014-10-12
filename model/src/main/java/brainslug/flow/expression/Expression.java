package brainslug.flow.expression;

public class Expression<T> {
  T value;

  public Expression(T expression) {
    this.value = expression;
  }

  public T getValue() {
    return value;
  }

  public String getString() {
    return value == null ? "" : value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Expression that = (Expression) o;

    if (value != null ? !value.equals(that.value) : that.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

  @Override
  public String toString() {
    return getString();
  }
}
