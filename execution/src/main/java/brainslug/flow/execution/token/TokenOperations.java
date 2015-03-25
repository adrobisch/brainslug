package brainslug.flow.execution.token;

import brainslug.flow.definition.Identifier;

import java.util.List;
import java.util.Map;

public class TokenOperations {
  TokenStore tokenStore;

  public TokenOperations(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
  }

  public Map<Identifier, List<Token>> getNodeTokensGroupedBySource(Identifier nodeId, Identifier instanceId) {
    return tokenStore.getNodeTokens(nodeId, instanceId).groupedBySourceNode();
  }

  public void removeFirstIncomingTokens(Identifier nodeId, Identifier instanceId) {
    Map<Identifier, List<Token>> nodeTokens = getNodeTokensGroupedBySource(nodeId, instanceId);

    for (List<Token> tokens : nodeTokens.values()) {
      removeToken(instanceId, first(tokens));
    }
  }

  protected Token first(List<Token> tokens) {
    if (tokens.isEmpty()) {
      return null;
    } else {
      return tokens.get(0);
    }
  }

  public void removeTokens(Identifier instanceId, List<Token> tokens) {
    for (Token token : tokens) {
      removeToken(instanceId, token);
    }
  }

  protected void removeToken(Identifier instanceId, Token token) {
    if (token != null) {
      tokenStore.removeToken(instanceId, token.getId());
    }
  }
}
