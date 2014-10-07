package brainslug.flow.execution.impl;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.*;
import brainslug.flow.listener.EventType;
import brainslug.flow.execution.TriggerContext;
import brainslug.flow.model.*;
import brainslug.flow.model.marker.IntermediateEvent;
import brainslug.flow.model.marker.StartEvent;
import brainslug.util.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenFlowExecutor implements FlowExecutor {

  private Logger log = LoggerFactory.getLogger(TokenFlowExecutor.class);

  protected BrainslugContext context;
  protected TokenStore tokenStore;

  Map<Class<? extends FlowNodeDefinition>, FlowNodeExecutor> nodeExecutors = new HashMap<Class<? extends FlowNodeDefinition>, FlowNodeExecutor>();

  public TokenFlowExecutor(BrainslugContext context) {
    this.context = context;
    this.tokenStore = context.getTokenStore();

    addNodeExecutorMappings();
  }

  protected void addNodeExecutorMappings() {
    nodeExecutors.put(EventDefinition.class, new DefaultNodeExecutor().withTokenStore(tokenStore));
    nodeExecutors.put(ParallelDefinition.class, new DefaultNodeExecutor().withTokenStore(tokenStore));
    nodeExecutors.put(MergeDefinition.class, new DefaultNodeExecutor().withTokenStore(tokenStore));
    nodeExecutors.put(ChoiceDefinition.class, new ChoiceNodeExecutor().withTokenStore(tokenStore));
    nodeExecutors.put(JoinDefinition.class, new JoinNodeExecutor().withTokenStore(tokenStore));
    nodeExecutors.put(TaskDefinition.class, new TaskNodeExecutor().withTokenStore(tokenStore));
  }

  FlowNodeDefinition<?> getNode(Identifier definitionId, Identifier nodeId) {
    FlowNodeDefinition<?> node = context.getDefinitionStore().findById(definitionId).getNode(nodeId);
    if (node == null) {
      throw new IllegalArgumentException(String.format("node for does not exist %s", nodeId));
    }
    return node;
  }

  protected <T extends FlowNodeDefinition> FlowNodeExecutor<T> getNodeExecutor(T nodeDefinition) {
    FlowNodeExecutor<T> nodeExecutor = nodeExecutors.get(nodeDefinition.getClass());
    if (nodeExecutor == null) {
      throw new IllegalArgumentException(String.format("no executor found for node definition %s", nodeDefinition));
    }
    return nodeExecutor;
  }

  @Override
  public void setContext(BrainslugContext context) {
    this.context = context;
  }

  @Override
  public Identifier startFlow(TriggerContext<?> trigger) {
    FlowNodeDefinition<?> startNode = getStartNodeDefinition(trigger.getDefinitionId(), trigger.getNodeId());

    Identifier instanceId = tokenStore.createInstance(trigger.getDefinitionId());
    tokenStore.addToken(instanceId, startNode.getId(), Option.<Identifier>empty());

    context.getPropertyStore().storeProperties(trigger.getInstanceId(), trigger.getProperties());
    context.trigger(new TriggerContext()
      .nodeId(startNode.getId())
      .definitionId(trigger.getDefinitionId())
      .instanceId(instanceId)
      .properties(trigger.getProperties()));

    return instanceId;
  }

  protected FlowNodeDefinition<?> getStartNodeDefinition(Identifier definitionId, Identifier nodeId) {
    return getNode(definitionId, nodeId);
  }

  @Override
  public void trigger(TriggerContext<?> triggerContext) {
    log.debug("triggering {}", triggerContext);

    FlowNodeDefinition node = getNode(triggerContext.getDefinitionId(), triggerContext.getNodeId());
    FlowNodeExecutor<FlowNodeDefinition> nodeExecutor = getNodeExecutor(node);

    ExecutionContext executionContext = createExecutionContext(triggerContext);

    context.getListenerManager().notifyListeners(EventType.BEFORE_EXECUTION, triggerContext);
    List<FlowNodeDefinition> next = nodeExecutor.execute(node, executionContext);
    context.getPropertyStore().storeProperties(triggerContext.getInstanceId(), triggerContext.getProperties());
    context.getListenerManager().notifyListeners(EventType.AFTER_EXECUTION, triggerContext);

    triggerNext(triggerContext, node, next);
  }


  // TODO: create ExecutionContextFactory, which contains merging
  protected ExecutionContext createExecutionContext(TriggerContext triggerContext) {
    DefaultExecutionContext executionContext = new DefaultExecutionContext(triggerContext, context);

    if(triggerContext.getInstanceId() != null) {
      executionContext.getTrigger().properties(mergeProperties(triggerContext, executionContext));
    }

    return executionContext;
  }

  protected ExecutionProperties mergeProperties(TriggerContext triggerContext, DefaultExecutionContext executionContext) {
    ExecutionProperties properties = context.getPropertyStore()
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

  protected void addToken(TriggerContext triggerContext, FlowNodeDefinition<?> node, FlowNodeDefinition nextNode) {
    if (triggerContext.getInstanceId() != null) {
      tokenStore.addToken(triggerContext.getInstanceId(), nextNode.getId(), Option.of(node.getId()));
    }
  }
}
