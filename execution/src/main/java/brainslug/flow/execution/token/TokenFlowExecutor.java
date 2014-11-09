package brainslug.flow.execution.token;

import brainslug.flow.Identifier;
import brainslug.flow.context.*;
import brainslug.flow.execution.*;
import brainslug.flow.execution.async.AsyncTriggerScheduler;
import brainslug.flow.execution.async.AsyncTriggerStore;
import brainslug.flow.execution.expression.PredicateEvaluator;
import brainslug.flow.listener.EventType;
import brainslug.flow.listener.ListenerManager;
import brainslug.flow.node.*;
import brainslug.util.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TokenFlowExecutor implements FlowExecutor {

  private TokenOperations tokenOperations;

  private Logger log = LoggerFactory.getLogger(TokenFlowExecutor.class);

  protected TokenStore tokenStore;
  protected DefinitionStore definitionStore;
  protected PropertyStore propertyStore;
  protected ListenerManager listenerManager;
  protected Registry registry;
  protected PredicateEvaluator predicateEvaluator;
  protected AsyncTriggerStore asyncTriggerStore;
  protected AsyncTriggerScheduler asyncTriggerScheduler;
  protected CallDefinitionExecutor callDefinitionExecutor;

  Map<Class<? extends FlowNodeDefinition>, FlowNodeExecutor> nodeExecutors = new HashMap<Class<? extends FlowNodeDefinition>, FlowNodeExecutor>();


  public TokenFlowExecutor(TokenStore tokenStore,
                           DefinitionStore definitionStore,
                           PropertyStore propertyStore,
                           ListenerManager listenerManager,
                           Registry registry,
                           PredicateEvaluator predicateEvaluator,
                           AsyncTriggerStore asyncTriggerStore,
                           AsyncTriggerScheduler asyncTriggerScheduler,
                           CallDefinitionExecutor callDefinitionExecutor) {
    this.tokenStore = tokenStore;
    this.definitionStore = definitionStore;
    this.propertyStore = propertyStore;
    this.listenerManager = listenerManager;
    this.registry = registry;
    this.predicateEvaluator = predicateEvaluator;
    this.asyncTriggerStore = asyncTriggerStore;
    this.asyncTriggerScheduler = asyncTriggerScheduler;
    this.callDefinitionExecutor = callDefinitionExecutor;

    this.tokenOperations = new TokenOperations(tokenStore);

    addNodeExecutorMappings();
  }

  protected void addNodeExecutorMappings() {
    nodeExecutors.put(EventDefinition.class, new EventNodeExecutor(asyncTriggerStore, predicateEvaluator).withTokenOperations(tokenOperations));
    nodeExecutors.put(ParallelDefinition.class, new DefaultNodeExecutor<DefaultNodeExecutor, ParallelDefinition>().withTokenOperations(tokenOperations));
    nodeExecutors.put(MergeDefinition.class, new DefaultNodeExecutor<DefaultNodeExecutor, MergeDefinition>().withTokenOperations(tokenOperations));
    nodeExecutors.put(ChoiceDefinition.class, new ChoiceNodeExecutor(predicateEvaluator).withTokenOperations(tokenOperations));
    nodeExecutors.put(JoinDefinition.class, new JoinNodeExecutor().withTokenOperations(tokenOperations));
    nodeExecutors.put(TaskDefinition.class, createTaskNodeExecutor());
  }

  protected TaskNodeExecutor createTaskNodeExecutor() {
    return new TaskNodeExecutor(definitionStore, predicateEvaluator, callDefinitionExecutor, asyncTriggerScheduler).withTokenOperations(tokenOperations);
  }

  FlowNodeDefinition<?> getNode(Identifier definitionId, Identifier nodeId) {
    FlowNodeDefinition<?> node = definitionStore.findById(definitionId).getNode(nodeId);
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
  public Identifier startFlow(TriggerContext trigger) {
    FlowNodeDefinition<?> startNode = getStartNodeDefinition(trigger.getDefinitionId(), trigger.getNodeId());

    Identifier instanceId = tokenStore.createInstance(trigger.getDefinitionId());
    tokenStore.addToken(instanceId, startNode.getId(), Option.<Identifier>empty());

    propertyStore.storeProperties(trigger.getInstanceId(), trigger.getProperties());

    trigger(new Trigger()
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
  public void trigger(TriggerContext trigger) {
    log.debug("triggering {}", trigger);

    FlowNodeDefinition node = getNode(trigger.getDefinitionId(), trigger.getNodeId());
    FlowNodeExecutor<FlowNodeDefinition> nodeExecutor = getNodeExecutor(node);

    ExecutionContext executionContext = createExecutionContext(trigger);

    listenerManager.notifyListeners(EventType.BEFORE_EXECUTION, trigger);

    FlowNodeExecutionResult executionResult = nodeExecutor.execute(node, executionContext);
    propertyStore.storeProperties(trigger.getInstanceId(), trigger.getProperties());

    listenerManager.notifyListeners(EventType.AFTER_EXECUTION, trigger);

    triggerNext(trigger, node, executionResult);
  }

  // TODO: create ExecutionContextFactory, which contains merging
  protected ExecutionContext createExecutionContext(TriggerContext trigger) {
    DefaultExecutionContext executionContext = new DefaultExecutionContext(trigger, registry);

    if(trigger.getInstanceId() != null) {
      executionContext.getTrigger().setProperties(mergeProperties(trigger, executionContext));
    }

    return executionContext;
  }

  protected ExecutionProperties mergeProperties(TriggerContext trigger, ExecutionContext executionContext) {
    ExecutionProperties properties = propertyStore
        .loadProperties(executionContext.getTrigger().getInstanceId());

    properties.putAll(trigger.getProperties());
    return properties;
  }

  protected void triggerNext(TriggerContext trigger, FlowNodeDefinition<?> node, FlowNodeExecutionResult flowNodeExecutionResult) {
    for (FlowNodeDefinition nextNode : flowNodeExecutionResult.getNextNodes()) {
      addToken(trigger, node, nextNode);
      trigger(createTriggerContextForNextNode(trigger, nextNode));
    }
  }

  protected Trigger createTriggerContextForNextNode(TriggerContext event, FlowNodeDefinition nextNode) {
    return new Trigger()
          .nodeId(nextNode.getId())
          .definitionId(event.getDefinitionId())
          .instanceId(event.getInstanceId())
          .properties(event.getProperties());
  }

  protected void addToken(TriggerContext trigger, FlowNodeDefinition<?> node, FlowNodeDefinition nextNode) {
    if (trigger.getInstanceId() != null) {
      tokenStore.addToken(trigger.getInstanceId(), nextNode.getId(), Option.of(node.getId()));
    }
  }

  public void setTokenStore(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
    this.tokenOperations = new TokenOperations(tokenStore);
  }

  public void setDefinitionStore(DefinitionStore definitionStore) {
    this.definitionStore = definitionStore;
  }

  public void setPropertyStore(PropertyStore propertyStore) {
    this.propertyStore = propertyStore;
  }

  public void setListenerManager(ListenerManager listenerManager) {
    this.listenerManager = listenerManager;
  }

  public void setRegistry(Registry registry) {
    this.registry = registry;
  }

  public void setPredicateEvaluator(PredicateEvaluator predicateEvaluator) {
    this.predicateEvaluator = predicateEvaluator;
  }

  public void setAsyncTriggerStore(AsyncTriggerStore asyncTriggerStore) {
    this.asyncTriggerStore = asyncTriggerStore;
  }

  public void setAsyncTriggerScheduler(AsyncTriggerScheduler asyncTriggerScheduler) {
    this.asyncTriggerScheduler = asyncTriggerScheduler;
  }

  public void setCallDefinitionExecutor(CallDefinitionExecutor callDefinitionExecutor) {
    this.callDefinitionExecutor = callDefinitionExecutor;
  }
}
