package brainslug.flow.execution.node;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.instance.FlowInstanceToken;
import brainslug.flow.execution.instance.FlowInstanceTokenList;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.util.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowNodeExecutionResult {
  protected List<TokenRemoval> removedTokens = new ArrayList<TokenRemoval>();
  protected List<FlowNodeDefinition> nextNodes;
  protected boolean failed;
  protected Exception exception;
  private FlowNodeDefinition<?> executedNode;

  public FlowNodeExecutionResult(FlowNodeDefinition<?> executedNode) {
    this.executedNode = executedNode;
    this.nextNodes = new ArrayList<FlowNodeDefinition>();
  }

  public FlowNodeExecutionResult(FlowNodeDefinition<?> executedNode, List<FlowNodeDefinition> nextNodes) {
    this.executedNode = executedNode;
    this.nextNodes = nextNodes;
  }

  public List<FlowNodeDefinition> getNextNodes() {
    return nextNodes;
  }

  public List<TokenRemoval> getRemovedTokens() {
    return removedTokens;
  }

  public FlowNodeExecutionResult withNext(FlowNodeDefinition next) {
    nextNodes.add(next);
    return this;
  }

  public FlowNodeExecutionResult withFirstIncomingTokensRemoved(FlowInstanceTokenList tokenList) {
    List<FlowInstanceToken> nodeTokens = tokenList.getNodeTokens(executedNode.getId());
    Map<Identifier, Boolean> removedFrom = new HashMap<Identifier, Boolean>();
    for (FlowInstanceToken nodeToken : nodeTokens) {
      if (nodeToken.getSourceNodeId().isPresent() && removedFrom.get(nodeToken.getSourceNodeId().get()) == null) {
        withRemovedTokens(nodeToken.getNodeId(), nodeToken.getSourceNodeId(), 1);
        removedFrom.put(nodeToken.getSourceNodeId().get(), true);
      } else {
        withRemovedTokens(nodeToken.getNodeId(), Option.<Identifier>empty(), 1);
      }
    }
    return this;
  }

  public FlowNodeExecutionResult withRemovedTokens(Identifier nodeId, Option<Identifier> sourceId, Integer quantity) {
    removedTokens.add(new TokenRemoval(nodeId, sourceId, quantity));
    return this;
  }

  public boolean isFailed() {
    return failed;
  }

  public FlowNodeExecutionResult failed(boolean failed) {
    this.failed = failed;
    return this;
  }

  public Option<Exception> getException() {
    return Option.of(exception);
  }

  public FlowNodeExecutionResult setException(Exception exception) {
    this.exception = exception;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FlowNodeExecutionResult that = (FlowNodeExecutionResult) o;

    if (failed != that.failed) return false;
    if (removedTokens != null ? !removedTokens.equals(that.removedTokens) : that.removedTokens != null) return false;
    if (nextNodes != null ? !nextNodes.equals(that.nextNodes) : that.nextNodes != null) return false;
    return !(executedNode != null ? !executedNode.equals(that.executedNode) : that.executedNode != null);

  }

  @Override
  public int hashCode() {
    int result = removedTokens != null ? removedTokens.hashCode() : 0;
    result = 31 * result + (nextNodes != null ? nextNodes.hashCode() : 0);
    result = 31 * result + (failed ? 1 : 0);
    result = 31 * result + (executedNode != null ? executedNode.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "FlowNodeExecutionResult{" +
            "removedTokens=" + removedTokens +
            ", nextNodes=" + nextNodes +
            ", failed=" + failed +
            ", executedNode=" + executedNode +
            '}';
  }

  public static class TokenRemoval {
    Identifier nodeId;
    Option<Identifier> sourceId;
    Integer quantity;

    public TokenRemoval(Identifier nodeId, Option<Identifier> sourceId, Integer quantity) {
      this.nodeId = nodeId;
      this.sourceId = sourceId;
      this.quantity = quantity;
    }

    public Identifier getNodeId() {
      return nodeId;
    }

    public Option<Identifier> getSourceId() {
      return sourceId;
    }

    public Integer getQuantity() {
      return quantity;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TokenRemoval that = (TokenRemoval) o;

      if (nodeId != null ? !nodeId.equals(that.nodeId) : that.nodeId != null) return false;
      if (sourceId != null ? !sourceId.equals(that.sourceId) : that.sourceId != null) return false;
      return !(quantity != null ? !quantity.equals(that.quantity) : that.quantity != null);

    }

    @Override
    public int hashCode() {
      int result = nodeId != null ? nodeId.hashCode() : 0;
      result = 31 * result + (sourceId != null ? sourceId.hashCode() : 0);
      result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
      return result;
    }

    @Override
    public String toString() {
      return "TokenRemoval{" +
              "nodeId=" + nodeId +
              ", sourceId=" + sourceId +
              ", quantity=" + quantity +
              '}';
    }
  }
}