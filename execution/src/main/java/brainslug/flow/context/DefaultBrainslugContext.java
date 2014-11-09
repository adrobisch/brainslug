package brainslug.flow.context;

import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import brainslug.flow.execution.CallDefinitionExecutor;
import brainslug.flow.execution.DefinitionStore;
import brainslug.flow.execution.FlowExecutor;
import brainslug.flow.execution.async.*;
import brainslug.flow.execution.expression.PredicateEvaluator;
import brainslug.flow.execution.token.TokenStore;
import brainslug.flow.listener.ListenerManager;
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
  PredicateEvaluator predicateEvaluator;
  Registry registry;

  FlowExecutor flowExecutor;
  TokenStore tokenStore;

  public DefaultBrainslugContext(AsyncTriggerScheduler asyncTriggerScheduler,
                                 AsyncTriggerStore asyncTriggerStore,
                                 AsyncTriggerSchedulerOptions asyncTriggerSchedulerOptions,
                                 AsyncFlowStartScheduler asyncFlowStartScheduler,
                                 SchedulerOptions asyncFlowStartSchedulerOptions,
                                 DefinitionStore definitionStore,
                                 ListenerManager listenerManager,
                                 CallDefinitionExecutor callDefinitionExecutor,
                                 PredicateEvaluator predicateEvaluator,
                                 Registry registry,
                                 FlowExecutor flowExecutor,
                                 TokenStore tokenStore) {
    this.asyncTriggerScheduler = asyncTriggerScheduler;
    this.asyncTriggerStore = asyncTriggerStore;
    this.asyncTriggerSchedulerOptions = asyncTriggerSchedulerOptions;
    this.asyncFlowStartScheduler = asyncFlowStartScheduler;
    this.asyncFlowStartSchedulerOptions = asyncFlowStartSchedulerOptions;
    this.definitionStore = definitionStore;
    this.listenerManager = listenerManager;
    this.callDefinitionExecutor = callDefinitionExecutor;
    this.predicateEvaluator = predicateEvaluator;
    this.registry = registry;
    this.flowExecutor = flowExecutor;
    this.tokenStore = tokenStore;
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

  public DefinitionStore getDefinitionStore() {
    return definitionStore;
  }

  @Override
  public void trigger(TriggerContext context) {
    flowExecutor.trigger(context);
  }

  @Override
  public Identifier startFlow(Identifier definitionId, Identifier startNodeId) {
    return flowExecutor.startFlow(new Trigger().definitionId(definitionId).nodeId(startNodeId));
  }

  @Override
  public Identifier startFlow(Identifier definitionId, Identifier startNodeId, ExecutionProperties properties) {
    TriggerContext trigger = new Trigger()
      .definitionId(definitionId)
      .nodeId(startNodeId)
      .properties(properties);

    return flowExecutor.startFlow(trigger);
  }

  @Override
  public BrainslugContext start() {
    Preconditions.notNull(asyncTriggerScheduler).start(this, asyncTriggerSchedulerOptions);
    Preconditions.notNull(asyncFlowStartScheduler).start(asyncFlowStartSchedulerOptions, this, getDefinitionStore().getDefinitions());
    return this;
  }

  @Override
  public BrainslugContext stop() {
    Preconditions.notNull(asyncTriggerScheduler).stop();
    Preconditions.notNull(asyncFlowStartScheduler).stop();
    return this;
  }

  public ListenerManager getListenerManager() {
    return listenerManager;
  }

  public Registry getRegistry() {
    return registry;
  }

  public PredicateEvaluator getPredicateEvaluator() {
    return predicateEvaluator;
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
