package brainslug.jpa;

import brainslug.flow.definition.Identifier;
import brainslug.flow.definition.StringIdentifier;
import brainslug.flow.execution.token.Token;
import brainslug.flow.execution.token.TokenList;
import brainslug.flow.execution.token.TokenStore;
import brainslug.flow.instance.FlowInstanceToken;
import brainslug.flow.instance.FlowInstanceTokenList;
import brainslug.jpa.entity.FlowTokenEntity;
import brainslug.jpa.entity.QFlowTokenEntity;
import brainslug.util.IdGenerator;
import brainslug.util.Option;
import com.mysema.query.types.ConstructorExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class JpaTokenStore implements TokenStore {

  private Logger log = LoggerFactory.getLogger(JpaTokenStore.class);

  private final Database database;
  private final IdGenerator idGenerator;

  public JpaTokenStore(Database database, IdGenerator idGenerator) {
    this.database = database;
    this.idGenerator = idGenerator;
  }

  @Override
  public FlowInstanceTokenList getInstanceTokens(Identifier instanceId) {
    return new TokenList(tokensForInstance(instanceId));
  }

  private List<Token> tokensForInstance(Identifier instanceId) {
    return database.query().from(QFlowTokenEntity.flowTokenEntity)
      .where(
        QFlowTokenEntity.flowTokenEntity.flowInstanceId.eq(instanceId.stringValue()),
        QFlowTokenEntity.flowTokenEntity.isDead.eq(0)
      )
      .list(ConstructorExpression.create(Token.class,
              QFlowTokenEntity.flowTokenEntity.id,
              QFlowTokenEntity.flowTokenEntity.currentNode,
              QFlowTokenEntity.flowTokenEntity.sourceNode,
              QFlowTokenEntity.flowTokenEntity.flowInstanceId,
              QFlowTokenEntity.flowTokenEntity.isDead,
              QFlowTokenEntity.flowTokenEntity.isFinal));
  }

  @Override
  public FlowInstanceTokenList getNodeTokens(Identifier nodeId, Identifier instanceId) {
    return new TokenList(tokensForNode(nodeId, instanceId));
  }

  List<Token> tokensForNode(Identifier nodeId, Identifier instanceId) {
    return database.query().from(QFlowTokenEntity.flowTokenEntity)
        .where(
          QFlowTokenEntity.flowTokenEntity.flowInstanceId.eq(instanceId.stringValue()),
          QFlowTokenEntity.flowTokenEntity.currentNode.eq(nodeId.stringValue()),
          QFlowTokenEntity.flowTokenEntity.isDead.eq(0)
        )
        .list(ConstructorExpression.create(Token.class,
                QFlowTokenEntity.flowTokenEntity.id,
                QFlowTokenEntity.flowTokenEntity.currentNode,
                QFlowTokenEntity.flowTokenEntity.sourceNode,
                QFlowTokenEntity.flowTokenEntity.flowInstanceId,
                QFlowTokenEntity.flowTokenEntity.isDead,
                QFlowTokenEntity.flowTokenEntity.isFinal));
  }

  @Override
  public FlowInstanceToken addToken(Identifier instanceId, Identifier nodeId, Option<Identifier> sourceNodeId, boolean isFinal) {
    Identifier tokenId = idGenerator.generateId();

    database.insertOrUpdate(new FlowTokenEntity()
      .withId(tokenId.stringValue())
      .withCreated(new Date().getTime())
      .withCurrentNode(nodeId.stringValue())
      .withSourceNode(sourceNodeId.orElse(new StringIdentifier(null)).stringValue())
      .withFlowInstanceId(instanceId.stringValue())
      .withIsDead(0)
    );

    return new Token(tokenId, nodeId, sourceNodeId, Option.of(instanceId), false, isFinal);
  }

  @Override
  public boolean removeToken(Identifier instanceId, Identifier tokenId) {
    log.debug("removing token: {}", tokenId.stringValue());

    Long updatedCount = database.update(QFlowTokenEntity.flowTokenEntity)
      .where(
        QFlowTokenEntity.flowTokenEntity.id.eq(tokenId.stringValue()),
        QFlowTokenEntity.flowTokenEntity.flowInstanceId.eq(instanceId.stringValue())
      ).set(QFlowTokenEntity.flowTokenEntity.isDead, 1)
      .execute();

    return updatedCount > 0;
  }

}
