package brainslug.flow.execution.token;

import brainslug.flow.instance.FlowInstanceTokenList;
import brainslug.util.IdGenerator;
import brainslug.flow.definition.Identifier;
import brainslug.util.Option;

import java.util.*;

public class HashMapTokenStore implements TokenStore {

  Map<Identifier, List<Token>> instanceToTokenMap = new HashMap<Identifier, List<Token>>();

  IdGenerator idGenerator;

  public HashMapTokenStore(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  public List<Token> tokensForInstance(Identifier instanceId) {
    if (instanceToTokenMap.get(instanceId) == null) {
      List<Token> instanceTokens = new ArrayList<Token>();
      instanceToTokenMap.put(instanceId, instanceTokens);
      return instanceTokens;
    } else {
      return instanceToTokenMap.get(instanceId);
    }
  }

  @Override
  public FlowInstanceTokenList getInstanceTokens(Identifier instanceId) {
    return new TokenList(tokensForInstance(instanceId));
  }

  @Override
  public FlowInstanceTokenList getNodeTokens(Identifier nodeId, Identifier instanceId) {
    List<Token> nodeTokens = new ArrayList<Token>();
    for (final Iterator<Token> instanceTokens = tokensForInstance(instanceId).iterator(); instanceTokens.hasNext(); ) {
      Token token = instanceTokens.next();
      if(token.getNodeId().equals(nodeId) && !token.isDead()) {
        nodeTokens.add(token);
      }
    }
    return new TokenList(nodeTokens);
  }

  @Override
  public Token addToken(Identifier instanceId, Identifier nodeId, Option<Identifier> sourceNodeId, boolean isFinal) {
    Token token = new Token(idGenerator.generateId(),
      nodeId, sourceNodeId,
      Option.of(instanceId), false, isFinal);

    getInstanceTokens(instanceId).add(token);
    return token;
  }

  @Override
  public boolean removeToken(Identifier instanceId, Identifier tokenIdToDelete) {
    for (final Iterator<Token> instanceTokens = tokensForInstance(instanceId).iterator(); instanceTokens.hasNext(); ) {
      Token nextToken = instanceTokens.next();
      if (nextToken.getId().equals(tokenIdToDelete)) {
        nextToken.setDead(true);
        return true;
      }
    }
    return false;
  }
}
