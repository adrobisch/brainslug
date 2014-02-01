package brainslug.flow.model;

import java.util.ArrayList;
import java.util.List;

public class FlowDefinition {

  private Identifier id;
  private String name;

  List<FlowNodeDefinition> nodes = new ArrayList<FlowNodeDefinition>();

  public FlowDefinition() {
  }

  public Identifier getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public <T extends FlowNodeDefinition> T addNode(T flowNodeDefinition) {
    nodes.add(flowNodeDefinition);
    return flowNodeDefinition;
  }

  public FlowNodeDefinition<?> getNode(Identifier id) {
    for (FlowNodeDefinition node: nodes) {
      if(node.getId().equals(id)) {
        return node;
      }
    }
    throw new IllegalArgumentException("Node with id " + id + " does not exist.");
  }

  public FlowNodeDefinition<?> getNode(Identifier id, Class clazz) {
    FlowNodeDefinition<?> node = getNode(id);
    if (!node.getClass().isAssignableFrom(clazz)) {
      throw new IllegalArgumentException("Requested node should have type " + clazz.getName());
    }
    return node;
  }

  public List<FlowNodeDefinition> getNodes() {
    return nodes;
  }

  public FlowDefinition name(String name) {
    this.name = name;
    return this;
  }

  public FlowDefinition id(Identifier id) {
    this.id = id;
    return this;
  }
}
