package brainslug.flow.execution;

import brainslug.flow.model.Identifier;

/**
 * A storage for flow instance properties.
 */
public interface PropertyStore {
  public void storeProperties(Identifier<?> instanceId, ExecutionProperties executionContext);

  /**
   * load properties for an instance
   *
   * @param instanceId the instance to load the properties for
   * @return the properties of the specified instance id, if none exists an empty map ist returned
   */
  ExecutionProperties loadProperties(Identifier<?> instanceId);
}
