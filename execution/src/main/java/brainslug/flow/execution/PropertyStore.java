package brainslug.flow.execution;

import brainslug.flow.Identifier;
import brainslug.flow.context.ExecutionProperties;

/**
 * A storage for flow instance setProperties.
 */
public interface PropertyStore {
  public void storeProperties(Identifier<?> instanceId, ExecutionProperties executionContext);

  /**
   * load setProperties for an instance
   *
   * @param instanceId the instance to load the setProperties for
   * @return the setProperties of the specified instance id, if none exists an empty map ist returned
   */
  ExecutionProperties loadProperties(Identifier<?> instanceId);
}
