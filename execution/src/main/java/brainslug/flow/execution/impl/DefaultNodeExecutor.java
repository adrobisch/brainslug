package brainslug.flow.execution.impl;

import brainslug.flow.event.EventPath;
import brainslug.flow.event.RemoveTokenEvent;
import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.FlowNodeExectuor;
import brainslug.flow.model.FlowEdgeDefinition;
import brainslug.flow.model.FlowNodeDefinition;

import java.util.ArrayList;
import java.util.List;

import static brainslug.flow.event.EventPathFactory.topic;

public class DefaultNodeExecutor<T extends FlowNodeDefinition> implements FlowNodeExectuor<T> {

  @Override
  public List<FlowNodeDefinition> execute(T node, ExecutionContext execution) {
    pushRemoveTokenEvent(execution);
    return takeAll(node);
  }

  protected void pushRemoveTokenEvent(ExecutionContext execution) {
    execution.getBrainslugContext().getEventDispatcher().push(EventPath.TOKENSTORE_PATH,
      new RemoveTokenEvent()
        .nodeId(execution.getTrigger().getNodeId())
        .sourceNodeId(execution.getTrigger().getSourceNodeId())
        .instanceId(execution.getTrigger().getInstanceId())
        .definitionId(execution.getTrigger().getDefinitionId())
    );
  }

  protected List<FlowNodeDefinition> takeAll(FlowNodeDefinition<?> node) {
    List<FlowNodeDefinition> next = new ArrayList<FlowNodeDefinition>();

    for (FlowEdgeDefinition edge : node.getOutgoing()) {
      next.add(edge.getTarget());
    }

    return next;
  }

  protected List<FlowNodeDefinition> takeNone() {
    return new ArrayList<FlowNodeDefinition>();
  }

}
