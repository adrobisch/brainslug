package brainslug.flow.execution.token;

import brainslug.flow.definition.Identifier;
import brainslug.flow.instance.FlowInstanceToken;

import java.util.List;
import java.util.Map;

public class TokenOperations {
  TokenStore tokenStore;

  public TokenOperations(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
  }

  public Map<Identifier, List<FlowInstanceToken>> getNodeTokensGroupedBySource(Identifier nodeId, Identifier instanceId) {
    return tokenStore.getNodeTokens(nodeId, instanceId).groupedBySourceNode();
  }

  public void removeFirstIncomingTokens(Identifier nodeId, Identifier instanceId) {
    Map<Identifier, List<FlowInstanceToken>> nodeTokens = getNodeTokensGroupedBySource(nodeId, instanceId);

    for (List<FlowInstanceToken> tokens : nodeTokens.values()) {
      removeToken(instanceId, first(tokens));
    }
  }

  protected FlowInstanceToken first(List<FlowInstanceToken> tokens) {
    if (tokens.isEmpty()) {
      return null;
    } else {
      return tokens.get(0);
    }
  }

  public void removeTokens(Identifier instanceId, List<FlowInstanceToken> tokens) {
    for (FlowInstanceToken token : tokens) {
      removeToken(instanceId, token);
    }
  }

  protected void removeToken(Identifier instanceId, FlowInstanceToken token) {
    if (token != null) {
      tokenStore.setDead(instanceId, token.getId());
    }
  }
}
