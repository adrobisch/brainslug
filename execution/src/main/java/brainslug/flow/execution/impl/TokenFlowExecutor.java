package brainslug.flow.execution.impl;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.event.FlowEvent;
import brainslug.flow.event.TriggerEvent;
import brainslug.flow.execution.*;
import brainslug.flow.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenFlowExecutor implements FlowExecutor {

  protected BrainslugContext context;
  protected TokenStore tokenStore;

  Map<Class<? extends FlowNodeDefinition>, FlowNodeExectuor> nodeExecutors = new HashMap<Class<? extends FlowNodeDefinition>, FlowNodeExectuor>();
  Map<Class<? extends FlowEvent>, EventHandler> eventHandlers = new HashMap<Class<? extends FlowEvent>, EventHandler>();

  protected EventHandler<TriggerEvent> triggerNodeHandler = new EventHandler<TriggerEvent>() {
    @Override
    public void handle(TriggerEvent triggerEvent) {
      FlowNodeDefinition node = getNode(triggerEvent.getDefinitionId(), triggerEvent.getNodeId());
      FlowNodeExectuor<FlowNodeDefinition> nodeExecutor = getNodeExecutor(node);

      DefaultExecutionContext executionContext = new DefaultExecutionContext(triggerEvent, context);

      nodeExecutor.preExecute(executionContext);
      List<FlowNodeDefinition> next = nodeExecutor.execute(node, executionContext);
      nodeExecutor.postExecute(executionContext);

      triggerNext(triggerEvent, node, next);
    }

  };

  protected void triggerNext(TriggerEvent event, FlowNodeDefinition<?> node, List<FlowNodeDefinition> next) {
    for (FlowNodeDefinition nextNode : next) {

      if (event.getInstanceId() != null) {
        // TODO: AddToken should be a event which is processed by a token store related listener
        tokenStore.addToken(event.getInstanceId(), nextNode.getId(), new Token(node.getId()));
      }

      FlowEvent triggerEvent = new TriggerEvent()
        .nodeId(nextNode.getId())
        .sourceNodeId(node.getId())
        .definitionId(event.getDefinitionId())
        .instanceId(event.getInstanceId());

      context.getEventDispatcher().push(triggerEvent);
    }
  }

  public TokenFlowExecutor(TokenStore tokenStore) {
    this.tokenStore = tokenStore;

    addNodeExecutorMappings();
    addEventHandlers();
  }

  private void addEventHandlers() {
    eventHandlers.put(TriggerEvent.class, triggerNodeHandler);
  }

  void addNodeExecutorMappings() {
    nodeExecutors.put(EventDefinition.class, new DefaultNodeExecutor());
    nodeExecutors.put(ParallelDefinition.class, new DefaultNodeExecutor());
    nodeExecutors.put(MergeDefinition.class, new DefaultNodeExecutor());
    nodeExecutors.put(ChoiceDefinition.class, new ChoiceNodeExecutor());
    nodeExecutors.put(JoinDefinition.class, new JoinNodeExecutor());
    nodeExecutors.put(TaskDefinition.class, new TaskNodeExecutor());
  }

  @Override
  public void notify(FlowEvent event) {
    eventHandlers.get(event.getClass()).handle(event);
  }

  FlowNodeDefinition<?> getNode(Identifier definitionId, Identifier nodeId) {
    FlowNodeDefinition<?> node = context.getFlowDefinitions().findById(definitionId).getNode(nodeId);
    if (node == null) {
      throw new IllegalArgumentException(String.format("node for does not exist %s", nodeId));
    }
    return node;
  }

  <T extends FlowNodeDefinition> FlowNodeExectuor<T> getNodeExecutor(T nodeDefinition) {
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
    FlowNodeDefinition<?> node = getNode(definitionId, nodeId);
    Identifier instanceId = context.getIdGenerator().generateId();

    tokenStore.createInstance(instanceId);
    tokenStore.addToken(instanceId, nodeId, new Token(nodeId));

    context.getEventDispatcher().push(new TriggerEvent().sourceNodeId(node.getId()).nodeId(node.getId()).definitionId(definitionId).instanceId(instanceId));
    context.getEventDispatcher().dispatch();

    return instanceId;
  }

  interface EventHandler<T extends FlowEvent> {
    public void handle(T event);
  }
}
