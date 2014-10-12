package brainslug.flow.execution;

import brainslug.flow.node.FlowNodeDefinition;
import java.util.List;

public class FlowNodeExecutionResult {
  final protected List<FlowNodeDefinition> nextNodes;

  public FlowNodeExecutionResult(List<FlowNodeDefinition> nextNodes) {
    this.nextNodes = nextNodes;
  }

  public List<FlowNodeDefinition> getNextNodes() {
    return nextNodes;
  }
}