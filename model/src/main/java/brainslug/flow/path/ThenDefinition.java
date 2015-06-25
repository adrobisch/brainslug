package brainslug.flow.path;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.expression.EqualsExpression;
import brainslug.flow.expression.Expression;
import brainslug.flow.expression.Value;
import brainslug.flow.node.ChoiceDefinition;

public class ThenDefinition extends FlowPathDefinition<ThenDefinition> {

  private final ChoiceDefinition choiceDefinition;
  private final Expression expression;

  public ThenDefinition(Expression expression, FlowDefinition definition, ChoiceDefinition choiceDefinition) {
    super(definition, choiceDefinition);

    this.expression = expression;
    this.choiceDefinition = choiceDefinition;
  }

  public ChoiceDefinition or() {
    return getChoiceNode();
  }

  public ThenDefinition otherwise() {
    ThenDefinition otherwise = new ThenDefinition(new EqualsExpression<Value<Boolean>, Value<Boolean>>(new Value<Boolean>(true), new Value<Boolean>(true)), definition, choiceDefinition);
    getChoiceNode().setOtherwisePath(otherwise);
    return otherwise;
  }

  protected ChoiceDefinition getChoiceNode() {
    return choiceDefinition;
  }

  public Expression getExpression() {
    return expression;
  }
}
