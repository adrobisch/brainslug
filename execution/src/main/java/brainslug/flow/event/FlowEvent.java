package brainslug.flow.event;

import brainslug.flow.model.EnumIdentifier;
import brainslug.flow.model.Identifier;

public class FlowEvent {

  protected Identifier id;
  protected Identifier definitionId;
  protected Identifier instanceId;

  public Identifier getId() {
    return id;
  }

  public Identifier getDefinitionId() {
    return definitionId;
  }

  public Identifier getInstanceId() {
    return instanceId;
  }

  public FlowEvent id(Identifier id) {
    this.id = id;
    return this;
  }

  public FlowEvent definitionId(Enum id) {
    return definitionId(new EnumIdentifier(id));
  }

  public FlowEvent definitionId(Identifier definitionId) {
    this.definitionId = definitionId;
    return this;
  }

  public FlowEvent instanceId(Enum id) {
    return instanceId(new EnumIdentifier(id));
  }

  public FlowEvent instanceId(Identifier definitionId) {
    this.instanceId = definitionId;
    return this;
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
