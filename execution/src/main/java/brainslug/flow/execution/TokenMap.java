package brainslug.flow.execution;

import brainslug.flow.model.Identifier;

import java.util.*;

/**
* @author andreas
*/
public class TokenMap {
  Map<Identifier, List<Token>> nodeTokens = Collections.synchronizedMap(new HashMap<Identifier, List<Token>>());

  public List<Token> get(Identifier nodeId) {
    return Collections.synchronizedList(getOrCreateTokenList(nodeId));
  }

  public void put(Identifier nodeId, Token token) {
    getOrCreateTokenList(nodeId).add(token);
  }

  private List<Token> getOrCreateTokenList(Identifier nodeId) {
    if (nodeTokens.get(nodeId) == null) {
      nodeTokens.put(nodeId, Collections.synchronizedList(new ArrayList<Token>()));
    }
    return nodeTokens.get(nodeId);
  }
}
