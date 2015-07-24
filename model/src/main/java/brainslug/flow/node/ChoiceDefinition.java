package brainslug.flow.node;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.expression.Expression;
import brainslug.flow.path.ThenDefinition;
import brainslug.util.Option;

import java.util.ArrayList;
import java.util.List;

public class ChoiceDefinition extends FlowNodeDefinition<ChoiceDefinition> {

  private final FlowDefinition definition;
  List<ThenDefinition> thenPaths = new ArrayList<ThenDefinition>();
  ThenDefinition otherwisePath;

  public ChoiceDefinition(FlowDefinition definition) {
    this.definition = definition;
  }

  public ThenDefinition when(Expression whenExpression) {
    return addThenDefinition(new ThenDefinition(whenExpression, definition, this));
  }

  protected ThenDefinition addThenDefinition(ThenDefinition then) {
    thenPaths.add(then);
    return then;
  }

  public List<ThenDefinition> getThenPaths() {
    return thenPaths;
  }

  public Option<ThenDefinition> getOtherwisePath() {
    return Option.of(otherwisePath);
  }

  public void setOtherwisePath(ThenDefinition otherwisePath) {
    this.otherwisePath = otherwisePath;
  }
}
