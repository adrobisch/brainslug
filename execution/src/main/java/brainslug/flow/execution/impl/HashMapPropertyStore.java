package brainslug.flow.execution.impl;

import brainslug.flow.execution.ExecutionProperties;
import brainslug.flow.execution.PropertyStore;
import brainslug.flow.model.Identifier;
import brainslug.util.Option;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HashMapPropertyStore implements PropertyStore {

  Map<Identifier<?>, ExecutionProperties> propertiesByInstance = Collections.synchronizedMap(new HashMap<Identifier<?>, ExecutionProperties>());

  @Override
  public void storeProperties(Identifier<?> instanceId, ExecutionProperties properties) {
    propertiesByInstance.put(instanceId, properties);
  }

  @Override
  public ExecutionProperties loadProperties(Identifier<?> instanceId) {
    return Option.of(propertiesByInstance.get(instanceId)).orElse(new ExecutionProperties());
  }
}
