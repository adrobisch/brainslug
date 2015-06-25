package brainslug.flow.execution.property.store;

import brainslug.flow.definition.Identifier;
import brainslug.flow.context.ExecutionProperty;
import brainslug.flow.context.FlowProperties;

/**
 * A storage for flow instance properties.
 */
public interface PropertyStore {
  public void storeProperties(Identifier<?> instanceId, FlowProperties<ExecutionProperty> executionContext);

  /**
   * load properties for an instance
   *
   * @param instanceId the instance to load the properties for
   * @return the properties of the specified instance id, if none exists an empty map ist returned
   */
  public FlowProperties loadProperties(Identifier<?> instanceId);
}
