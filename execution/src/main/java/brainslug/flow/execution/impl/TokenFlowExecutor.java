package brainslug.flow.execution.impl;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.*;
import brainslug.flow.listener.EventType;
import brainslug.flow.listener.TriggerContext;
import brainslug.flow.model.*;
import brainslug.flow.model.marker.IntermediateEvent;
import brainslug.flow.model.marker.StartEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenFlowExecutor implements FlowExecutor {

  private Logger log = LoggerFactory.getLogger(TokenFlowExecutor.class);

  protected BrainslugContext context;
  protected TokenStore tokenStore;

  Map<Class<? extends FlowNodeDefinition>, FlowNodeExectuor> nodeExecutors = new HashMap<Class<? extends FlowNodeDefinition>, FlowNodeExectuor>();

  public TokenFlowExecutor(TokenStore tokenStore) {
    this.tokenStore = tokenStore;

    addNodeExecutorMappings();
  }

  void addNodeExecutorMappings() {
    nodeExecutors.put(EventDefinition.class, new DefaultNodeExecutor());
    nodeExecutors.put(ParallelDefinition.class, new DefaultNodeExecutor());
    nodeExecutors.put(MergeDefinition.class, new DefaultNodeExecutor());
    nodeExecutors.put(ChoiceDefinition.class, new ChoiceNodeExecutor());
    nodeExecutors.put(JoinDefinition.class, new JoinNodeExecutor(tokenStore));
    nodeExecutors.put(TaskDefinition.class, new TaskNodeExecutor());
  }

  FlowNodeDefinition<?> getNode(Identifier definitionId, Identifier nodeId) {
    FlowNodeDefinition<?> node = context.getFlowDefinitions().findById(definitionId).getNode(nodeId);
    if (node == null) {
      throw new IllegalArgumentException(String.format("node for does not exist %s", nodeId));
    }
    return node;
  }

  protected <T extends FlowNodeDefinition> FlowNodeExectuor<T> getNodeExecutor(T nodeDefinition) {
    FlowNodeExectuor<T> nodeExecutor = nodeExecutors.get(nodeDefinition.getClass());
    if (nodeExecutor == null) {
      throw new IllegalArgumentException(String.format("no executor found for node definition %s", nodeDefinition));
    }
    if (nodeExecutor instanceof TokenStoreAware) {
      ((TokenStoreAware) nodeExecutor).setTokenStore(tokenStore);
    }
    return nodeExecutor;
  }

  @Override
  public void setContext(BrainslugContext context) {
    this.context = context;
  }

  @Override
  public Identifier startFlow(Identifier definitionId, Identifier nodeId) {
    FlowNodeDefinition<?> node = getStartNodeDefinition(definitionId, nodeId);
    Identifier instanceId = context.getIdGenerator().generateId();

    tokenStore.createInstance(instanceId);
    tokenStore.addToken(instanceId, nodeId, new Token(nodeId));

    context.trigger(new TriggerContext().sourceNodeId(node.getId()).nodeId(node.getId()).definitionId(definitionId).instanceId(instanceId));

    return instanceId;
  }

  private FlowNodeDefinition<?> getStartNodeDefinition(Identifier definitionId, Identifier nodeId) {
    FlowNodeDefinition<?> node = getNode(definitionId, nodeId);

    if (!node.hasMixin(StartEvent.class)) {
      throw new IllegalArgumentException("flow must be started with start event");
    }

    return node;
  }

  @Override
  public void trigger(TriggerContext triggerContext) {
    log.debug("triggering {}", triggerContext);

    FlowNodeDefinition node = getNode(triggerContext.getDefinitionId(), triggerContext.getNodeId());
    FlowNodeExectuor<FlowNodeDefinition> nodeExecutor = getNodeExecutor(node);

    DefaultExecutionContext executionContext = new DefaultExecutionContext(triggerContext, context);

    context.getListenerManager().notifyListeners(EventType.BEFORE_EXECUTION, triggerContext);
    List<FlowNodeDefinition> next = nodeExecutor.execute(node, executionContext);
    context.getListenerManager().notifyListeners(EventType.AFTER_EXECUTION, triggerContext);

    triggerNext(triggerContext, node, next);
  }

  protected void triggerNext(TriggerContext event, FlowNodeDefinition<?> node, List<FlowNodeDefinition> next) {
    for (FlowNodeDefinition nextNode : next) {
      addToken(event, node, nextNode);
      if (!waitingForExternalTrigger(nextNode)) {
        context.trigger(createTriggerContextForNextNode(event, node, nextNode));
      }
    }
  }

  private boolean waitingForExternalTrigger(FlowNodeDefinition nextNode) {
    if (nextNode.hasMixin(IntermediateEvent.class)) {
      return true;
    }
    return false;
  }

  private TriggerContext createTriggerContextForNextNode(TriggerContext<?> event, FlowNodeDefinition<?> node, FlowNodeDefinition nextNode) {
    return new TriggerContext()
          .nodeId(nextNode.getId())
          .sourceNodeId(node.getId())
          .definitionId(event.getDefinitionId())
          .instanceId(event.getInstanceId())
          .properties(event.getProperties());
  }

  private void addToken(TriggerContext event, FlowNodeDefinition<?> node, FlowNodeDefinition nextNode) {
    if (event.getInstanceId() != null) {
      tokenStore.addToken(event.getInstanceId(), nextNode.getId(), new Token(node.getId()));
    }
  }
}
