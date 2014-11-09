package brainslug.flow.execution;

import brainslug.flow.context.ExecutionProperty;

import java.util.Arrays;

public class DefaultExecutionProperty implements ExecutionProperty {
  protected String key;
  protected String valueType;

  protected Object objectValue;

  protected String stringValue;
  protected Long longValue;
  protected Double doubleValue;
  protected byte[] byteArrayValue;

  public DefaultExecutionProperty() {
  }

  public DefaultExecutionProperty(String key, String valueType, String stringValue, Long longValue, Double doubleValue, byte[] byteArrayValue) {
    this.key = key;
    this.valueType = valueType;
    this.stringValue = stringValue;
    this.longValue = longValue;
    this.doubleValue = doubleValue;
    this.byteArrayValue = byteArrayValue;
  }

  @Override
  public Object getObjectValue() {
    return objectValue;
  }

  public DefaultExecutionProperty withObjectValue(Object propertyValue) {
    this.objectValue = propertyValue;
    return this;
  }

  @Override
  public String getKey() {
    return key;
  }

  public DefaultExecutionProperty withKey(String key) {
    this.key = key;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DefaultExecutionProperty property = (DefaultExecutionProperty) o;

    if (!Arrays.equals(byteArrayValue, property.byteArrayValue)) return false;
    if (doubleValue != null ? !doubleValue.equals(property.doubleValue) : property.doubleValue != null) return false;
    if (key != null ? !key.equals(property.key) : property.key != null) return false;
    if (longValue != null ? !longValue.equals(property.longValue) : property.longValue != null) return false;
    if (objectValue != null ? !objectValue.equals(property.objectValue) : property.objectValue != null) return false;
    if (stringValue != null ? !stringValue.equals(property.stringValue) : property.stringValue != null) return false;
    if (valueType != null ? !valueType.equals(property.valueType) : property.valueType != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = key != null ? key.hashCode() : 0;
    result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
    result = 31 * result + (objectValue != null ? objectValue.hashCode() : 0);
    result = 31 * result + (stringValue != null ? stringValue.hashCode() : 0);
    result = 31 * result + (longValue != null ? longValue.hashCode() : 0);
    result = 31 * result + (doubleValue != null ? doubleValue.hashCode() : 0);
    result = 31 * result + (byteArrayValue != null ? Arrays.hashCode(byteArrayValue) : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ExecutionProperty{" +
      "key='" + key + '\'' +
      ", valueType='" + valueType + '\'' +
      ", objectValue=" + objectValue +
      ", stringValue='" + stringValue + '\'' +
      ", longValue=" + longValue +
      ", doubleValue=" + doubleValue +
      ", byteArrayValue=" + Arrays.toString(byteArrayValue) +
      '}';
  }

  @Override
  public <T> T as(Class<T> clazz) {
    if (objectValue != null) {
      return (T) objectValue;
    }
    if (clazz.equals(String.class)) {
      return (T) stringValue;
    }
    if (clazz.equals(Long.class)) {
      return (T) longValue;
    }
    if (clazz.equals(Double.class)) {
      return (T) doubleValue;
    }
    if (clazz.equals(byte[].class)) {
      return (T) byteArrayValue;
    }
    return null;
  }
}
