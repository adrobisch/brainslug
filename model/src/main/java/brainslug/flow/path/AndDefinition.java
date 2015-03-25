package brainslug.flow.path;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.node.ParallelDefinition;

public class AndDefinition extends FlowPathDefinition<AndDefinition> {
  public AndDefinition(FlowDefinition definition, FlowNodeDefinition<ParallelDefinition> startNode) {
    super(definition, startNode);
  }

  public AndDefinition and() {
    return ((ParallelDefinition) getStartNode()).fork();
  }
}
