package brainslug.flow.model;

import brainslug.flow.model.expression.EqualDefinition;

public class ThenDefinition extends FlowPathDefinition<ThenDefinition> {

  EqualDefinition predicateDefinition;

  public ThenDefinition(EqualDefinition predicateDefinition, FlowDefinition definition, FlowNodeDefinition<ChoiceDefinition> startNode) {
    super(definition, startNode);
    this.predicateDefinition = predicateDefinition;
  }

  public ChoiceDefinition or() {
    return getStartNode();
  }

  public ThenDefinition otherwise() {
    ThenDefinition otherwise = new ThenDefinition(new EqualDefinition<Boolean, Boolean>(true, true), definition, getStartNode());
    getStartNode().setOtherwisePath(otherwise);
    return otherwise;
  }

  protected ChoiceDefinition getStartNode() {
    return (ChoiceDefinition) this.startNode;
  }

  public EqualDefinition getPredicateDefinition() {
    return predicateDefinition;
  }
}
