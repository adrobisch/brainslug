package brainslug.flow.path;

import brainslug.flow.node.FlowNodeDefinition;

public class FlowEdgeDefinition {
  FlowNodeDefinition<?> source;
  FlowNodeDefinition<?> target;

  String displayName;

  public FlowEdgeDefinition(FlowNodeDefinition source, FlowNodeDefinition target) {
    this.source = source;
    this.target = target;
  }

  public FlowEdgeDefinition display(String displayName) {
    this.displayName = displayName;
    return this;
  }

  public FlowNodeDefinition<?> getSource() {
    return source;
  }

  public FlowNodeDefinition<?> getTarget() {
    return target;
  }

  public String getDisplayName() {
    return displayName == null ? "" : displayName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FlowEdgeDefinition that = (FlowEdgeDefinition) o;

    if (source.getId() != null ? !source.getId().equals(that.source.getId()) : that.source.getId() != null) return false;
    if (target.getId() != null ? !target.getId().equals(that.target.getId()) : that.target.getId() != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = source.getId() != null ? source.getId().hashCode() : 0;
    result = 31 * result + (target.getId() != null ? target.getId().hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "FlowEdgeDefinition{" +
      "source=" + source.getId() +
      ", target=" + target.getId() +
      '}';
  }
}
