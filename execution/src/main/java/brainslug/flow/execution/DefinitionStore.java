package brainslug.flow.execution;

import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;

public interface DefinitionStore {
  public void addDefinition(FlowDefinition flowDefinition);
  public FlowDefinition findById(Identifier id);
}
