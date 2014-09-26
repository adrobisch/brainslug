package brainslug.flow.execution;

import brainslug.flow.model.Identifier;
import brainslug.util.Option;

public interface TokenStore {
  TokenList getInstanceTokens(Identifier instanceId);
  public TokenList getNodeTokens(Identifier nodeId, Identifier instanceId);
  Token addToken(Identifier instanceId, Identifier nodeId, Option<Identifier> sourceNodeId);
  void removeToken(Identifier instanceId, Identifier tokenId);
  Identifier createInstance(Identifier definitionId);
}
