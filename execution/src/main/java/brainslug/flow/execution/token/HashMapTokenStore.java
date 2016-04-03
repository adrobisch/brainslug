package brainslug.flow.execution.token;

import brainslug.flow.execution.instance.FlowInstanceTokenList;
import brainslug.util.IdGenerator;
import brainslug.flow.definition.Identifier;
import brainslug.util.Option;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapTokenStore implements TokenStore {

  Map<Identifier, Map<Identifier, Token>> instanceToTokenMap = new ConcurrentHashMap<Identifier, Map<Identifier, Token>>();

  IdGenerator idGenerator;

  public HashMapTokenStore(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  public List<Token> tokensForInstance(Identifier instanceId) {
    return new ArrayList<Token>(getOrCreateInstanceTokenMap(instanceId).values());
  }

  Map<Identifier, Token> getOrCreateInstanceTokenMap(Identifier instanceId)  {
    if (instanceToTokenMap.get(instanceId) == null) {
      Map<Identifier, Token> instanceTokens = new ConcurrentHashMap<Identifier, Token>();
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
    for (Token instanceToken : tokensForInstance(instanceId)) {
      if(instanceToken.getNodeId().equals(nodeId) && !instanceToken.isDead()) {
        nodeTokens.add(instanceToken);
      }
    }
    return new TokenList(nodeTokens);
  }

  @Override
  public Token addToken(Identifier instanceId, Identifier nodeId, Option<Identifier> sourceNodeId, boolean isFinal) {
    Token token = new Token(idGenerator.generateId(), nodeId, sourceNodeId, instanceId, false, isFinal);
    getOrCreateInstanceTokenMap(instanceId).put(token.getId(), token);
    return token;
  }

  @Override
  public boolean setDead(Identifier instanceId, Identifier tokenIdToDelete) {
    Option<Token> token = findToken(instanceId, tokenIdToDelete);
    if (token.isPresent()) {
      token.get().setDead(true);
      return true;
    }
    return false;
  }

  private Option<Token> findToken(Identifier instanceId, Identifier tokenIdToDelete) {
    for (final Iterator<Token> instanceTokens = tokensForInstance(instanceId).iterator(); instanceTokens.hasNext(); ) {
      Token nextToken = instanceTokens.next();
      if (nextToken.getId().equals(tokenIdToDelete)) {
        return Option.of(nextToken);
      }
    }
    return Option.empty();
  }

  @Override
  public boolean setFinal(Identifier instanceId, Identifier tokenId) {
    Option<Token> token = findToken(instanceId, tokenId);
    if (token.isPresent()) {
      token.get().setFinal(true);
      return true;
    }
    return false;
  }
}
