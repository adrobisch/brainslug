package brainslug.jdbc;

import brainslug.flow.execution.Token;
import brainslug.flow.execution.TokenStore;
import brainslug.flow.model.Identifier;
import brainslug.jdbc.tables.QBsToken;

import java.util.List;
import java.util.Map;

public class JdbcTokenStore implements TokenStore {

  private final Database database;

  public JdbcTokenStore(Database database) {
    this.database = database;
  }

  @Override
  public Map<Identifier, List<Token>> getTokens(Identifier nodeId, Identifier instanceId) {
    throw new UnsupportedOperationException("not implemented yet");
  }

  @Override
  public void addToken(Identifier instanceId, Identifier nodeId, Token token) {
    database.insert(QBsToken.bsToken)
      .columns(QBsToken.bsToken.currentNode, QBsToken.bsToken.sourceNode, QBsToken.bsToken.flowInstanceId)
      .values(nodeId, token.getSourceNode().stringValue(), instanceId);
  }

  @Override
  public void removeToken(Identifier instanceId, Identifier nodeId, Token sourceNodeId) {
    throw new UnsupportedOperationException("not implemented yet");
  }

  @Override
  public void createInstance(Identifier instanceId) {
    throw new UnsupportedOperationException("not implemented yet");
  }

}
