package brainslug.flow.definition;

import brainslug.flow.node.FlowNodeDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * A FlowDefinition is used to describe paths of actions for
 * a desired outcome. It consists of a set of nodes which are typed to define their behaviour
 * and which are connected according to the sequence of execution.
 */
public class FlowDefinition {

  private Identifier id;
  private String name;

  List<FlowNodeDefinition> startNodes = new ArrayList<FlowNodeDefinition>();
  List<FlowNodeDefinition> nodes = new ArrayList<FlowNodeDefinition>();

  public FlowDefinition() {
  }

  public <T extends FlowNodeDefinition> T addNode(T flowNodeDefinition) {
    nodes.add(flowNodeDefinition);
    return flowNodeDefinition;
  }

  public <T extends FlowNodeDefinition> T addStartNode(T flowNodeDefinition) {
    startNodes.add(flowNodeDefinition);
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

  public <T extends FlowNodeDefinition> List<FlowNodeDefinition<T>> getNodesByType(Class<T> clazz) {
    List<FlowNodeDefinition<T>> nodesWithType = new ArrayList<FlowNodeDefinition<T>>();
    for (FlowNodeDefinition node: nodes) {
      if(clazz.isAssignableFrom(node.getClass())) {
        nodesWithType.add(node);
      }
    }
    return nodesWithType;
  }

  public <T extends FlowNodeDefinition> T getNode(Identifier id, Class<T> clazz) {
    FlowNodeDefinition<?> node = getNode(id);
    if (!clazz.isAssignableFrom(node.getClass())) {
      throw new IllegalArgumentException("Requested node should have type " + clazz.getName());
    }
    return (T) node;
  }

  public FlowNodeDefinition<?> requireSingleStartNode() {
    if (getStartNodes().size() != 1) {
      throw new IllegalArgumentException("required single start node, but there are " + getNodes().size());
    }
    return getStartNodes().get(0);
  }

  public List<FlowNodeDefinition> getNodes() {
    return nodes;
  }

  public List<FlowNodeDefinition> getStartNodes() {
    return startNodes;
  }

  public boolean contains(FlowNodeDefinition<?> flowNodeDefinition) {
    return getNodes().contains(flowNodeDefinition);
  }

  public Identifier getId() {
    return id;
  }

  public String getName() {
    return name;
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
