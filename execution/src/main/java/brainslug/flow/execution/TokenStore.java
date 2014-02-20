package brainslug.flow.execution;

import brainslug.flow.event.Subscriber;
import brainslug.flow.model.Identifier;

import java.util.List;
import java.util.Map;

public interface TokenStore extends Subscriber {
  Map<Identifier, List<Token>> getTokens(Identifier nodeId, Identifier instanceId);
  void addToken(Identifier instanceId, Identifier nodeId, Token token);
  void removeToken(Identifier instanceId, Identifier nodeId, Identifier sourceNodeId);
  void createInstance(Identifier instanceId);
}
