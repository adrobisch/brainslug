package brainslug.jpa;

import brainslug.flow.execution.instance.FlowInstanceProperties;
import static brainslug.util.IdUtil.*;

import brainslug.jpa.entity.FlowInstanceEntity;
import brainslug.util.UuidGenerator;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static brainslug.flow.execution.property.ExecutionProperties.newProperties;
import static java.util.Arrays.asList;

public class JpaPropertyStoreIT extends AbstractDatabaseTest {
  JpaTokenStore jpaTokenStore;
  FlowInstanceEntity instance;
  JpaPropertyStore jpaPropertyStore;
  JpaInstanceStore jpaInstanceStore;

  @Before
  public void setup() {
    UuidGenerator idGenerator = new UuidGenerator();
    jpaTokenStore = new JpaTokenStore(database, idGenerator, jpaInstanceStore);
    jpaInstanceStore = new JpaInstanceStore(database, idGenerator);
    instance = (FlowInstanceEntity) jpaInstanceStore.createInstance(id("definitionId"));
    jpaPropertyStore = new JpaPropertyStore(database, idGenerator, jpaInstanceStore);
  }

  @Test
  public void shouldStoreStringProperty() throws Exception {
    jpaPropertyStore.setProperties(instance.getIdentifier(), newProperties().with("stringTest", "value"));

    FlowInstanceProperties loadedProperties = jpaPropertyStore.getProperties(instance.getIdentifier());
    Assertions.assertThat(loadedProperties.value("stringTest", String.class)).isEqualTo("value");
  }

  @Test
  public void shouldStoreDoubleProperty() throws Exception {
    jpaPropertyStore.setProperties(instance.getIdentifier(), newProperties().with("doubleTest", 1.2));

    FlowInstanceProperties loadedProperties = jpaPropertyStore.getProperties(instance.getIdentifier());
    Assertions.assertThat(loadedProperties.value("doubleTest", Double.class)).isEqualTo(1.2);
  }

  @Test
  public void shouldStoreFloatProperty() throws Exception {
    jpaPropertyStore.setProperties(instance.getIdentifier(), newProperties().with("floatTest", 1.2f));

    FlowInstanceProperties loadedProperties = jpaPropertyStore.getProperties(instance.getIdentifier());
    Assertions.assertThat(loadedProperties.value("floatTest", Float.class)).isEqualTo(1.2f);
  }

  @Test
  public void shouldStoreLongProperty() throws Exception {
    jpaPropertyStore.setProperties(instance.getIdentifier(), newProperties().with("longTest", 12l));

    FlowInstanceProperties loadedProperties = jpaPropertyStore.getProperties(instance.getIdentifier());
    Assertions.assertThat(loadedProperties.value("longTest", Long.class)).isEqualTo(12l);
  }

  @Test
  public void shouldStoreBooleanProperty() throws Exception {
    jpaPropertyStore.setProperties(instance.getIdentifier(), newProperties().with("booleanTest", true));

    FlowInstanceProperties loadedProperties = jpaPropertyStore.getProperties(instance.getIdentifier());
    Assertions.assertThat(loadedProperties.value("booleanTest", Boolean.class)).isEqualTo(true);
  }

  @Test
  public void shouldStoreIntProperty() throws Exception {
    jpaPropertyStore.setProperties(instance.getIdentifier(), newProperties().with("intTest", 1));

    FlowInstanceProperties loadedProperties = jpaPropertyStore.getProperties(instance.getIdentifier());
    Assertions.assertThat(loadedProperties.value("intTest", Double.class)).isEqualTo(1);
  }

  @Test
  public void shouldStoreSerializable() throws Exception {
    jpaPropertyStore.setProperties(instance.getIdentifier(), newProperties().with("serializableTest", asList("1", "2")));

    FlowInstanceProperties loadedProperties = jpaPropertyStore.getProperties(instance.getIdentifier());
    List list = (List) loadedProperties.value("serializableTest", List.class);

    Assertions.assertThat(list).contains("1", "2");
  }

  @Test
  public void shouldUpdateProperty() throws Exception {
    jpaPropertyStore.setProperties(instance.getIdentifier(), newProperties().with("doubleTest", 1.2));
    database.flush();
    Assertions.assertThat(jpaPropertyStore.propertyEntity(instance, "doubleTest").get().getVersion()).isEqualTo(0);

    jpaPropertyStore.setProperties(instance.getIdentifier(), newProperties().with("doubleTest", 1.3));
    database.flush();
    Assertions.assertThat(jpaPropertyStore.propertyEntity(instance, "doubleTest").get().getVersion()).isEqualTo(1);

    FlowInstanceProperties loadedProperties = jpaPropertyStore.getProperties(instance.getIdentifier());
    Assertions.assertThat(loadedProperties.values().size()).isEqualTo(1);
    Assertions.assertThat(loadedProperties.value("doubleTest", Double.class)).isEqualTo(1.3);
  }

}