package brainslug.flow.node;

import brainslug.flow.expression.Expression;
import brainslug.flow.path.FlowPathDefinition;
import brainslug.flow.path.ThenDefinition;
import brainslug.util.Option;

import java.util.ArrayList;
import java.util.List;

public class ChoiceDefinition extends FlowNodeDefinition<ChoiceDefinition> {

  FlowPathDefinition path;
  List<ThenDefinition> thenPaths = new ArrayList<ThenDefinition>();
  ThenDefinition otherwisePath;

  public ChoiceDefinition(FlowPathDefinition path) {
    this.path = path;
  }

  public ThenDefinition when(Expression whenExpression) {
    return addThenDefinition(new ThenDefinition(whenExpression, path.getDefinition(), this));
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
