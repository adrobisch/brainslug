package brainslug.flow.context;

import brainslug.flow.event.EventDispatcher;
import brainslug.flow.event.FlowEvent;
import brainslug.flow.event.SynchronousEventDispatcher;
import brainslug.flow.execution.FlowExecutor;
import brainslug.flow.execution.expression.DefaultPredicateEvaluator;
import brainslug.flow.execution.impl.HashMapTokenStore;
import brainslug.flow.execution.impl.TokenFlowExecutor;
import brainslug.flow.model.FlowDefinition;
import brainslug.flow.model.FlowDefinitions;
import brainslug.flow.model.Identifier;
import brainslug.util.UuidGenerator;

public class BrainslugContext {
  FlowDefinitions flowDefinitions = new FlowDefinitions();
  EventDispatcher eventDispatcher;
  FlowExecutor flowExecutor;
  PredicateEvaluator predicateEvaluator;
  IdGenerator idGenerator;

  Registry registry;

  public BrainslugContext() {
    withDispatcher(new SynchronousEventDispatcher());
    withExecutor(new TokenFlowExecutor(new HashMapTokenStore()));
    withRegistry(new HashMapRegistry());
    withPredicateEvaluator(new DefaultPredicateEvaluator(this));
    withIdGenerator(new UuidGenerator());
  }

  public BrainslugContext withExecutor(FlowExecutor newFlowExecutor) {
    eventDispatcher.removeSubscriber(flowExecutor);
    setupFlowExecutor(newFlowExecutor);
    return this;
  }

  private void setupFlowExecutor(FlowExecutor newFlowExecutor) {
    this.flowExecutor = newFlowExecutor;
    flowExecutor.setContext(this);
    eventDispatcher.addSubscriber(newFlowExecutor);
  }

  public BrainslugContext withDispatcher(EventDispatcher eventDispatcher) {
    this.eventDispatcher = eventDispatcher;
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

  public void trigger(FlowEvent event) {
    eventDispatcher.push(event);
    eventDispatcher.dispatch();
  }

  public Identifier startFlow(Identifier definitionId, Identifier nodeId) {
    return flowExecutor.startFlow(definitionId, nodeId);
  }

  public EventDispatcher getEventDispatcher() {
    return eventDispatcher;
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
