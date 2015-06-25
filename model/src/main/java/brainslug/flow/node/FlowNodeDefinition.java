package brainslug.flow.node;

import brainslug.flow.definition.EnumIdentifier;
import brainslug.flow.definition.Identifier;
import brainslug.flow.definition.StringIdentifier;
import brainslug.flow.path.FlowEdgeDefinition;
import brainslug.util.MixableBase;

import java.util.ArrayList;
import java.util.List;

public class FlowNodeDefinition<T extends FlowNodeDefinition> extends MixableBase<FlowNodeDefinition> {

  Identifier id;
  String displayName;
  String documentation;

  List<FlowEdgeDefinition> incoming = new ArrayList<FlowEdgeDefinition>();
  List<FlowEdgeDefinition> outgoing = new ArrayList<FlowEdgeDefinition>();

  public T id(Enum id) {
    this.id = new EnumIdentifier(id);
    return self();
  }

  public T id(String id) {
    this.id = new StringIdentifier(id);
    return self();
  }

  public T id(Identifier id) {
    this.id = id;
    return self();
  }

  public T display(String displayName) {
    this.displayName = displayName;
    return self();
  }

  public T documentation(String documentation) {
    this.documentation = documentation;
    return self();
  }

  public T self() {
    return (T) this;
  }

  public FlowNodeDefinition<T> addIncoming(FlowNodeDefinition source) {
    incoming.add(new FlowEdgeDefinition(source, this));
    return this;
  }

  public FlowNodeDefinition<T> addOutgoing(FlowNodeDefinition target) {
    outgoing.add(new FlowEdgeDefinition(this, target));
    return this;
  }

  public List<FlowEdgeDefinition> getIncoming() {
    return incoming;
  }

  public List<FlowEdgeDefinition> getOutgoing() {
    return outgoing;
  }

  public Identifier getId() {
    return id;
  }

  public String getDisplayName() {
    return displayName == null ? "" : displayName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FlowNodeDefinition that = (FlowNodeDefinition) o;

    if (id != null ? !id.equals(that.id) : that.id != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "FlowNodeDefinition{" +
      "getIdentifier=" + id +
      ", incoming=" + incoming +
      ", outgoing=" + outgoing +
      '}';
  }
}
