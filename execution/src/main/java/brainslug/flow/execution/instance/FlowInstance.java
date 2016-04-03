package brainslug.flow.execution.instance;

import brainslug.flow.definition.Identifier;

public interface FlowInstance {
  Identifier getIdentifier();
  Identifier getDefinitionId();
  FlowInstanceTokenList getTokens();
  FlowInstanceProperties<?, FlowInstanceProperty<?>> getProperties();
}
