package brainslug.flow.event;

import brainslug.flow.model.Identifier;

public class TriggerEvent extends FlowEvent<TriggerEvent> {

  private Identifier<?> sourceNodeId;

  public TriggerEvent sourceNodeId(Identifier<?> sourceNodeId) {
    this.sourceNodeId = sourceNodeId;
    return this;
  }

  public Identifier<?> getSourceNodeId() {
    return sourceNodeId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    TriggerEvent that = (TriggerEvent) o;

    if (nodeId != null ? !nodeId.equals(that.nodeId) : that.nodeId != null) return false;
    if (sourceNodeId != null ? !sourceNodeId.equals(that.sourceNodeId) : that.sourceNodeId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (nodeId != null ? nodeId.hashCode() : 0);
    result = 31 * result + (sourceNodeId != null ? sourceNodeId.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TriggerEvent{" +
      "nodeId=" + nodeId +
      ", sourceNodeId=" + sourceNodeId +
      '}';
  }
}
