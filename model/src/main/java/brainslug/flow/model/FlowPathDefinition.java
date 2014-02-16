package brainslug.flow.model;

import brainslug.flow.model.marker.EndEvent;
import brainslug.flow.model.marker.IntermediateEvent;

import java.util.LinkedList;

public class FlowPathDefinition<T extends FlowPathDefinition> {
  final FlowDefinition definition;
  LinkedList<FlowNodeDefinition> pathNodes = new LinkedList<FlowNodeDefinition>();

  public FlowPathDefinition(FlowDefinition definition, FlowNodeDefinition startNode) {
    this.definition = definition;
    pathNodes.add(startNode);
    startNode.setFlowPathDefinition(this);
  }

  public ChoiceDefinition choice(Identifier id) {
    return appendNode(new ChoiceDefinition(this)).id(id).self();
  }

  public AndDefinition parallel(Identifier id) {
    return appendNode(new ParallelDefinition(this).id(id)).self().fork();
  }

  public T execute(AbstractTaskDefinition taskDefinition) {
    appendNode(taskDefinition);
    return then();
  }

  public T waitFor(EventDefinition eventDefinition) {
    eventDefinition.with(new IntermediateEvent());
    appendNode(eventDefinition);
    return then();
  }

  public T end(FlowNodeDefinition<EventDefinition> eventDefinition) {
    if (definition.contains(eventDefinition)) {
      addToPath(eventDefinition);
      connect(pathNodes.getLast(), definition.getNode(eventDefinition.getId()));
      return then();
    }

    eventDefinition.with(new EndEvent());
    appendNode(eventDefinition);
    return then();
  }

  private <T extends FlowNodeDefinition> T appendNode(T flowNodeDefinition) {
    if (definition.contains(flowNodeDefinition)) {
      throw new IllegalStateException("Node already exists");
    }

    connect(pathNodes.getLast(), flowNodeDefinition);

    addToPath(flowNodeDefinition);
    definition.addNode(flowNodeDefinition);


    return flowNodeDefinition;
  }

  private <T extends FlowNodeDefinition> void addToPath(T flowNodeDefinition) {
    pathNodes.add(flowNodeDefinition);
    flowNodeDefinition.setFlowPathDefinition(this);
  }

  private <T extends FlowNodeDefinition> void connect(FlowNodeDefinition previousNode, T flowNodeDefinition) {
    previousNode.addOutgoing(flowNodeDefinition);
    flowNodeDefinition.addIncoming(previousNode);
  }

  public T then() {
    return (T) this;
  }

  public LinkedList<FlowNodeDefinition> getPathNodes() {
    return pathNodes;
  }

  public FlowNodeDefinition getStartNode() {
    return pathNodes.getFirst();
  }
}
