package brainslug.jdbc;

import brainslug.flow.context.IdGenerator;
import brainslug.flow.execution.Token;
import brainslug.flow.execution.TokenStore;
import brainslug.flow.model.Identifier;
import brainslug.jdbc.tables.QFlowInstance;
import brainslug.jdbc.tables.QFlowToken;
import brainslug.util.Option;
import com.mysema.query.types.ConstructorExpression;

import java.util.*;

public class JdbcTokenStore implements TokenStore {

  private final Database database;
  private final IdGenerator idGenerator;

  public JdbcTokenStore(Database database, IdGenerator idGenerator) {
    this.database = database;
    this.idGenerator = idGenerator;
  }

  @Override
  public Map<Identifier, List<Token>> tokensGroupedBySource(Identifier nodeId, Identifier instanceId) {
    List<Token> nodeTokens = database.query().from(QFlowToken.flowToken)
      .where(
        QFlowToken.flowToken.flowInstanceId.eq(instanceId.stringValue()),
        QFlowToken.flowToken.currentNode.eq(nodeId.stringValue())
      ).list(ConstructorExpression.create(Token.class, QFlowToken.flowToken.id, QFlowToken.flowToken.sourceNode));

    return createSourceNodeTokenMap(nodeTokens);
  }

  private Map<Identifier, List<Token>> createSourceNodeTokenMap(List<Token> nodeTokens) {
    Map<Identifier, List<Token>> sourceNodeToTokenMap = new HashMap<Identifier, List<Token>>();
    for (Token token : nodeTokens) {
      getOrCreateTokenList(sourceNodeToTokenMap, token.getSourceNode()).add(token);
    }
    return sourceNodeToTokenMap;
  }

  private List<Token> getOrCreateTokenList(Map<Identifier, List<Token>> sourceNodeToTokenMap, Option<Identifier> sourceNodeKey) {
    if (!sourceNodeKey.isPresent()) {
      return new ArrayList<Token>();
    }

    if (sourceNodeToTokenMap.get(sourceNodeKey.get()) == null) {
      sourceNodeToTokenMap.put(sourceNodeKey.get(), new ArrayList<Token>());
    }
    return sourceNodeToTokenMap.get(sourceNodeKey.get());
  }

  public void addToken(Identifier instanceId, Identifier nodeId, Option<Identifier> sourceNodeId) {
    database.insert(QFlowToken.flowToken)
      .columns(QFlowToken.flowToken.id, QFlowToken.flowToken.currentNode, QFlowToken.flowToken.sourceNode, QFlowToken.flowToken.flowInstanceId)
      .values(idGenerator.generateId().stringValue(), nodeId.stringValue(), sourceNodeId.orElse(null), instanceId.stringValue()).execute();
  }

  @Override
  public void removeToken(Identifier tokenId) {
    database.delete(QFlowToken.flowToken)
      .where(
        QFlowToken.flowToken.id.eq(tokenId.stringValue())
      ).execute();
  }

  @Override
  public Identifier createInstance() {
    Identifier identifier = idGenerator.generateId();
    database.insert(QFlowInstance.flowInstance)
      .columns(QFlowInstance.flowInstance.id)
      .values(identifier).execute();
    return identifier;
  }

}
