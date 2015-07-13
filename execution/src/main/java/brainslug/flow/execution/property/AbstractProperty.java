package brainslug.flow.execution.property;

import brainslug.flow.instance.FlowInstanceProperty;

public class AbstractProperty<PropertyType> implements FlowInstanceProperty<PropertyType> {

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
}
