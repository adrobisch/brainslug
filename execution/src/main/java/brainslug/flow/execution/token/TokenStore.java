package brainslug.flow.execution.token;

import brainslug.flow.definition.Identifier;
import brainslug.flow.instance.FlowInstanceToken;
import brainslug.flow.instance.FlowInstanceTokenList;
import brainslug.util.Option;

public interface TokenStore {
  FlowInstanceTokenList getInstanceTokens(Identifier instanceId);
  FlowInstanceTokenList getNodeTokens(Identifier nodeId, Identifier instanceId);
  FlowInstanceToken addToken(Identifier instanceId, Identifier nodeId, Option<Identifier> sourceNodeId, boolean isFinal);
  boolean removeToken(Identifier instanceId, Identifier tokenId);
}
