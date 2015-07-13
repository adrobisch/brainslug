package brainslug.flow.execution.token;

import brainslug.flow.definition.Identifier;
import brainslug.flow.instance.FlowInstanceToken;
import brainslug.flow.instance.FlowInstanceTokenList;

import java.util.*;

public class TokenList implements FlowInstanceTokenList {

  List<FlowInstanceToken> tokenList = new ArrayList<FlowInstanceToken>();

  public TokenList() {
  }

  public TokenList(List<? extends FlowInstanceToken> tokenList) {
    this.tokenList = (List<FlowInstanceToken>) tokenList;
  }

  @Override
  public List<FlowInstanceToken> getActiveTokens() {
    return notDead(tokenList);
  }

  @Override
  public Map<Identifier, List<FlowInstanceToken>> groupedBySourceNode() {
    return sourceNodeMap(getActiveTokens());
  }

  protected Map<Identifier, List<FlowInstanceToken>> sourceNodeMap(List<FlowInstanceToken> instanceTokens) {
    Map<Identifier, List<FlowInstanceToken>> sourceNodeMap = new HashMap<Identifier, List<FlowInstanceToken>>();
    for (FlowInstanceToken token : instanceTokens) {
      if (token.getSourceNodeId().isPresent()) {
        getOrCreateTokenList(sourceNodeMap, token.getSourceNodeId().get())
          .add(token);
      }
    }
    return Collections.unmodifiableMap(sourceNodeMap);
  }

  protected List<FlowInstanceToken> getOrCreateTokenList(Map<Identifier, List<FlowInstanceToken>> sourceNodeMap, Identifier sourceNodeId) {
    if (sourceNodeMap.get(sourceNodeId) == null) {
      sourceNodeMap.put(sourceNodeId, new ArrayList<FlowInstanceToken>());
    }
    return sourceNodeMap.get(sourceNodeId);
  }

  @Override
  public Iterator<FlowInstanceToken> getIterator() {
    return getActiveTokens().iterator();
  }

  @Override
  public void add(FlowInstanceToken token) {
    tokenList.add(token);
  }

  List<FlowInstanceToken> notDead(List<? extends FlowInstanceToken> tokens) {
    List<FlowInstanceToken> activeTokens = new ArrayList<FlowInstanceToken>();
    for (FlowInstanceToken token : tokens) {
      if (!token.isDead()) {
        activeTokens.add(token);
      }
    }

    return activeTokens;
  }
}
