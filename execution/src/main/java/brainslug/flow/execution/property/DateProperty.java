package brainslug.flow.execution.property;

import brainslug.flow.execution.property.AbstractProperty;

import java.util.Date;

public class DateProperty extends AbstractProperty<Date> {
  public DateProperty(String key, Date value) {
    super(key, value);
  }
}
