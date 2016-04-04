package brainslug.jpa;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.token.Token;
import brainslug.flow.execution.instance.FlowInstanceToken;
import brainslug.flow.execution.instance.FlowInstanceTokenList;
import brainslug.util.Option;
import org.junit.Test;

import java.util.List;

import static brainslug.util.IdUtil.id;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class JpaTokenStoreIT extends AbstractDatabaseTest {

  Identifier instanceId = id("instance");
  Identifier flowId = id("flow");
  Identifier nodeId = id("node");
  Identifier tokenId = id("token");

  @Test
  public void shouldInsertInstanceAndToken() throws Exception {
    // given:
    JpaInstanceStore jpaInstanceStore = createJpaInstanceStore();
    JpaTokenStore jpaTokenStore = createJpaTokenStore(jpaInstanceStore);

    // when:
    Identifier instanceId = createInstanceWithSingleRootToken(jpaTokenStore, jpaInstanceStore);

    assertThat(instanceId).isNotNull();

    // then:
    FlowInstanceTokenList instanceTokens = jpaTokenStore.getInstanceTokens(instanceId);
    assertThat(instanceTokens.getActiveTokens())
      .contains(new Token(tokenId, nodeId, Option.<Identifier>empty(), instanceId, false, false))
      .hasSize(1);
  }

  @Test
  public void shouldSetTokenDead() throws Exception {
    // given:
    JpaInstanceStore jpaInstanceStore = createJpaInstanceStore();
    JpaTokenStore jpaTokenStore = createJpaTokenStore(jpaInstanceStore);

    Identifier instanceId = createInstanceWithSingleRootToken(jpaTokenStore, jpaInstanceStore);
    // when:
    jpaTokenStore.setDead(instanceId, tokenId);

    assertThat(jpaTokenStore.getNodeTokens(nodeId, instanceId).getActiveTokens())
      .hasSize(0);
  }

  @Test
  public void shouldSetTokenFinal() throws Exception {
    // given:
    JpaInstanceStore jpaInstanceStore = createJpaInstanceStore();
    JpaTokenStore jpaTokenStore = createJpaTokenStore(jpaInstanceStore);

    Identifier instanceId = createInstanceWithSingleRootToken(jpaTokenStore, jpaInstanceStore);
    // when:
    jpaTokenStore.setFinal(instanceId, tokenId);

    List<FlowInstanceToken> activeTokens = jpaTokenStore.getNodeTokens(nodeId, instanceId).getActiveTokens();

    assertThat(activeTokens)
      .hasSize(1);

    assertThat(activeTokens.get(0).isFinal()).isTrue();
  }

  private Identifier createInstanceWithSingleRootToken(JpaTokenStore jpaTokenStore, JpaInstanceStore jpaInstanceStore) {
    when(idGeneratorMock.generateId()).thenReturn(instanceId);
    jpaInstanceStore.createInstance(flowId);

    when(idGeneratorMock.generateId()).thenReturn(tokenId);
    jpaTokenStore.addToken(instanceId, nodeId, Option.<Identifier>empty(), false);
    return instanceId;
  }

  JpaTokenStore createJpaTokenStore(JpaInstanceStore jpaInstanceStore) {
    return new JpaTokenStore(database, idGeneratorMock, jpaInstanceStore);
  }

  JpaInstanceStore createJpaInstanceStore() {
    return new JpaInstanceStore(database, idGeneratorMock);
  }

}