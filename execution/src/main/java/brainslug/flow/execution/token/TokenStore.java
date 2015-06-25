package brainslug.flow.execution.token;

import brainslug.flow.definition.Identifier;
import brainslug.util.Option;

public interface TokenStore {
  TokenList getInstanceTokens(Identifier instanceId);
  TokenList getNodeTokens(Identifier nodeId, Identifier instanceId);
  Token addToken(Identifier instanceId, Identifier nodeId, Option<Identifier> sourceNodeId);
  boolean removeToken(Identifier instanceId, Identifier tokenId);
}
