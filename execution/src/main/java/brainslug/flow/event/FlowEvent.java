package brainslug.flow.event;

import brainslug.flow.model.EnumIdentifier;
import brainslug.flow.model.Identifier;

public class FlowEvent<T extends FlowEvent> {

  protected Identifier id;
  protected Identifier definitionId;
  protected Identifier instanceId;
  protected Identifier nodeId;

  public Identifier getId() {
    return id;
  }

  public Identifier getDefinitionId() {
    return definitionId;
  }

  public Identifier getInstanceId() {
    return instanceId;
  }

  public Identifier getNodeId() {
    return nodeId;
  }

  T self() {
    return (T) this;
  }

  public T id(Identifier id) {
    this.id = id;
    return self();
  }

  public T definitionId(Enum id) {
    return definitionId(new EnumIdentifier(id));
  }

  public T definitionId(Identifier definitionId) {
    this.definitionId = definitionId;
    return self();
  }

  public T instanceId(Identifier definitionId) {
    this.instanceId = definitionId;
    return self();
  }

  public T nodeId(Identifier nodeId) {
    this.nodeId = nodeId;
    return self();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FlowEvent flowEvent = (FlowEvent) o;

    if (definitionId != null ? !definitionId.equals(flowEvent.definitionId) : flowEvent.definitionId != null)
      return false;
    if (id != null ? !id.equals(flowEvent.id) : flowEvent.id != null) return false;
    if (instanceId != null ? !instanceId.equals(flowEvent.instanceId) : flowEvent.instanceId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (definitionId != null ? definitionId.hashCode() : 0);
    result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "FlowEvent{" +
      "id=" + id +
      ", definitionId=" + definitionId +
      ", instanceId=" + instanceId +
      '}';
  }
}
