package brainslug.flow.execution.token;

import brainslug.flow.definition.DefinitionStore;
import brainslug.flow.definition.Identifier;
import brainslug.flow.context.*;
import brainslug.flow.execution.*;
import brainslug.flow.execution.async.AsyncTriggerScheduler;
import brainslug.flow.execution.async.AsyncTriggerStore;
import brainslug.flow.execution.expression.ExpressionEvaluator;
import brainslug.flow.execution.instance.InstanceSelector;
import brainslug.flow.execution.instance.InstanceStore;
import brainslug.flow.execution.node.*;
import brainslug.flow.execution.node.task.CallDefinitionExecutor;
import brainslug.flow.execution.node.task.ScriptExecutor;
import brainslug.flow.execution.property.store.PropertyStore;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.instance.FlowInstanceProperty;
import brainslug.flow.listener.EventType;
import brainslug.flow.listener.ListenerManager;
import brainslug.flow.node.*;
import brainslug.util.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TokenFlowExecutor implements FlowExecutor {

  private final ExecutionContextFactory executionContextFactory;
  private TokenOperations tokenOperations;

  private Logger log = LoggerFactory.getLogger(TokenFlowExecutor.class);

  protected TokenStore tokenStore;
  protected InstanceStore instanceStore;
  protected DefinitionStore definitionStore;
  protected PropertyStore propertyStore;
  protected ListenerManager listenerManager;
  protected Registry registry;
  protected ExpressionEvaluator expressionEvaluator;
  protected AsyncTriggerStore asyncTriggerStore;
  protected AsyncTriggerScheduler asyncTriggerScheduler;
  protected CallDefinitionExecutor callDefinitionExecutor;
  protected ScriptExecutor scriptExecutor;

  Map<Class<? extends FlowNodeDefinition>, FlowNodeExecutor> nodeExecutors = new HashMap<Class<? extends FlowNodeDefinition>, FlowNodeExecutor>();


  public TokenFlowExecutor(TokenStore tokenStore,
                           InstanceStore instanceStore,
                           DefinitionStore definitionStore,
                           PropertyStore propertyStore,
                           ListenerManager listenerManager,
                           Registry registry,
                           ExpressionEvaluator expressionEvaluator,
                           AsyncTriggerStore asyncTriggerStore,
                           AsyncTriggerScheduler asyncTriggerScheduler,
                           CallDefinitionExecutor callDefinitionExecutor,
                           ScriptExecutor scriptExecutor) {
    this.tokenStore = tokenStore;
    this.instanceStore = instanceStore;
    this.definitionStore = definitionStore;
    this.propertyStore = propertyStore;
    this.listenerManager = listenerManager;
    this.registry = registry;
    this.expressionEvaluator = expressionEvaluator;
    this.asyncTriggerStore = asyncTriggerStore;
    this.asyncTriggerScheduler = asyncTriggerScheduler;
    this.callDefinitionExecutor = callDefinitionExecutor;
    this.scriptExecutor = scriptExecutor;

    this.tokenOperations = new TokenOperations(tokenStore);
    this.executionContextFactory = new ExecutionContextFactory(propertyStore, registry);

    addNodeExecutorMappings();
  }

  protected void addNodeExecutorMappings() {
    nodeExecutors.put(EventDefinition.class, new EventNodeExecutor(asyncTriggerStore, expressionEvaluator));
    nodeExecutors.put(ParallelDefinition.class, new DefaultNodeExecutor<ParallelDefinition>());
    nodeExecutors.put(MergeDefinition.class, new DefaultNodeExecutor<MergeDefinition>());
    nodeExecutors.put(ChoiceDefinition.class, new ChoiceNodeExecutor(expressionEvaluator));
    nodeExecutors.put(JoinDefinition.class, new JoinNodeExecutor());
    nodeExecutors.put(TaskDefinition.class, createTaskNodeExecutor());
  }

  protected TaskNodeExecutor createTaskNodeExecutor() {
    return new TaskNodeExecutor(definitionStore, expressionEvaluator, callDefinitionExecutor, asyncTriggerScheduler, scriptExecutor);
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
  public FlowInstance startFlow(TriggerContext trigger) {
    FlowNodeDefinition<?> startNode = getStartNodeDefinition(trigger.getDefinitionId(), trigger.getNodeId());

    FlowInstance instance = instanceStore.createInstance(trigger.getDefinitionId());
    tokenStore.addToken(instance.getIdentifier(), startNode.getId(), Option.<Identifier>empty(), false);

    storeProperties(trigger, instance.getIdentifier());

    trigger(new Trigger()
            .nodeId(startNode.getId())
            .definitionId(trigger.getDefinitionId())
            .instanceId(instance.getIdentifier())
            .properties(trigger.getProperties()));

    // get a fresh copy of the instance after triggering
    return instanceStore.findInstance(new InstanceSelector().withInstanceId(instance.getIdentifier())).get();
  }

  protected void storeProperties(TriggerContext trigger, Identifier instanceId) {
    for (FlowInstanceProperty<?> property : trigger.getProperties().values()) {
      if (!property.isTransient()) {
        propertyStore.setProperty(instanceId, property);
      }
    }
  }

  protected FlowNodeDefinition<?> getStartNodeDefinition(Identifier definitionId, Identifier nodeId) {
    return getNode(definitionId, nodeId);
  }

  @Override
  public void trigger(TriggerContext trigger) {
    log.debug("triggering {}", trigger);

    if (trigger.getInstanceId() == null) {
      throw new IllegalArgumentException("instance not specified for trigger");
    }

    FlowNodeDefinition node = getNode(trigger.getDefinitionId(), trigger.getNodeId());

    FlowInstance flowInstance = instanceStore
            .findInstance(new InstanceSelector().withInstanceId(trigger.getInstanceId()))
            .get();

    FlowNodeExecutor<FlowNodeDefinition> nodeExecutor = getNodeExecutor(node);

    ExecutionContext executionContext = executionContextFactory.createExecutionContext(flowInstance, trigger);

    listenerManager.notifyListeners(EventType.BEFORE_EXECUTION, trigger);

    FlowNodeExecutionResult executionResult = nodeExecutor.execute(node, executionContext);
    storeProperties(trigger, trigger.getInstanceId());

    listenerManager.notifyListeners(EventType.AFTER_EXECUTION, trigger);

    removeTokens(trigger, executionResult);
    triggerNext(trigger, node, executionResult);
  }

  private void removeTokens(TriggerContext trigger, FlowNodeExecutionResult executionResult) {
    for (FlowNodeExecutionResult.TokenRemoval removedToken : executionResult.getRemovedTokens()) {
      tokenOperations.removeTokens(trigger.getInstanceId(), removedToken.getNodeId(), removedToken.getSourceId(), removedToken.getQuantity());
    }
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
    tokenStore.addToken(trigger.getInstanceId(), nextNode.getId(), Option.of(node.getId()), isFinalToken(nextNode));
  }

  private boolean isFinalToken(FlowNodeDefinition nextNode) {
    return nextNode.getOutgoing().isEmpty();
  }

  public void setTokenStore(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
  }

  public void setTokenOperations(TokenOperations tokenOperations) {
    this.tokenOperations = tokenOperations;
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

  public void setExpressionEvaluator(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
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
