package brainslug.flow.context;

import brainslug.flow.listener.DefaultListenerManager;
import brainslug.flow.listener.ListenerManager;
import brainslug.flow.listener.TriggerContext;
import brainslug.flow.execution.FlowExecutor;
import brainslug.flow.execution.TokenStore;
import brainslug.flow.execution.expression.DefaultPredicateEvaluator;
import brainslug.flow.execution.impl.HashMapTokenStore;
import brainslug.flow.execution.impl.TokenFlowExecutor;
import brainslug.flow.model.FlowDefinition;
import brainslug.flow.model.FlowDefinitions;
import brainslug.flow.model.Identifier;
import brainslug.util.UuidGenerator;

public class BrainslugContext {

  FlowDefinitions flowDefinitions = new FlowDefinitions();
  ListenerManager listenerManager;
  FlowExecutor flowExecutor;
  TokenStore tokenStore;
  PredicateEvaluator predicateEvaluator;
  IdGenerator idGenerator;

  Registry registry;

  public BrainslugContext() {
    withDispatcher(new DefaultListenerManager());
    withTokenStore(new HashMapTokenStore());
    withExecutor(new TokenFlowExecutor(tokenStore));
    withRegistry(new HashMapRegistry());
    withPredicateEvaluator(new DefaultPredicateEvaluator());
    withIdGenerator(new UuidGenerator());
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

  public BrainslugContext withDispatcher(ListenerManager listenerManager) {
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
    flowDefinitions.addDefinition(flowDefinition);
    return this;
  }

  public FlowDefinitions getFlowDefinitions() {
    return flowDefinitions;
  }

  public void trigger(TriggerContext context) {
    flowExecutor.trigger(context);
  }

  public Identifier startFlow(Identifier definitionId, Identifier nodeId) {
    return flowExecutor.startFlow(definitionId, nodeId);
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

  public IdGenerator getIdGenerator() {
    return idGenerator;
  }
}
