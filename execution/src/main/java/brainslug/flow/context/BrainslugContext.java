package brainslug.flow.context;

import brainslug.flow.execution.*;
import brainslug.flow.execution.impl.ExecutorServiceScheduler;
import brainslug.flow.execution.impl.HashMapPropertyStore;
import brainslug.flow.listener.DefaultListenerManager;
import brainslug.flow.listener.ListenerManager;
import brainslug.flow.execution.expression.DefaultPredicateEvaluator;
import brainslug.flow.execution.impl.HashMapTokenStore;
import brainslug.flow.execution.impl.TokenFlowExecutor;
import brainslug.flow.model.DefinitionStore;
import brainslug.flow.model.FlowDefinition;
import brainslug.flow.model.Identifier;
import brainslug.util.UuidGenerator;

public class BrainslugContext {

  AsyncTaskScheduler asyncTaskScheduler;
  DefinitionStore definitionStore;
  ListenerManager listenerManager;
  FlowExecutor flowExecutor;
  TokenStore tokenStore;
  PropertyStore propertyStore;
  PredicateEvaluator predicateEvaluator;
  IdGenerator idGenerator;

  Registry registry;

  public BrainslugContext() {
    withAsyncTaskScheduler(new ExecutorServiceScheduler());
    withDefinitionStore(new DefinitionStore());
    withListenerManager(new DefaultListenerManager());
    withTokenStore(new HashMapTokenStore());
    withExecutor(new TokenFlowExecutor(this));
    withRegistry(new HashMapRegistry());
    withPredicateEvaluator(new DefaultPredicateEvaluator());
    withIdGenerator(new UuidGenerator());
    withPropertyStore(new HashMapPropertyStore());
  }

  public BrainslugContext withAsyncTaskScheduler(AsyncTaskScheduler asyncTaskScheduler) {
    this.asyncTaskScheduler = asyncTaskScheduler;
    asyncTaskScheduler.setContext(this);
    return this;
  }

  public BrainslugContext withPropertyStore(HashMapPropertyStore propertyStore) {
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
    flowExecutor.setContext(this);
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

  public Identifier startFlow(Identifier definitionId, Identifier startNodeId, ExecutionProperties properties) {
    TriggerContext triggerContext = new TriggerContext()
        .definitionId(definitionId)
        .nodeId(startNodeId)
        .properties(properties);

    return flowExecutor.startFlow(triggerContext);
  }

  public Identifier startFlow(Identifier definitionId, Identifier startNodeId) {
    return flowExecutor.startFlow(new TriggerContext().definitionId(definitionId).nodeId(startNodeId));
  }

  public Identifier startFlow(TriggerContext context) {
    return flowExecutor.startFlow(context);
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

  public AsyncTaskScheduler getAsyncTaskScheduler() {
    return asyncTaskScheduler;
  }

  public IdGenerator getIdGenerator() {
    return idGenerator;
  }
}
