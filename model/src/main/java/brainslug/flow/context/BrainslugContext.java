package brainslug.flow.context;

import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;

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
   * start the flow at the given startNodeId
   * @param definitionId the definition to be started
   * @param startNodeId the node in the definition to start at
   * @return id of the instance
   */
  Identifier startFlow(Identifier definitionId, Identifier startNodeId);

  /**
   * start the flow at the given startNodeId
   * @param definitionId the definition to be started
   * @param startNodeId the node in the definition to start at
   * @param properties the properties to be available during execution
   * @return id of the instance
   */
  Identifier startFlow(Identifier definitionId, Identifier startNodeId, ExecutionProperties properties);

  /**
   * start the async schedulers (if enabled)
   *
   * @return BrainslugContext with schedulers started
   */
  BrainslugContext start();

  /**
   * stop the async schedulers (if enabled)
   *
   * @return BrainslugContext with schedulers stopped
   */
  BrainslugContext stop();

  <T> BrainslugContext registerService(Class<T> serviceClass, T serviceInstance);

  <T> T getService(Class<T> clazz);
}
