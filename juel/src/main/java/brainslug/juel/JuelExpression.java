package brainslug.juel;

import brainslug.flow.expression.Value;

import static java.lang.String.format;

public class JuelExpression extends Value<String> {
  public JuelExpression(String expression) {
    super(expression);
  }

  @Override
  public String getString() {
    return format("${%s}", getValue());
  }
}
