package brainslug.flow.execution.property;

import brainslug.flow.execution.instance.FlowInstanceProperty;

public class AbstractProperty<PropertyType> implements FlowInstanceProperty<PropertyType> {

  PropertyType value;
  String key;
  boolean isTransient = false;

  public AbstractProperty(String key, PropertyType value) {
    this.key = key;
    this.value = value;
  }

  public AbstractProperty<PropertyType> setTransient(boolean isTransient) {
    this.isTransient = isTransient;
    return this;
  }

  @Override
  public boolean isTransient() {
    return isTransient;
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public PropertyType getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AbstractProperty<?> that = (AbstractProperty<?>) o;

    if (value != null ? !value.equals(that.value) : that.value != null) return false;
    return !(key != null ? !key.equals(that.key) : that.key != null);

  }

  @Override
  public int hashCode() {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (key != null ? key.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return getClass().getName() + "{" +
            "value=" + value +
            ", key='" + key + '\'' +
            '}';
  }
}
