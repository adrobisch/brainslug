package brainslug.flow.path;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.expression.EqualDefinition;
import brainslug.flow.node.ChoiceDefinition;

public class ThenDefinition extends FlowPathDefinition<ThenDefinition> {

  private final ChoiceDefinition choiceDefinition;
  private final EqualDefinition predicateDefinition;

  public ThenDefinition(EqualDefinition predicateDefinition, FlowDefinition definition, ChoiceDefinition choiceDefinition) {
    super(definition, choiceDefinition);

    this.predicateDefinition = predicateDefinition;
    this.choiceDefinition = choiceDefinition;
  }

  public ChoiceDefinition or() {
    return getChoiceNode();
  }

  public ThenDefinition otherwise() {
    ThenDefinition otherwise = new ThenDefinition(new EqualDefinition<Boolean, Boolean>(true, true), definition, choiceDefinition);
    getChoiceNode().setOtherwisePath(otherwise);
    return otherwise;
  }

  protected ChoiceDefinition getChoiceNode() {
    return choiceDefinition;
  }

  public EqualDefinition getPredicateDefinition() {
    return predicateDefinition;
  }
}
