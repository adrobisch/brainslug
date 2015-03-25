package brainslug.flow.execution.property;

import brainslug.flow.context.ExecutionProperty;

public class AbstractProperty<PropertyType> implements ExecutionProperty<PropertyType> {

  PropertyType value;
  String key;

  public AbstractProperty(String key, PropertyType value) {
    this.key = key;
    this.value = value;
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
  public <AsType> AsType as(Class<AsType> clazz) {
    return (AsType) getValue();
  }
}
