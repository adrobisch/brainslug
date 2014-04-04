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

  public TokenFlowExecutor(BrainslugContext context) {
    this.tokenStore = context.getTokenStore();

    addNodeExecutorMappings();
  }

  protected void addNodeExecutorMappings() {
    nodeExecutors.put(EventDefinition.class, new DefaultNodeExecutor());
    nodeExecutors.put(ParallelDefinition.class, new DefaultNodeExecutor());
    nodeExecutors.put(MergeDefinition.class, new DefaultNodeExecutor());
    nodeExecutors.put(ChoiceDefinition.class, new ChoiceNodeExecutor());
    nodeExecutors.put(JoinDefinition.class, new JoinNodeExecutor(tokenStore));
    nodeExecutors.put(TaskDefinition.class, new TaskNodeExecutor());
  }

  FlowNodeDefinition<?> getNode(Identifier definitionId, Identifier nodeId) {
    FlowNodeDefinition<?> node = context.getDefinitionStore().findById(definitionId).getNode(nodeId);
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
  public Identifier startFlow(TriggerContext<?> trigger) {
    FlowNodeDefinition<?> node = getStartNodeDefinition(trigger.getDefinitionId(), trigger.getNodeId());
    Identifier instanceId = context.getIdGenerator().generateId();

    tokenStore.createInstance(instanceId);
    tokenStore.addToken(instanceId, trigger.getNodeId(), new Token(trigger.getNodeId()));

    context.getPropertyStore().storeProperties(trigger.getInstanceId(), trigger.getProperties());
    context.trigger(new TriggerContext().sourceNodeId(node.getId())
      .nodeId(node.getId())
      .definitionId(trigger.getDefinitionId())
      .instanceId(instanceId));

    return instanceId;
  }

  protected FlowNodeDefinition<?> getStartNodeDefinition(Identifier definitionId, Identifier nodeId) {
    FlowNodeDefinition<?> node = getNode(definitionId, nodeId);

    if (!node.hasMixin(StartEvent.class)) {
      throw new IllegalArgumentException("flow must be started with start event");
    }

    return node;
  }

  @Override
  public void trigger(TriggerContext<?> triggerContext) {
    log.debug("triggering {}", triggerContext);

    FlowNodeDefinition node = getNode(triggerContext.getDefinitionId(), triggerContext.getNodeId());
    FlowNodeExectuor<FlowNodeDefinition> nodeExecutor = getNodeExecutor(node);

    ExecutionContext executionContext = createExecutionContext(triggerContext);

    context.getListenerManager().notifyListeners(EventType.BEFORE_EXECUTION, triggerContext);
    List<FlowNodeDefinition> next = nodeExecutor.execute(node, executionContext);
    context.getPropertyStore().storeProperties(triggerContext.getInstanceId(), triggerContext.getProperties());
    context.getListenerManager().notifyListeners(EventType.AFTER_EXECUTION, triggerContext);

    triggerNext(triggerContext, node, next);
  }

  protected ExecutionContext createExecutionContext(TriggerContext triggerContext) {
    DefaultExecutionContext executionContext = new DefaultExecutionContext(triggerContext, context);

    if(triggerContext.getInstanceId() != null) {
      executionContext.getTrigger().properties(mergeProperties(triggerContext, executionContext));
    }

    return executionContext;
  }

  protected Map<Object, Object> mergeProperties(TriggerContext triggerContext, DefaultExecutionContext executionContext) {
    Map<Object, Object> properties = context.getPropertyStore()
        .loadProperties(executionContext.getTrigger().getInstanceId());
    properties.putAll(triggerContext.getProperties());
    return properties;
  }

  protected void triggerNext(TriggerContext event, FlowNodeDefinition<?> node, List<FlowNodeDefinition> next) {
    for (FlowNodeDefinition nextNode : next) {
      addToken(event, node, nextNode);
      if (!waitingForExternalTrigger(nextNode)) {
        context.trigger(createTriggerContextForNextNode(event, node, nextNode));
      }
    }
  }

  protected boolean waitingForExternalTrigger(FlowNodeDefinition nextNode) {
    if (nextNode.hasMixin(IntermediateEvent.class)) {
      return true;
    }
    return false;
  }

  protected TriggerContext createTriggerContextForNextNode(TriggerContext<?> event, FlowNodeDefinition<?> currentNode, FlowNodeDefinition nextNode) {
    return new TriggerContext()
          .nodeId(nextNode.getId())
          .sourceNodeId(currentNode.getId())
          .definitionId(event.getDefinitionId())
          .instanceId(event.getInstanceId())
          .properties(event.getProperties());
  }

  protected void addToken(TriggerContext event, FlowNodeDefinition<?> node, FlowNodeDefinition nextNode) {
    if (event.getInstanceId() != null) {
      tokenStore.addToken(event.getInstanceId(), nextNode.getId(), new Token(node.getId()));
    }
  }
}
