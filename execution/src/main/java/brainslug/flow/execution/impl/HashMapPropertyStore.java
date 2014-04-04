package brainslug.flow.execution.impl;

import brainslug.flow.execution.PropertyStore;
import brainslug.flow.model.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HashMapPropertyStore implements PropertyStore {

  Map<Identifier, Map<Object, Object>> propertiesByInstance = Collections.synchronizedMap(new HashMap<Identifier, Map<Object, Object>>());

  @Override
  public void storeProperties(Identifier<?> instanceId, Map<Object, Object> properties) {
    propertiesByInstance.put(instanceId, properties);
  }

  @Override
  public Map<Object, Object> loadProperties(Identifier<?> instanceId) {
    Map<Object, Object> instanceProperties = propertiesByInstance.get(instanceId);
    return instanceProperties == null ? Collections.emptyMap() : instanceProperties;
  }
}
