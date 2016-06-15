package brainslug.flow.context;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.instance.InstanceStore;
import brainslug.flow.execution.node.task.CallDefinitionExecutor;
import brainslug.flow.definition.DefinitionStore;
import brainslug.flow.execution.FlowExecutor;
import brainslug.flow.execution.async.*;
import brainslug.flow.execution.expression.ExpressionEvaluator;
import brainslug.flow.execution.token.TokenStore;
import brainslug.flow.execution.instance.FlowInstance;
import brainslug.flow.execution.instance.FlowInstanceProperties;
import brainslug.flow.execution.instance.FlowInstanceSelector;
import brainslug.flow.listener.ListenerManager;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.util.Preconditions;

import java.util.Collection;

public class DefaultBrainslugContext implements BrainslugContext {

  AsyncTriggerScheduler asyncTriggerScheduler;
  AsyncTriggerStore asyncTriggerStore;
  AsyncTriggerSchedulerOptions asyncTriggerSchedulerOptions;

  AsyncFlowStartScheduler asyncFlowStartScheduler;
  SchedulerOptions asyncFlowStartSchedulerOptions;

  DefinitionStore definitionStore;
  ListenerManager listenerManager;
  CallDefinitionExecutor callDefinitionExecutor;
  ExpressionEvaluator expressionEvaluator;
  Registry registry;

  FlowExecutor flowExecutor;
  TokenStore tokenStore;
  private final InstanceStore instanceStore;

  protected DefaultBrainslugContext(AsyncTriggerScheduler asyncTriggerScheduler,
                                 AsyncTriggerStore asyncTriggerStore,
                                 AsyncTriggerSchedulerOptions asyncTriggerSchedulerOptions,
                                 AsyncFlowStartScheduler asyncFlowStartScheduler,
                                 SchedulerOptions asyncFlowStartSchedulerOptions,
                                 DefinitionStore definitionStore,
                                 ListenerManager listenerManager,
                                 CallDefinitionExecutor callDefinitionExecutor,
                                 ExpressionEvaluator expressionEvaluator,
                                 Registry registry,
                                 FlowExecutor flowExecutor,
                                 TokenStore tokenStore,
                                 InstanceStore instanceStore) {
    this.asyncTriggerScheduler = asyncTriggerScheduler;
    this.asyncTriggerStore = asyncTriggerStore;
    this.asyncTriggerSchedulerOptions = asyncTriggerSchedulerOptions;
    this.asyncFlowStartScheduler = asyncFlowStartScheduler;
    this.asyncFlowStartSchedulerOptions = asyncFlowStartSchedulerOptions;
    this.definitionStore = definitionStore;
    this.listenerManager = listenerManager;
    this.callDefinitionExecutor = callDefinitionExecutor;
    this.expressionEvaluator = expressionEvaluator;
    this.registry = registry;
    this.flowExecutor = flowExecutor;
    this.tokenStore = tokenStore;
    this.instanceStore = instanceStore;
  }

  @Override
  public DefaultBrainslugContext addFlowDefinition(FlowDefinition flowDefinition) {
    definitionStore.addDefinition(flowDefinition);
    return this;
  }

  @Override
  public Collection<FlowDefinition> getDefinitions() {
    return definitionStore.getDefinitions();
  }

  @Override
  public FlowDefinition getDefinitionById(Identifier flowId) {
    return definitionStore.findById(flowId);
  }

  public DefinitionStore getDefinitionStore() {
    return definitionStore;
  }

  @Override
  public void trigger(TriggerContext context) {
    flowExecutor.trigger(context);
  }

  @Override
  public void signalEvent(Identifier eventId, Identifier instanceId, Identifier definitionId) {
    trigger(new Trigger()
            .nodeId(eventId)
            .instanceId(instanceId)
            .definitionId(definitionId)
            .signaling(true));
  }

  @Override
  public FlowInstance startFlow(FlowDefinition flowDefinition) {
    return startFlow(flowDefinition.getId());
  }

  @Override
  public FlowInstance startFlow(FlowDefinition flowDefinition, FlowInstanceProperties properties) {
    return startFlow(flowDefinition, flowDefinition.requireSingleStartNode(), properties);
  }

  @Override
  public FlowInstance startFlow(FlowDefinition flowDefinition, FlowNodeDefinition startNode, FlowInstanceProperties properties) {
    return startFlow(flowDefinition.getId(), startNode.getId(), properties);
  }

  @Override
  public FlowInstance startFlow(Identifier definitionId) {
    return startFlow(definitionId, getDefinitionById(definitionId).requireSingleStartNode().getId());
  }

  @Override
  public FlowInstance startFlow(Identifier definitionId, Identifier startNodeId) {
    return flowExecutor.startFlow(new Trigger().definitionId(definitionId).nodeId(startNodeId));
  }

  @Override
  public FlowInstance startFlow(Identifier definitionId, FlowInstanceProperties properties) {
    return startFlow(definitionId, getDefinitionById(definitionId).requireSingleStartNode().getId(), properties);
  }

  @Override
  public FlowInstance startFlow(Identifier definitionId, Identifier startNodeId, FlowInstanceProperties properties) {
    TriggerContext trigger = new Trigger()
      .definitionId(definitionId)
      .nodeId(startNodeId)
      .properties(properties);

    return flowExecutor.startFlow(trigger);
  }

  @Override
  public Collection<? extends FlowInstance> findInstances(FlowInstanceSelector instanceSelector) {
    return instanceStore.findInstances(instanceSelector);
  }

  @Override
  public BrainslugContext init() {
    Preconditions.notNull(asyncTriggerScheduler).start(this, asyncTriggerStore, asyncTriggerSchedulerOptions);
    Preconditions.notNull(asyncFlowStartScheduler).start(asyncFlowStartSchedulerOptions, this, getDefinitionStore().getDefinitions());
    return this;
  }

  @Override
  public BrainslugContext destroy() {
    Preconditions.notNull(asyncTriggerScheduler).stop();
    Preconditions.notNull(asyncFlowStartScheduler).stop();
    return this;
  }

  @Override
  public <T> BrainslugContext registerService(Class<T> serviceClass, T serviceInstance) {
    registry.registerService(serviceClass, serviceInstance);
    return this;
  }

  @Override
  public <T> T getService(Class<T> serviceClass) {
    return registry.getService(serviceClass);
  }

  public ListenerManager getListenerManager() {
    return listenerManager;
  }

  public Registry getRegistry() {
    return registry;
  }

  public ExpressionEvaluator getExpressionEvaluator() {
    return expressionEvaluator;
  }

  public FlowExecutor getFlowExecutor() {
    return flowExecutor;
  }

  public TokenStore getTokenStore() {
    return tokenStore;
  }

  public AsyncTriggerScheduler getAsyncTriggerScheduler() {
    return asyncTriggerScheduler;
  }



  public AsyncTriggerStore getAsyncTriggerStore() {
    return asyncTriggerStore;
  }

  public CallDefinitionExecutor getCallDefinitionExecutor() {
    return callDefinitionExecutor;
  }
}
