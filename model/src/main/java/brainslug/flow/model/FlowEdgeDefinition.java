package brainslug.flow.model;

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

    if (source.id != null ? !source.id.equals(that.source.id) : that.source.id != null) return false;
    if (target.id != null ? !target.id.equals(that.target.id) : that.target.id != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = source.id != null ? source.id.hashCode() : 0;
    result = 31 * result + (target.id != null ? target.id.hashCode() : 0);
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
