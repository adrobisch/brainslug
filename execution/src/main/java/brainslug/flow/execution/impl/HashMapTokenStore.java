package brainslug.flow.execution.impl;

import brainslug.flow.execution.Token;
import brainslug.flow.execution.TokenMap;
import brainslug.flow.execution.TokenStore;
import brainslug.flow.model.Identifier;

import java.util.*;

public class HashMapTokenStore implements TokenStore {

  Map<Identifier, TokenMap> instanceTokenMaps = Collections.synchronizedMap(new HashMap<Identifier, TokenMap>());

  @Override
  public Map<Identifier, List<Token>> tokensGroupedBySource(Identifier nodeId, Identifier instanceId) {
    List<Token> instanceTokens = requireTokenMap(instanceId).get(nodeId);
    return instanceTokens == null ? Collections.<Identifier, List<Token>>emptyMap() : sourceNodeMap(instanceTokens);
  }

  Map<Identifier, List<Token>> sourceNodeMap(List<Token> instanceTokens) {
    Map<Identifier, List<Token>> sourceNodeMap = new HashMap<Identifier, List<Token>>();
    for (Token token : instanceTokens) {
      if (sourceNodeMap.get(token.getSourceNode()) == null) {
        sourceNodeMap.put(token.getSourceNode(), new ArrayList<Token>());
      }
      sourceNodeMap.get(token.getSourceNode()).add(token);
    }
    return Collections.unmodifiableMap(sourceNodeMap);
  }

  @Override
  public void addToken(Identifier instanceId, Identifier nodeId, Token token) {
    requireTokenMap(instanceId).get(nodeId).add(token);
  }

  @Override
  public void removeToken(Identifier instanceId, Identifier nodeId, Token token) {
    Map<Identifier, List<Token>> sourceNodeMap = sourceNodeMap(requireTokenMap(instanceId).get(nodeId));
    List<Token> nodeTokens = sourceNodeMap.get(token.getSourceNode());
    if (nodeTokens != null && nodeTokens.size() > 0) {
      Token firstTokenFromSource = sourceNodeMap.get(token.getSourceNode()).get(0);
      requireTokenMap(instanceId).get(nodeId).remove(firstTokenFromSource);
    }
  }

  @Override
  public void createInstance(Identifier instanceId) {
    if (instanceTokenMaps.get(instanceId) == null) {
      instanceTokenMaps.put(instanceId, new TokenMap());
    }
  }

  private TokenMap requireTokenMap(Identifier instanceId) {
    if (instanceTokenMaps.get(instanceId) == null) {
      throw new IllegalArgumentException("instanceId " + instanceId + " does not exist");
    }
    return instanceTokenMaps.get(instanceId);
  }

}
