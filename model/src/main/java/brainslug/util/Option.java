package brainslug.util;

import java.util.NoSuchElementException;

/**
 * A simplified port of the Java 8
 * Optional type.
 *
 * @author andreas
 */
public class Option<T> {

  private final T value;

  private Option(T value) {
    this.value = value;
  }

  public static <T> Option<T> empty() {
    return new Option<T>(null);
  }

  public static <T> Option<T> of(T value) {
    return new Option<T>(value);
  }

  public boolean isPresent() {
    return value != null;
  }

  public T get() {
    if (isPresent()) {
      return value;
    } else {
      throw new NoSuchElementException();
    }
  }

  public T orElse(T other) {
    if (isPresent()) {
      return value;
    } else {
      return other;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Option option = (Option) o;

    if (value != null ? !value.equals(option.value) : option.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return value != null ? value.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Option{" +
      "value=" + value +
      '}';
  }
}
