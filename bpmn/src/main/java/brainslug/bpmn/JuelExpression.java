package brainslug.bpmn;

import brainslug.flow.model.expression.Expression;

public class JuelExpression extends Expression<String> {
  public JuelExpression(String expression) {
    super(expression);
  }

  @Override
  public String getString() {
    return String.format("${%s}", getValue());
  }
}
