package brainslug.flow.model.expression;

public class Constant<T> {
  T value;

  public Constant(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Constant constant = (Constant) o;

    if (value != null ? !value.equals(constant.value) : constant.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Constant{" +
      "value=" + value +
      '}';
  }
}
