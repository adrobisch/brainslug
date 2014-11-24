package brainslug.flow.context;

import brainslug.flow.execution.*;
import brainslug.flow.execution.async.*;
import brainslug.flow.execution.expression.DefaultPredicateEvaluator;
import brainslug.flow.execution.expression.PredicateEvaluator;
import brainslug.flow.execution.token.HashMapTokenStore;
import brainslug.flow.execution.token.TokenFlowExecutor;
import brainslug.flow.execution.token.TokenStore;
import brainslug.flow.listener.DefaultListenerManager;
import brainslug.flow.listener.ListenerManager;
import brainslug.util.IdGenerator;
import brainslug.util.UuidGenerator;

public abstract class AbstractBrainslugContextBuilder<SelfType extends AbstractBrainslugContextBuilder, T extends BrainslugContext> {

  protected AsyncTriggerExecutor asyncTriggerExecutor = new AsyncTriggerExecutor();
  protected AsyncTriggerStore asyncTriggerStore = new ArrayListTriggerStore();
  protected AsyncTriggerSchedulerOptions asyncTriggerSchedulerOptions = new AsyncTriggerSchedulerOptions();

  protected AsyncFlowStartScheduler asyncFlowStartScheduler = new DefaultFlowStartScheduler();
  protected SchedulerOptions asyncFlowStartSchedulerOptions = new SchedulerOptions();

  protected DefinitionStore definitionStore = new HashMapDefinitionStore();
  protected ListenerManager listenerManager = new DefaultListenerManager();
  protected CallDefinitionExecutor callDefinitionExecutor = new CallDefinitionExecutor();
  protected PropertyStore propertyStore = new HashMapPropertyStore();
  protected PredicateEvaluator predicateEvaluator = new DefaultPredicateEvaluator();
  protected IdGenerator idGenerator = new UuidGenerator();
  protected Registry registry = new HashMapRegistry();

  protected FlowExecutor flowExecutor;
  protected TokenStore tokenStore;
  protected AsyncTriggerScheduler asyncTriggerScheduler;

  public T build() {
    if (tokenStore == null) {
      withTokenStore(new HashMapTokenStore(idGenerator));
    }

    if (asyncTriggerScheduler == null) {
      withAsyncTriggerScheduler(new ExecutorServiceAsyncTriggerScheduler()
        .withAsyncTriggerExecutor(asyncTriggerExecutor)
      );
    }

    if (flowExecutor == null) {
      withExecutor(new TokenFlowExecutor(
        tokenStore,
        definitionStore,
        propertyStore,
        listenerManager,
        registry,
        predicateEvaluator,
        asyncTriggerStore,
        asyncTriggerScheduler,
        callDefinitionExecutor
      ));
    }

    return internalBuild();
  }

  protected abstract T internalBuild();

  public SelfType self() {
    return (SelfType) this;
  }

  public SelfType withAsyncTriggerScheduler(AsyncTriggerScheduler asyncTriggerScheduler) {
    this.asyncTriggerScheduler = asyncTriggerScheduler;
    return self();
  }

  public SelfType withAsyncTriggerSchedulerOptions(AsyncTriggerSchedulerOptions asyncTriggerSchedulerOptions) {
    this.asyncTriggerSchedulerOptions = asyncTriggerSchedulerOptions;
    return self();
  }

  public SelfType withAsyncTriggerStore(AsyncTriggerStore asyncTriggerStore) {
    this.asyncTriggerStore = asyncTriggerStore;
    return self();
  }

  public SelfType withAsyncFlowStartScheduler(AsyncFlowStartScheduler asyncFlowStartScheduler) {
    this.asyncFlowStartScheduler = asyncFlowStartScheduler;
    return self();
  }

  public SelfType withAsyncFlowStartSchedulerOptions(SchedulerOptions asyncFlowStartSchedulerOptions) {
    this.asyncFlowStartSchedulerOptions = asyncFlowStartSchedulerOptions;
    return self();
  }

  public SelfType withPropertyStore(PropertyStore propertyStore) {
    this.propertyStore = propertyStore;
    return self();
  }

  public SelfType withDefinitionStore(DefinitionStore definitionStore) {
    this.definitionStore = definitionStore;
    return self();
  }

  public SelfType withTokenStore(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
    return self();
  }

  public SelfType withExecutor(FlowExecutor newFlowExecutor) {
    this.flowExecutor = newFlowExecutor;
    return self();
  }

  public SelfType withAsyncTriggerExecutor(AsyncTriggerExecutor asyncTriggerExecutor) {
    this.asyncTriggerExecutor = asyncTriggerExecutor;
    return self();
  }

  public SelfType withListenerManager(ListenerManager listenerManager) {
    this.listenerManager = listenerManager;
    return self();
  }

  public SelfType withRegistry(Registry registry) {
    this.registry = registry;
    return self();
  }

  public SelfType withPredicateEvaluator(PredicateEvaluator predicateEvaluator) {
    this.predicateEvaluator = predicateEvaluator;
    return self();
  }

  public SelfType withIdGenerator(UuidGenerator uuidGenerator) {
    this.idGenerator = uuidGenerator;
    return self();
  }

  public SelfType withCallDefinitionExecutor(CallDefinitionExecutor callDefinitionExecutor) {
    this.callDefinitionExecutor = callDefinitionExecutor;
    return self();
  }

}
