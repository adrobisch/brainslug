package brainslug.flow.execution;

import brainslug.flow.model.Identifier;
import brainslug.util.Option;

import java.util.List;
import java.util.Map;

public interface TokenStore {
  List<Token> getInstanceTokens(Identifier instanceId);
  Map<Identifier, List<Token>> tokensGroupedBySourceNode(Identifier nodeId, Identifier instanceId);
  Token addToken(Identifier instanceId, Identifier nodeId, Option<Identifier> sourceNodeId);
  void removeToken(Identifier instanceId, Identifier tokenId);
  Identifier createInstance(Identifier definitionId);
}
