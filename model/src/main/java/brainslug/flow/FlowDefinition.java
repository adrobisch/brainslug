package brainslug.flow;

import brainslug.flow.expression.PredicateDefinition;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.util.Option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A FlowDefinition is used to describe paths of actions for
 * a desired outcome. It consists of a set of nodes which are typed to define their behaviour
 * and connected according to the sequence of execution.
 */
public class FlowDefinition {

  private Identifier id;
  private String name;

  List<FlowNodeDefinition> nodes = new ArrayList<FlowNodeDefinition>();
  Map<Identifier, PredicateDefinition> goalPredicates = new HashMap<Identifier, PredicateDefinition>();

  public FlowDefinition() {
  }

  public Option<PredicateDefinition> getGoalPredicate(Identifier goalId) {
    return Option.of(goalPredicates.get(goalId));
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

  public <T extends FlowNodeDefinition> T getNode(Identifier id, Class<T> clazz) {
    FlowNodeDefinition<?> node = getNode(id);
    if (!clazz.isAssignableFrom(node.getClass())) {
      throw new IllegalArgumentException("Requested node should have type " + clazz.getName());
    }
    return (T) node;
  }

  public List<FlowNodeDefinition> getNodes() {
    return nodes;
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

  public void addGoal(Identifier id, PredicateDefinition predicate) {
    goalPredicates.put(id, predicate);
  }
}
