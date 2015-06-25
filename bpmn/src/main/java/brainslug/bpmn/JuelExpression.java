package brainslug.bpmn;

import brainslug.flow.expression.Value;

public class JuelExpression extends Value<String> {
  public JuelExpression(String expression) {
    super(expression);
  }

  @Override
  public String getString() {
    return String.format("${%s}", getValue());
  }
}
