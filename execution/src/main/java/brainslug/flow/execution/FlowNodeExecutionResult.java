package brainslug.flow.execution;

import brainslug.flow.node.FlowNodeDefinition;

import java.util.ArrayList;
import java.util.List;

public class FlowNodeExecutionResult {
  protected List<FlowNodeDefinition> nextNodes;
  protected boolean failed;

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

  public boolean isFailed() {
    return failed;
  }

  public FlowNodeExecutionResult failed(boolean failed) {
    this.failed = failed;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FlowNodeExecutionResult that = (FlowNodeExecutionResult) o;

    if (failed != that.failed) return false;
    if (nextNodes != null ? !nextNodes.equals(that.nextNodes) : that.nextNodes != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = nextNodes != null ? nextNodes.hashCode() : 0;
    result = 31 * result + (failed ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "FlowNodeExecutionResult{" +
      "nextNodes=" + nextNodes +
      ", failed=" + failed +
      '}';
  }
}