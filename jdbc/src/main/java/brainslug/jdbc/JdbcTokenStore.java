package brainslug.jdbc;

import brainslug.flow.context.IdGenerator;
import brainslug.flow.execution.Token;
import brainslug.flow.execution.impl.AbstractTokenStore;
import brainslug.flow.model.Identifier;
import brainslug.jdbc.tables.QFlowInstance;
import brainslug.jdbc.tables.QFlowToken;
import brainslug.util.Option;
import com.mysema.query.types.ConstructorExpression;

import java.util.*;

public class JdbcTokenStore extends AbstractTokenStore {

  private final Database database;
  private final IdGenerator idGenerator;

  public JdbcTokenStore(Database database, IdGenerator idGenerator) {
    this.database = database;
    this.idGenerator = idGenerator;
  }

  @Override
  public List<Token> getInstanceTokens(Identifier instanceId) {
    return database.query().from(QFlowToken.flowToken)
      .where(
        QFlowToken.flowToken.flowInstanceId.eq(instanceId.stringValue())
      )
      .list(ConstructorExpression.create(Token.class, QFlowToken.flowToken.id, QFlowToken.flowToken.currentNode));
  }

  @Override
  public Map<Identifier, List<Token>> tokensGroupedBySourceNode(Identifier nodeId, Identifier instanceId) {
    List<Token> nodeTokens = database.query().from(QFlowToken.flowToken)
      .where(
        QFlowToken.flowToken.flowInstanceId.eq(instanceId.stringValue()),
        QFlowToken.flowToken.currentNode.eq(nodeId.stringValue())
      )
      .list(ConstructorExpression.create(Token.class, QFlowToken.flowToken.id, QFlowToken.flowToken.sourceNode));

    return sourceNodeMap(nodeId, nodeTokens);
  }

  @Override
  public Token addToken(Identifier instanceId, Identifier nodeId, Option<Identifier> sourceNodeId) {
    Identifier tokenId = idGenerator.generateId();
    database.insert(QFlowToken.flowToken)
      .columns(QFlowToken.flowToken.id,
        QFlowToken.flowToken.currentNode,
        QFlowToken.flowToken.sourceNode,
        QFlowToken.flowToken.flowInstanceId,
        QFlowToken.flowToken.created
      )
      .values(tokenId.stringValue(),
        nodeId.stringValue(),
        sourceNodeId.orElse(null),
        instanceId.stringValue(),
        new Date().getTime()
      )
      .execute();
    return new Token(tokenId, nodeId, sourceNodeId, Option.of(instanceId));
  }

  @Override
  public void removeToken(Identifier instanceId, Identifier tokenId) {
    database.delete(QFlowToken.flowToken)
      .where(
        QFlowToken.flowToken.id.eq(tokenId.stringValue()),
        QFlowToken.flowToken.flowInstanceId.eq(instanceId.stringValue())
      )
      .execute();
  }

  @Override
  public Identifier createInstance(Identifier definitionId) {
    Identifier identifier = idGenerator.generateId();
    database.insert(QFlowInstance.flowInstance)
      .columns(QFlowInstance.flowInstance.id,
        QFlowInstance.flowInstance.definitionId,
        QFlowInstance.flowInstance.created
      )
      .values(identifier.stringValue(),
        definitionId.stringValue(),
        new Date().getTime()
      )
      .execute();

    return identifier;
  }

}
