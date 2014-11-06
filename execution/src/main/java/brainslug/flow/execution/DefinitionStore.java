package brainslug.flow.execution;

import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;

import java.util.Collection;

public interface DefinitionStore {
  public void addDefinition(FlowDefinition flowDefinition);
  public Collection<FlowDefinition> getDefinitions();
  public FlowDefinition findById(Identifier id);
}
