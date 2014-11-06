package brainslug.flow.context;

import brainslug.flow.execution.*;
import brainslug.flow.execution.async.*;
import brainslug.flow.execution.expression.PredicateEvaluator;
import brainslug.flow.execution.HashMapPropertyStore;
import brainslug.flow.execution.token.TokenStore;
import brainslug.flow.listener.DefaultListenerManager;
import brainslug.flow.listener.ListenerManager;
import brainslug.flow.execution.expression.DefaultPredicateEvaluator;
import brainslug.flow.execution.token.HashMapTokenStore;
import brainslug.flow.execution.token.TokenFlowExecutor;
import brainslug.flow.execution.DefinitionStore;
import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import brainslug.util.IdGenerator;
import brainslug.util.Preconditions;
import brainslug.util.UuidGenerator;

public class BrainslugContext {

  AsyncTriggerScheduler asyncTriggerScheduler;
  AsyncTriggerStore asyncTriggerStore;
  AsyncTriggerSchedulerOptions asyncTriggerSchedulerOptions;

  AsyncFlowStartScheduler asyncFlowStartScheduler;
  SchedulerOptions asyncFlowStartSchedulerOptions;

  DefinitionStore definitionStore;
  ListenerManager listenerManager;
  FlowExecutor flowExecutor;
  TokenStore tokenStore;
  PropertyStore propertyStore;
  PredicateEvaluator predicateEvaluator;
  IdGenerator idGenerator;

  Registry registry;

  public BrainslugContext() {
    initialize();
  }

  protected void initialize() {
    withIdGenerator(new UuidGenerator())
      .withTokenStore(new HashMapTokenStore(idGenerator));

    withPropertyStore(new HashMapPropertyStore());
    withDefinitionStore(new HashMapDefinitionStore());

    withAsyncTriggerStore(new ArrayListTriggerStore())
      .withAsyncTriggerSchedulerOptions(new AsyncTriggerSchedulerOptions())
      .withAsyncTriggerScheduler(new ExecutorServiceAsyncTriggerScheduler());

    withAsyncFlowStartSchedulerOptions(new SchedulerOptions())
      .withAsyncFlowStartScheduler(new DefaultFlowStartScheduler());

    withListenerManager(new DefaultListenerManager());
    withExecutor(new TokenFlowExecutor(this));
    withRegistry(new HashMapRegistry());
    withPredicateEvaluator(new DefaultPredicateEvaluator());
  }

  public BrainslugContext withAsyncTriggerScheduler(AsyncTriggerScheduler asyncTriggerScheduler) {
    this.asyncTriggerScheduler = asyncTriggerScheduler;
    return this;
  }

  public BrainslugContext withAsyncTriggerSchedulerOptions(AsyncTriggerSchedulerOptions asyncTriggerSchedulerOptions) {
    this.asyncTriggerSchedulerOptions = asyncTriggerSchedulerOptions;
    return this;
  }

  public BrainslugContext withAsyncTriggerStore(AsyncTriggerStore asyncTriggerStore) {
    this.asyncTriggerStore = asyncTriggerStore;
    return this;
  }

  public BrainslugContext withAsyncFlowStartScheduler(AsyncFlowStartScheduler asyncFlowStartScheduler) {
    this.asyncFlowStartScheduler = asyncFlowStartScheduler;
    return this;
  }

  public BrainslugContext withAsyncFlowStartSchedulerOptions(SchedulerOptions asyncFlowStartSchedulerOptions) {
    this.asyncFlowStartSchedulerOptions = asyncFlowStartSchedulerOptions;
    return this;
  }

  public BrainslugContext withPropertyStore(PropertyStore propertyStore) {
    this.propertyStore = propertyStore;
    return this;
  }

  public BrainslugContext withDefinitionStore(DefinitionStore definitionStore) {
    this.definitionStore = definitionStore;
    return this;
  }

  public BrainslugContext withTokenStore(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
    return this;
  }

  public BrainslugContext withExecutor(FlowExecutor newFlowExecutor) {
    setupFlowExecutor(newFlowExecutor);
    return this;
  }

  private void setupFlowExecutor(FlowExecutor newFlowExecutor) {
    this.flowExecutor = newFlowExecutor;
  }

  public BrainslugContext withListenerManager(ListenerManager listenerManager) {
    this.listenerManager = listenerManager;
    return this;
  }

  public BrainslugContext withRegistry(Registry registry) {
    this.registry = registry;
    return this;
  }

  public BrainslugContext withPredicateEvaluator(PredicateEvaluator predicateEvaluator) {
    this.predicateEvaluator = predicateEvaluator;
    return this;
  }

  public BrainslugContext withIdGenerator(UuidGenerator uuidGenerator) {
    this.idGenerator = uuidGenerator;
    return this;
  }

  public BrainslugContext addFlowDefinition(FlowDefinition flowDefinition) {
    definitionStore.addDefinition(flowDefinition);
    return this;
  }

  public DefinitionStore getDefinitionStore() {
    return definitionStore;
  }

  public void trigger(TriggerContext context) {
    flowExecutor.trigger(context);
  }

  public Identifier startFlow(Identifier definitionId, Identifier startNodeId) {
    return flowExecutor.startFlow(new TriggerContext().definitionId(definitionId).nodeId(startNodeId));
  }

  public Identifier startFlow(Identifier definitionId, Identifier startNodeId, ExecutionProperties properties) {
    TriggerContext triggerContext = new TriggerContext()
      .definitionId(definitionId)
      .nodeId(startNodeId)
      .properties(properties);

    return flowExecutor.startFlow(triggerContext);
  }

  public BrainslugContext start() {
    Preconditions.notNull(asyncTriggerScheduler).start(this, asyncTriggerSchedulerOptions);
    Preconditions.notNull(asyncFlowStartScheduler).start(asyncFlowStartSchedulerOptions, this, getDefinitionStore());
    return this;
  }

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

  public PropertyStore getPropertyStore() {
    return propertyStore;
  }

  public AsyncTriggerScheduler getAsyncTriggerScheduler() {
    return asyncTriggerScheduler;
  }

  public IdGenerator getIdGenerator() {
    return idGenerator;
  }

  public AsyncTriggerStore getAsyncTriggerStore() {
    return asyncTriggerStore;
  }
}
