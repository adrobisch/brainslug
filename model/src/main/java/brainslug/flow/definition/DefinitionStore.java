package brainslug.flow.definition;

import java.util.Collection;

public interface DefinitionStore {
  void addDefinition(FlowDefinition flowDefinition);
  Collection<FlowDefinition> getDefinitions();
  FlowDefinition findById(Identifier id);
}
