package brainslug.flow.execution;

import brainslug.flow.model.Identifier;

import java.util.Map;

/**
 * A storage for execution context properties.
 */
public interface PropertyStore {
  public void storeProperties(Identifier<?> instanceId, Map<Object, Object> executionContext);
  Map<Object,Object> loadProperties(Identifier<?> instanceId);
}
