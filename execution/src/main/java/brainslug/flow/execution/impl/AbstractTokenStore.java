package brainslug.flow.execution.impl;

import brainslug.flow.execution.Token;
import brainslug.flow.execution.TokenStore;
import brainslug.flow.model.Identifier;

import java.util.*;

public abstract class AbstractTokenStore implements TokenStore {

  protected Map<Identifier, List<Token>> sourceNodeMap(Identifier nodeId, List<Token> instanceTokens) {
    Map<Identifier, List<Token>> sourceNodeMap = new HashMap<Identifier, List<Token>>();
    for (Token token : instanceTokens) {
      if (nodeId.equals(token.getNodeId()) && token.getSourceNode().isPresent()) {
        getOrCreateTokenList(sourceNodeMap, token.getSourceNode().get())
          .add(token);
      }
    }
    return Collections.unmodifiableMap(sourceNodeMap);
  }

  protected List<Token> getOrCreateTokenList(Map<Identifier, List<Token>> sourceNodeMap, Identifier sourceNodeId) {
    if (sourceNodeMap.get(sourceNodeId) == null) {
      sourceNodeMap.put(sourceNodeId, new ArrayList<Token>());
    }
    return sourceNodeMap.get(sourceNodeId);
  }

}
