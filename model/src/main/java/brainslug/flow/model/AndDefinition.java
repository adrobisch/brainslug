package brainslug.flow.model;

public class AndDefinition extends FlowPathDefinition<AndDefinition> {
  public AndDefinition(FlowDefinition definition, FlowNodeDefinition<ParallelDefinition> startNode) {
    super(definition, startNode);
  }

  public AndDefinition and() {
    return ((ParallelDefinition) this.startNode).fork();
  }
}
