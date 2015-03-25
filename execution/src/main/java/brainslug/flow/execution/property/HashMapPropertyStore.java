package brainslug.flow.execution.property;

import brainslug.flow.definition.Identifier;
import brainslug.flow.context.ExecutionProperty;
import brainslug.flow.context.FlowProperties;
import brainslug.util.Option;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HashMapPropertyStore implements PropertyStore {

  Map<Identifier<?>, FlowProperties> propertiesByInstance = Collections.synchronizedMap(new HashMap<Identifier<?>, FlowProperties>());

  @Override
  public void storeProperties(Identifier<?> instanceId, FlowProperties<ExecutionProperty> properties) {
    propertiesByInstance.put(instanceId, properties);
  }

  @Override
  public FlowProperties loadProperties(Identifier<?> instanceId) {
    return Option.of(propertiesByInstance.get(instanceId)).orElse(new ExecutionProperties());
  }
}
