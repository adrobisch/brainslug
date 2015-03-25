package brainslug.jpa;

import brainslug.flow.context.FlowProperties;
import brainslug.flow.definition.Identifier;
import brainslug.util.IdUtil;
import brainslug.util.UuidGenerator;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static brainslug.flow.execution.property.ExecutionProperties.newProperties;
import static java.util.Arrays.asList;

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
  public void shouldStoreDoubleProperty() throws Exception {
    jpaPropertyStore.storeProperties(instanceId, newProperties().with("doubleTest", 1.2));

    FlowProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.getValue("doubleTest", Double.class)).isEqualTo(1.2);
  }

  @Test
  public void shouldStoreFloatProperty() throws Exception {
    jpaPropertyStore.storeProperties(instanceId, newProperties().with("floatTest", 1.2f));

    FlowProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.getValue("floatTest", Float.class)).isEqualTo(1.2f);
  }

  @Test
  public void shouldStoreLongProperty() throws Exception {
    jpaPropertyStore.storeProperties(instanceId, newProperties().with("longTest", 12l));

    FlowProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.getValue("longTest", Long.class)).isEqualTo(12l);
  }

  @Test
  public void shouldStoreBooleanProperty() throws Exception {
    jpaPropertyStore.storeProperties(instanceId, newProperties().with("booleanTest", true));

    FlowProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.getValue("booleanTest", Boolean.class)).isEqualTo(true);
  }

  @Test
  public void shouldStoreIntProperty() throws Exception {
    jpaPropertyStore.storeProperties(instanceId, newProperties().with("intTest", 1));

    FlowProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    Assertions.assertThat(loadedProperties.getValue("intTest", Double.class)).isEqualTo(1);
  }

  @Test
  public void shouldStoreSerializable() throws Exception {
    jpaPropertyStore.storeProperties(instanceId, newProperties().with("serializableTest", asList("1", "2")));

    FlowProperties loadedProperties = jpaPropertyStore.loadProperties(instanceId);
    List list = (List) loadedProperties.getValue("serializableTest", List.class);

    Assertions.assertThat(list).contains("1", "2");
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