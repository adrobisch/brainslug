package brainslug.flow.expression;

import brainslug.flow.definition.Identifier;

public class Property<T> extends Value<Identifier> {

  private final Class<T> valueClass;

  public Property(Identifier expression, Class<T> valueClass) {
    super(expression);
    this.valueClass = valueClass;
  }

  public Class<T> getValueClass() {
    return valueClass;
  }
}
