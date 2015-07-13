package brainslug.flow.context;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.definition.Identifier;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.instance.FlowInstanceProperties;
import brainslug.flow.instance.FlowInstanceSelector;
import brainslug.flow.node.FlowNodeDefinition;

import java.util.Collection;

/**
 * The BrainslugContext is the main configuration and interface
 * for the execution of flow definitions.
 */
public interface BrainslugContext {
  /**
   * add a flow definition to the definition store to make it
   * executable
   *
   * @param flowDefinition the flow definition to add
   * @return context with added store
   */
  BrainslugContext addFlowDefinition(FlowDefinition flowDefinition);

  /**
   * @return the stored flow definitions from the definition store
   */
  Collection<FlowDefinition> getDefinitions();

  /**
   * @return get a flow definition by id from the definition store
   */
  FlowDefinition getDefinitionById(Identifier flowId);

  /**
   * trigger a node in a given instance and definition
   * for further exeution
   *
   * @param context the definition of the trigger environment
   */
  void trigger(TriggerContext context);

  /**
   * send a signaling trigger to the specified event
   *
   * @param eventId id of the event node
   * @param instanceId id of the instance containing this event node
   * @param definitionId id of the flow definition where the event is defined
   */
  void signalEvent(Identifier eventId, Identifier instanceId, Identifier definitionId);

  /**
   * start an instance with the start node definition
   * there must only be one definied
   *
   * @param flowDefinition the definition to be started
   * @return id of the started flow instance
   * @throws java.lang.IllegalStateException if more the one start node definition exists
   */
  Identifier startFlow(FlowDefinition flowDefinition);

  /**
   * start an instance with the start node definition
   * there must only be one definied
   *
   * @param flowDefinition the definition to be started
   * @param properties the properties to be available during execution
   * @return id of the started flow instance
   * @throws java.lang.IllegalStateException if more the one start node definition exists
   */
  Identifier startFlow(FlowDefinition flowDefinition, FlowInstanceProperties properties);

  /**
   * start an instance of the given flow definition
   *
   * @param flowDefinition the definition to be started
   * @param startNode the node in the definition to start at
   * @param properties the properties to be available during execution
   * @return id of the started flow instance
   */
  Identifier startFlow(FlowDefinition flowDefinition, FlowNodeDefinition startNode, FlowInstanceProperties properties);

  /**
   * start the flow at the given startNodeId
   * @param definitionId the definition to be started
   * @return id of the instance
   */
  Identifier startFlow(Identifier definitionId);

  /**
   * start the flow at the given startNodeId
   * @param definitionId the definition to be started
   * @param startNodeId the node in the definition to start at
   * @return id of the instance
   */
  Identifier startFlow(Identifier definitionId, Identifier startNodeId);

  /**
   * start the flow at the given startNodeId
   * @param definitionId the definition to be started
   * @param properties the properties to be available during execution
   * @return id of the instance
   */
  Identifier startFlow(Identifier definitionId, FlowInstanceProperties properties);

  /**
   * start the flow at the given startNodeId
   * @param definitionId the definition to be started
   * @param startNodeId the node in the definition to start at
   * @param properties the properties to be available during execution
   * @return id of the instance
   */
  Identifier startFlow(Identifier definitionId, Identifier startNodeId, FlowInstanceProperties properties);

  /**
   * find the flow instance matching the given instance selector
   *
   * @param instanceSelector
   * @return a optional flow instance
   */
  Collection<? extends FlowInstance> findInstances(FlowInstanceSelector instanceSelector);

  /**
   *
   * initialize context
   *
   * this will start the schedulers (if enabled)
   *
   * @return initialized BrainslugContext with schedulers started
   */
  BrainslugContext init();

  /**
   *
   * initialize context
   *
   * this will stop the schedulers (if enabled)
   *
   * @return initialized BrainslugContext with schedulers stopped
   */
  BrainslugContext destroy();

  <T> BrainslugContext registerService(Class<T> serviceClass, T serviceInstance);

  <T> T getService(Class<T> clazz);
}
