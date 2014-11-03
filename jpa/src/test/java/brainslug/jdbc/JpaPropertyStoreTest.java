package brainslug.jdbc;

import brainslug.flow.Identifier;
import brainslug.flow.execution.ExecutionProperties;
import brainslug.util.IdUtil;
import brainslug.util.UuidGenerator;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class JpaPropertyStoreTest extends AbstractDatabaseTest {
  @Test
  public void shouldStoreStringProperty() throws Exception {
    JpaTokenStore jpaTokenStore = new JpaTokenStore(database, new UuidGenerator());
    Identifier instanceId = jpaTokenStore.createInstance(IdUtil.id("definitionId"));

    JpaPropertyStore jpaPropertyStore = new JpaPropertyStore(database, new UuidGenerator());
    jpaPropertyStore.storeProperties(instanceId, ExecutionProperties.with("stringTest", "value"));

    ExecutionProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.get("stringTest", String.class)).isEqualTo("value");
  }

  @Test
  public void shouldDoubleProperty() throws Exception {
    JpaTokenStore jpaTokenStore = new JpaTokenStore(database, new UuidGenerator());
    Identifier instanceId = jpaTokenStore.createInstance(IdUtil.id("definitionId"));

    JpaPropertyStore jpaPropertyStore = new JpaPropertyStore(database, new UuidGenerator());
    jpaPropertyStore.storeProperties(instanceId, ExecutionProperties.with("doubleTest", 1.2));

    ExecutionProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.get("doubleTest", Double.class)).isEqualTo(1.2);
  }

}