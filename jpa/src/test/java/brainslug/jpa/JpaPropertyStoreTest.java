package brainslug.jpa;

import brainslug.flow.definition.Identifier;
import brainslug.flow.context.FlowProperties;
import brainslug.util.IdUtil;
import brainslug.util.UuidGenerator;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static brainslug.flow.execution.property.ExecutionProperties.newProperties;

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
    jpaPropertyStore.storeProperties(instanceId, newProperties().with("stringTest", "value"));

    FlowProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.getValue("stringTest", String.class)).isEqualTo("value");
  }

  @Test
  public void shouldDoubleProperty() throws Exception {
    jpaPropertyStore.storeProperties(instanceId, newProperties().with("doubleTest", 1.2));

    FlowProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.getValue("doubleTest", Double.class)).isEqualTo(1.2);
  }

  @Test
  public void shouldUpdateProperty() throws Exception {
    jpaPropertyStore.storeProperties(instanceId, newProperties().with("doubleTest", 1.2));
    database.flush();
    Assertions.assertThat(jpaPropertyStore.getProperty(instanceId, "doubleTest").getVersion()).isEqualTo(0);

    jpaPropertyStore.storeProperties(instanceId, newProperties().with("doubleTest", 1.3));
    database.flush();
    Assertions.assertThat(jpaPropertyStore.getProperty(instanceId, "doubleTest").getVersion()).isEqualTo(1);

    FlowProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.getValues().size()).isEqualTo(1);
    Assertions.assertThat(loadedProperties.getValue("doubleTest", Double.class)).isEqualTo(1.3);
  }

}