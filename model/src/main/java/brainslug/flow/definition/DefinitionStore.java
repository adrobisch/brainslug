package brainslug.flow.definition;

import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.definition.Identifier;

import java.util.Collection;

public interface DefinitionStore {
  public void addDefinition(FlowDefinition flowDefinition);
  public Collection<FlowDefinition> getDefinitions();
  public FlowDefinition findById(Identifier id);
}
