package brainslug.flow.execution.token;

import brainslug.flow.definition.Identifier;
import brainslug.flow.instance.FlowInstanceToken;
import brainslug.util.Option;

public class TokenOperations {
  TokenStore tokenStore;

  public TokenOperations(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
  }

  public void removeTokens(Identifier instanceId, Identifier nodeId, Option<Identifier> sourceId, Integer quantity) {
    int deleted = 0;
    for (FlowInstanceToken token : tokenStore.getNodeTokens(nodeId,instanceId)) {
      if (token.getSourceNodeId().isPresent() && sourceId.isPresent() && !token.getSourceNodeId().get().equals(sourceId.get())) {
        continue;
      }
      removeToken(instanceId, token);
      if (++deleted >= quantity) {
        break;
      }
    }
  }

  public void removeToken(Identifier instanceId, FlowInstanceToken token) {
    if (token != null) {
      tokenStore.setDead(instanceId, token.getId());
    }
  }
}
