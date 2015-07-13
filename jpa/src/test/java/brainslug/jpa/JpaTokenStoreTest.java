package brainslug.jpa;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.token.Token;
import brainslug.flow.instance.FlowInstanceTokenList;
import brainslug.util.Option;
import org.junit.Test;

import static brainslug.util.IdUtil.id;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class JpaTokenStoreTest extends AbstractDatabaseTest {

  Identifier instanceId = id("instance");
  Identifier flowId = id("flow");
  Identifier nodeId = id("node");
  Identifier tokenId = id("token");

  @Test
  public void shouldInsertInstanceAndToken() throws Exception {
    // given:
    JpaTokenStore jpaTokenStore = createJpaTokenStore();
    JpaInstanceStore jpaInstanceStore = createJpaInstanceStore();

    // when:
    Identifier instanceId = createInstanceWithSingleRootToken(jpaTokenStore, jpaInstanceStore);

    assertThat(instanceId).isNotNull();

    // then:
    FlowInstanceTokenList instanceTokens = jpaTokenStore.getInstanceTokens(instanceId);
    assertThat(instanceTokens.getActiveTokens())
      .contains(new Token(tokenId, nodeId, Option.<Identifier>empty(), Option.of(instanceId), false, false))
      .hasSize(1);
  }

  @Test
  public void shouldDeleteToken() throws Exception {
    // given:
    JpaTokenStore jpaTokenStore = createJpaTokenStore();
    JpaInstanceStore jpaInstanceStore = createJpaInstanceStore();

    Identifier instanceId = createInstanceWithSingleRootToken(jpaTokenStore, jpaInstanceStore);
    // when:
    jpaTokenStore.removeToken(instanceId, tokenId);

    assertThat(jpaTokenStore.getNodeTokens(nodeId, instanceId).getActiveTokens())
      .hasSize(0);
  }

  private Identifier createInstanceWithSingleRootToken(JpaTokenStore jpaTokenStore, JpaInstanceStore jpaInstanceStore) {
    when(idGeneratorMock.generateId()).thenReturn(instanceId);
    jpaInstanceStore.createInstance(flowId);

    when(idGeneratorMock.generateId()).thenReturn(tokenId);
    jpaTokenStore.addToken(instanceId, nodeId, Option.<Identifier>empty(), false);
    return instanceId;
  }

  JpaTokenStore createJpaTokenStore() {
    return new JpaTokenStore(database, idGeneratorMock);
  }

  JpaInstanceStore createJpaInstanceStore() {
    return new JpaInstanceStore(database, idGeneratorMock);
  }

}