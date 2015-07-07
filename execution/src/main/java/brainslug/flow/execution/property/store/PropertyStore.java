package brainslug.flow.execution.property.store;

import brainslug.flow.definition.Identifier;
import brainslug.flow.context.ExecutionProperty;
import brainslug.flow.context.FlowProperties;
import brainslug.util.Option;

/**
 * A storage for flow instance properties.
 */
public interface PropertyStore {

  void setProperty(Identifier<?> instanceId, ExecutionProperty<?> property);

  void setProperties(Identifier<?> instanceId, FlowProperties<?, ExecutionProperty<?>> executionContext);

  Option<ExecutionProperty<?>> getProperty(Identifier<?> instanceId, Identifier<?> key);

  /**
   * load properties for an instance
   *
   * @param instanceId the instance to load the properties for
   * @return the properties of the specified instance id, if none exists an empty map ist returned
   */
  FlowProperties<?, ExecutionProperty<?>> getProperties(Identifier<?> instanceId);

}
