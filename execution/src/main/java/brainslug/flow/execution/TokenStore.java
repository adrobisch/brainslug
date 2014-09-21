package brainslug.flow.execution;

import brainslug.flow.model.Identifier;
import brainslug.util.Option;

import java.util.List;
import java.util.Map;

public interface TokenStore {
  Map<Identifier, List<Token>> tokensGroupedBySource(Identifier nodeId, Identifier instanceId);
  void addToken(Identifier instanceId, Identifier nodeId, Option<Identifier> sourceNodeId);
  void removeToken(Identifier tokenId);
  Identifier createInstance();
}
