package brainslug.flow.execution.token;

import brainslug.flow.Identifier;

import java.util.*;

public class TokenList {

  List<Token> tokenList;

  public TokenList(List<Token> tokenList) {
    this.tokenList = tokenList;
  }

  public List<Token> getTokens() {
    return tokenList;
  }

  public Map<Identifier, List<Token>> groupedBySourceNode() {
    return sourceNodeMap(tokenList);
  }

  protected Map<Identifier, List<Token>> sourceNodeMap(List<Token> instanceTokens) {
    Map<Identifier, List<Token>> sourceNodeMap = new HashMap<Identifier, List<Token>>();
    for (Token token : instanceTokens) {
      if (token.getSourceNode().isPresent()) {
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

  public Iterator<Token> getIterator() {
    return tokenList.iterator();
  }

  public void add(Token token) {
    tokenList.add(token);
  }
}
