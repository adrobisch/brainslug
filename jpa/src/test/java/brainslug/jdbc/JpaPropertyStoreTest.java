package brainslug.jdbc;

import brainslug.flow.Identifier;
import brainslug.flow.execution.ExecutionProperties;
import brainslug.util.IdUtil;
import brainslug.util.UuidGenerator;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class JpaPropertyStoreTest extends AbstractDatabaseTest {
  JpaTokenStore jpaTokenStore;
  Identifier instanceId;
  JpaPropertyStore jpaPropertyStore;

  @Before
  public void setup() {
    jpaTokenStore = new JpaTokenStore(database, new UuidGenerator());
    instanceId = jpaTokenStore.createInstance(IdUtil.id("definitionId"));
    jpaPropertyStore = new JpaPropertyStore(database, new UuidGenerator());
  }

  @Test
  public void shouldStoreStringProperty() throws Exception {
    jpaPropertyStore.storeProperties(instanceId, ExecutionProperties.with("stringTest", "value"));

    ExecutionProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.get("stringTest", String.class)).isEqualTo("value");
  }

  @Test
  public void shouldDoubleProperty() throws Exception {
    jpaPropertyStore.storeProperties(instanceId, ExecutionProperties.with("doubleTest", 1.2));

    ExecutionProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.get("doubleTest", Double.class)).isEqualTo(1.2);
  }

  @Test
  public void shouldUpdateProperty() throws Exception {
    jpaPropertyStore.storeProperties(instanceId, ExecutionProperties.with("doubleTest", 1.2));
    database.flush();
    Assertions.assertThat(jpaPropertyStore.getProperty(instanceId, "doubleTest").getVersion()).isEqualTo(0);

    jpaPropertyStore.storeProperties(instanceId, ExecutionProperties.with("doubleTest", 1.3));
    database.flush();
    Assertions.assertThat(jpaPropertyStore.getProperty(instanceId, "doubleTest").getVersion()).isEqualTo(1);

    ExecutionProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.getValues().size()).isEqualTo(1);
    Assertions.assertThat(loadedProperties.get("doubleTest", Double.class)).isEqualTo(1.3);
  }

}