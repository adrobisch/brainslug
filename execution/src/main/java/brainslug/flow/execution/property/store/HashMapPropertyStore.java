package brainslug.flow.execution.property.store;

import brainslug.flow.definition.Identifier;
import brainslug.flow.context.ExecutionProperty;
import brainslug.flow.context.FlowProperties;
import brainslug.flow.execution.property.ExecutionProperties;
import brainslug.util.Option;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HashMapPropertyStore implements PropertyStore {

  Map<Identifier<?>, ExecutionProperties> propertiesByInstance = Collections.synchronizedMap(new HashMap<Identifier<?>, ExecutionProperties>());

  @Override
  public void setProperty(Identifier<?> instanceId, ExecutionProperty<?> property) {
    ExecutionProperties instanceProperties = propertiesByInstance.get(instanceId);
    if (instanceProperties != null) {
      instanceProperties.with(property.getKey(), property.getValue());
    }
  }

  @Override
  public void setProperties(Identifier<?> instanceId, FlowProperties<?, ExecutionProperty<?>> properties) {
    propertiesByInstance.put(instanceId, new ExecutionProperties().from(properties.getValues()));
  }

  @Override
  public Option<ExecutionProperty<?>> getProperty(Identifier<?> instanceId, Identifier<?> key) {
    ExecutionProperties instanceProperties = propertiesByInstance.get(instanceId);
    if (instanceProperties == null) {
      return Option.empty();
    } else {
      return Option.<ExecutionProperty<?>>of(instanceProperties.get(key.stringValue()));
    }
  }

  @Override
  public FlowProperties<?, ExecutionProperty<?>> getProperties(Identifier<?> instanceId) {
    return Option.of(propertiesByInstance.get(instanceId)).orElse(new ExecutionProperties());
  }
}
