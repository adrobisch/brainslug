package brainslug.flow.execution;

import brainslug.flow.node.FlowNodeDefinition;

import java.util.ArrayList;
import java.util.List;

public class FlowNodeExecutionResult {
  protected List<FlowNodeDefinition> nextNodes;

  public FlowNodeExecutionResult() {
    nextNodes = new ArrayList<FlowNodeDefinition>();
  }

  public FlowNodeExecutionResult(List<FlowNodeDefinition> nextNodes) {
    this.nextNodes = nextNodes;
  }

  public List<FlowNodeDefinition> getNextNodes() {
    return nextNodes;
  }

  public FlowNodeExecutionResult withNext(FlowNodeDefinition next) {
    nextNodes.add(next);
    return this;
  }

  @Override
  public String toString() {
    return "FlowNodeExecutionResult{" +
      "nextNodes=" + nextNodes +
      '}';
  }
}