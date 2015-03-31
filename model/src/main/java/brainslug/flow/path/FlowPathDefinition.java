package brainslug.flow.path;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.definition.Identifier;
import brainslug.flow.node.ChoiceDefinition;
import brainslug.flow.node.EventDefinition;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.flow.node.ParallelDefinition;
import brainslug.flow.node.event.AbstractEventDefinition;
import brainslug.flow.node.event.EndEvent;
import brainslug.flow.node.event.IntermediateEvent;
import brainslug.flow.node.task.AbstractTaskDefinition;

import java.util.LinkedList;

public class FlowPathDefinition<Self extends FlowPathDefinition> {
  protected final FlowDefinition definition;
  protected final FlowNodeDefinition startNode;
  LinkedList<FlowNodeDefinition> pathNodes = new LinkedList<FlowNodeDefinition>();

  public FlowPathDefinition(FlowDefinition definition, FlowNodeDefinition startNode) {
    this.definition = definition;
    this.startNode = startNode;
  }

  public ChoiceDefinition choice(Identifier id) {
    return appendNode(new ChoiceDefinition(this)).id(id).self();
  }

  public AndDefinition parallel(Identifier id) {
    return appendNode(new ParallelDefinition(this).id(id)).self().fork();
  }

  public Self execute(AbstractTaskDefinition taskDefinition) {
    appendNode(taskDefinition);
    return then();
  }

  public Self waitFor(AbstractEventDefinition eventDefinition) {
    eventDefinition.with(new IntermediateEvent());
    appendNode(eventDefinition);
    return then();
  }

  public Self end(Identifier endId) {
    return end(new EventDefinition().id(endId));
  }

  public Self end(FlowNodeDefinition<EventDefinition> eventDefinition) {
    if (definition.contains(eventDefinition)) {
      addToPath(eventDefinition);
      connect(pathNodes.getLast(), definition.getNode(eventDefinition.getId()));
      return then();
    }

    eventDefinition.with(new EndEvent());
    appendNode(eventDefinition);
    return then();
  }

  protected <T extends FlowNodeDefinition> T appendNode(T flowNodeDefinition) {
    if (definition.contains(flowNodeDefinition)) {
      throw new IllegalStateException("Node already exists");
    }

    if (pathNodes.isEmpty()) {
      connect(getStartNode(), flowNodeDefinition);
    } else {
      connect(getPathNodes().getLast(), flowNodeDefinition);
    }

    addToPath(flowNodeDefinition);
    definition.addNode(flowNodeDefinition);

    return flowNodeDefinition;
  }

  protected <T extends FlowNodeDefinition> void addToPath(T flowNodeDefinition) {
    pathNodes.add(flowNodeDefinition);
  }

  protected <T extends FlowNodeDefinition> void connect(FlowNodeDefinition previousNode, T flowNodeDefinition) {
    previousNode.addOutgoing(flowNodeDefinition);
    flowNodeDefinition.addIncoming(previousNode);
  }

  public Self then() {
    return (Self)  this;
  }

  public Self then(FlowNodeDefinition<?> flowNodeDefinition) {
    this.appendNode(flowNodeDefinition);
    return then();
  }

  public LinkedList<FlowNodeDefinition> getPathNodes() {
    return pathNodes;
  }

  public FlowNodeDefinition getStartNode() {
    return startNode;
  }

  public FlowNodeDefinition getFirstNode() {
    return getPathNodes().peekFirst();
  }

  public FlowDefinition getDefinition() {
    return definition;
  }
}
