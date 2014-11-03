package brainslug.flow.node;

import brainslug.flow.expression.PredicateDefinition;
import brainslug.util.Option;

public class EventDefinition extends FlowNodeDefinition<EventDefinition> {

  private PredicateDefinition predicateDefinition;

  public EventDefinition onlyIf(PredicateDefinition predicateDefinition) {
    this.predicateDefinition = predicateDefinition;
    return this;
  }

  public Option<PredicateDefinition> getPredicateDefinition() {
    return Option.of(predicateDefinition);
  }
}
