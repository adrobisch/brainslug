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
  public List<FlowInstanceToken> getNodeTokens(Identifier nodeId) {
    List<FlowInstanceToken> nodeTokens = new ArrayList<FlowInstanceToken>();
    for (FlowInstanceToken token : getActiveTokens()) {
      if (token.getNodeId().equals(nodeId)) {
        nodeTokens.add(token);
      }
    }
    return Collections.unmodifiableList(nodeTokens);
  }

  @Override
  public Iterator<FlowInstanceToken> iterator() {
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
