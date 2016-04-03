package brainslug.flow.execution.property.store;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.instance.FlowInstanceProperty;
import brainslug.flow.execution.instance.FlowInstanceProperties;
import brainslug.util.Option;

/**
 * A storage for flow instance properties.
 */
public interface PropertyStore {

  void setProperty(Identifier<?> instanceId, FlowInstanceProperty<?> property);

  void setProperties(Identifier<?> instanceId, FlowInstanceProperties<?, FlowInstanceProperty<?>> executionContext);

  Option<FlowInstanceProperty<?>> getProperty(Identifier<?> instanceId, Identifier<?> key);

  /**
   * load properties for an instance
   *
   * @param instanceId the instance to load the properties for
   * @return the properties of the specified instance id, if none exists an empty map ist returned
   */
  FlowInstanceProperties<?, FlowInstanceProperty<?>> getProperties(Identifier<?> instanceId);

}
