package brainslug.jpa;

import brainslug.flow.Identifier;
import brainslug.flow.execution.token.Token;
import brainslug.flow.execution.token.TokenList;
import brainslug.util.Option;
import org.junit.Test;

import static brainslug.util.IdUtil.id;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class JpaTokenStoreTest extends AbstractDatabaseTest {

  Identifier instanceId = id("instance");
  Identifier nodeId = id("node");
  Identifier tokenId = id("token");
  Identifier flowId = id("flow");

  @Test
  public void shouldInsertInstanceAndToken() throws Exception {
    // given:
    JpaTokenStore jpaTokenStore = createJdbcTokenStore();

    // when:
    Identifier instanceId = createInstanceWithSingleRootToken(jpaTokenStore);

    assertThat(instanceId).isNotNull();

    // then:
    TokenList instanceTokens = jpaTokenStore.getInstanceTokens(instanceId);
    assertThat(instanceTokens.getTokens())
      .contains(new Token(tokenId, nodeId, Option.<Identifier>empty(), Option.of(instanceId), false))
      .hasSize(1);
  }

  @Test
  public void shouldDeleteToken() throws Exception {
    // given:
    JpaTokenStore jpaTokenStore = createJdbcTokenStore();
    Identifier instanceId = createInstanceWithSingleRootToken(jpaTokenStore);
    // when:
    jpaTokenStore.removeToken(instanceId, tokenId);

    assertThat(jpaTokenStore.getNodeTokens(nodeId, instanceId).getTokens())
      .hasSize(0);
  }

  private Identifier createInstanceWithSingleRootToken(JpaTokenStore jpaTokenStore) {
    when(idGeneratorMock.generateId()).thenReturn(instanceId);
    Identifier instanceId = jpaTokenStore.createInstance(flowId);

    when(idGeneratorMock.generateId()).thenReturn(tokenId);
    jpaTokenStore.addToken(instanceId, nodeId, Option.<Identifier>empty());
    return instanceId;
  }

  JpaTokenStore createJdbcTokenStore() {
    return new JpaTokenStore(database, idGeneratorMock);
  }

}