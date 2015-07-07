package brainslug.flow.context;

import brainslug.flow.definition.DefinitionStore;
import brainslug.flow.definition.HashMapDefinitionStore;
import brainslug.flow.execution.*;
import brainslug.flow.execution.async.*;
import brainslug.flow.execution.expression.DefaultExpressionEvaluator;
import brainslug.flow.execution.expression.ExpressionEvaluator;
import brainslug.flow.execution.instance.HashMapInstanceStore;
import brainslug.flow.execution.instance.InstanceStore;
import brainslug.flow.execution.node.task.CallDefinitionExecutor;
import brainslug.flow.execution.property.store.HashMapPropertyStore;
import brainslug.flow.execution.property.store.PropertyStore;
import brainslug.flow.execution.token.HashMapTokenStore;
import brainslug.flow.execution.token.TokenFlowExecutor;
import brainslug.flow.execution.token.TokenStore;
import brainslug.flow.listener.DefaultListenerManager;
import brainslug.flow.listener.ListenerManager;
import brainslug.util.IdGenerator;
import brainslug.util.UuidGenerator;

public abstract class AbstractBrainslugContextBuilder<SelfType extends AbstractBrainslugContextBuilder, T extends BrainslugContext> {

  protected AsyncTriggerExecutor asyncTriggerExecutor = new AsyncTriggerExecutor();
  protected AsyncTriggerSchedulerOptions asyncTriggerSchedulerOptions = new AsyncTriggerSchedulerOptions();

  protected AsyncFlowStartScheduler asyncFlowStartScheduler = new ExecutorServiceFlowStartScheduler();
  protected SchedulerOptions asyncFlowStartSchedulerOptions = new SchedulerOptions();

  protected DefinitionStore definitionStore = new HashMapDefinitionStore();
  protected ListenerManager listenerManager = new DefaultListenerManager();
  protected CallDefinitionExecutor callDefinitionExecutor = new CallDefinitionExecutor();
  protected ExpressionEvaluator expressionEvaluator = new DefaultExpressionEvaluator();
  protected Registry registry = new HashMapRegistry();

  protected IdGenerator idGenerator;
  protected FlowExecutor flowExecutor;
  protected TokenStore tokenStore;
  protected InstanceStore instanceStore;
  protected AsyncTriggerStore asyncTriggerStore;
  protected PropertyStore propertyStore;
  protected AsyncTriggerScheduler asyncTriggerScheduler;

  public T build() {
    if (idGenerator == null) {
      withIdGenerator(new UuidGenerator());
    }

    if (asyncTriggerStore == null) {
      withAsyncTriggerStore(new ArrayListTriggerStore());
    }

    if (propertyStore == null) {
      withPropertyStore(new HashMapPropertyStore());
    }

    if (tokenStore == null) {
      withTokenStore(new HashMapTokenStore(idGenerator));
    }

    if (instanceStore == null) {
      withInstanceStore(new HashMapInstanceStore(idGenerator, propertyStore));
    }

    if (asyncTriggerScheduler == null) {
      withAsyncTriggerScheduler(new ExecutorServiceAsyncTriggerScheduler()
        .withAsyncTriggerExecutor(asyncTriggerExecutor)
      );
    }

    if (flowExecutor == null) {
      withFlowExecutor(new TokenFlowExecutor(
        tokenStore,
        instanceStore,
        definitionStore,
        propertyStore,
        listenerManager,
        registry,
        expressionEvaluator,
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

  public SelfType withFlowExecutor(FlowExecutor newFlowExecutor) {
    this.flowExecutor = newFlowExecutor;
    return self();
  }

  public SelfType withAsyncTriggerExecutor(AsyncTriggerExecutor asyncTriggerExecutor) {
    this.asyncTriggerExecutor = asyncTriggerExecutor;
    return self();
  }

  public SelfType withInstanceStore(InstanceStore instanceStore) {
    this.instanceStore = instanceStore;
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

  public SelfType withPredicateEvaluator(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
    return self();
  }

  public SelfType withIdGenerator(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
    return self();
  }

  public SelfType withCallDefinitionExecutor(CallDefinitionExecutor callDefinitionExecutor) {
    this.callDefinitionExecutor = callDefinitionExecutor;
    return self();
  }

}
