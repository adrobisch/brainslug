package brainslug.flow.execution.property;

import brainslug.flow.definition.Identifier;
import brainslug.flow.context.ExecutionProperty;
import brainslug.flow.context.FlowProperties;

/**
 * A storage for flow instance setProperties.
 */
public interface PropertyStore {
  public void storeProperties(Identifier<?> instanceId, FlowProperties<ExecutionProperty> executionContext);

  /**
   * load setProperties for an instance
   *
   * @param instanceId the instance to load the setProperties for
   * @return the setProperties of the specified instance id, if none exists an empty map ist returned
   */
  public FlowProperties loadProperties(Identifier<?> instanceId);
}
