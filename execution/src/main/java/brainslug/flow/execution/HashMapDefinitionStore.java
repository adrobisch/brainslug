package brainslug.flow.execution;

import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HashMapDefinitionStore implements DefinitionStore {
  Map<Identifier, FlowDefinition> flowDefinitions = new HashMap<Identifier, FlowDefinition>();

  public void addDefinition(FlowDefinition flowDefinition) {
    flowDefinitions.put(flowDefinition.getId(), flowDefinition);
  }

  @Override
  public Collection<FlowDefinition> getDefinitions() {
    return flowDefinitions.values();
  }

  public FlowDefinition findById(Identifier id) {
    LoggerFactory.getLogger(DefinitionStore.class).trace("flowDefinitions: " + flowDefinitions);

    if (flowDefinitions.get(id) == null) {
      throw new IllegalArgumentException(String.format("no flow with id %s found", id));
    }

    return flowDefinitions.get(id);
  }
}
