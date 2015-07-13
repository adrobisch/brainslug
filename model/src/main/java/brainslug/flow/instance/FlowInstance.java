package brainslug.flow.instance;

import brainslug.flow.definition.Identifier;

public interface FlowInstance {
  Identifier getIdentifier();
  Identifier getDefinitionId();
  FlowInstanceTokenList getTokens();
  FlowInstanceProperties getProperties();
}
