package brainslug.flow.context;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.definition.Identifier;
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
  public Collection<FlowDefinition> getDefinitions();

  /**
   * @return get a flow definition by id from the definition store
   */
  public FlowDefinition getDefinitionById(Identifier flowId);

  /**
   * trigger a node in a given instance and definition
   * for further exeution
   *
   * @param context the definition of the trigger environment
   */
  void trigger(TriggerContext context);


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
  Identifier startFlow(FlowDefinition flowDefinition, FlowProperties properties);

  /**
   * start an instance of the given flow definition
   *
   * @param flowDefinition the definition to be started
   * @param startNode the node in the definition to start at
   * @param properties the properties to be available during execution
   * @return id of the started flow instance
   */
  Identifier startFlow(FlowDefinition flowDefinition, FlowNodeDefinition startNode, FlowProperties properties);

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
  Identifier startFlow(Identifier definitionId, FlowProperties properties);

  /**
   * start the flow at the given startNodeId
   * @param definitionId the definition to be started
   * @param startNodeId the node in the definition to start at
   * @param properties the properties to be available during execution
   * @return id of the instance
   */
  Identifier startFlow(Identifier definitionId, Identifier startNodeId, FlowProperties properties);

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
