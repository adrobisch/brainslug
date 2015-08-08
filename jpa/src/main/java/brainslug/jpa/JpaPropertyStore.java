package brainslug.jpa;

import brainslug.flow.instance.FlowInstanceProperty;
import brainslug.flow.instance.FlowInstanceProperties;
import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.property.*;
import brainslug.flow.execution.property.store.PropertyStore;
import brainslug.jpa.entity.FlowInstanceEntity;
import brainslug.jpa.entity.InstancePropertyEntity;
import brainslug.jpa.entity.QInstancePropertyEntity;
import brainslug.jpa.util.ObjectSerializer;
import brainslug.util.IdGenerator;
import brainslug.util.Option;
import com.mysema.query.jpa.impl.JPAQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;

import static brainslug.jpa.entity.InstancePropertyEntity.ValueType.*;

public class JpaPropertyStore implements PropertyStore {

  private Logger log = LoggerFactory.getLogger(JpaPropertyStore.class);
  private ObjectSerializer serializer = new ObjectSerializer();

  protected final Database database;
  protected final IdGenerator idGenerator;
  private final JpaInstanceStore jpaInstanceStore;

  public JpaPropertyStore(Database database, IdGenerator idGenerator, JpaInstanceStore jpaInstanceStore) {
    this.database = database;
    this.idGenerator = idGenerator;
    this.jpaInstanceStore = jpaInstanceStore;
  }

  @Override
  public void setProperty(Identifier<?> instanceId, FlowInstanceProperty<?> property) {
    InstancePropertyEntity instanceProperty = getOrCreatePropertyEntity(instanceId, property);
    database.insertOrUpdate(withPropertyValue(instanceProperty, property));
  }

  @Override
  public void setProperties(Identifier<?> instanceId, FlowInstanceProperties<?, FlowInstanceProperty<?>> executionProperties) {
    log.debug("storing properties {} for instance {}", executionProperties, instanceId);

    for (FlowInstanceProperty property : executionProperties.getValues()) {
      setProperty(instanceId, property);
    }
  }

  @Override
  public Option<FlowInstanceProperty<?>> getProperty(Identifier<?> instanceId, Identifier<?> key) {
    FlowInstanceProperty<?> propertyEntity = (FlowInstanceProperty) propertyQuery(instanceId, key.stringValue()).singleResult();
    return Option.<FlowInstanceProperty<?>>of(propertyEntity);
  }

  JPAQuery propertyQuery(Identifier<?> instanceId, String propertyKey) {
    return database.query()
            .from(QInstancePropertyEntity.instancePropertyEntity)
            .where(
                    QInstancePropertyEntity.instancePropertyEntity.propertyKey.eq(propertyKey),
                    QInstancePropertyEntity.instancePropertyEntity.instanceId.eq(instanceId.stringValue())
            );
  }

  protected InstancePropertyEntity getOrCreatePropertyEntity(Identifier<?> instanceId, FlowInstanceProperty property) {
    FlowInstanceEntity instance = jpaInstanceStore.findInstanceById(instanceId);
    Option<InstancePropertyEntity> instanceProperty = propertyEntity(instance, property.getKey());

    if (instanceProperty.isPresent()) {
      return instanceProperty.get();
    } else {
      InstancePropertyEntity newProperty = newInstancePropertyEntity(instanceId, property);
      instance.getPropertiesEntities().add(newProperty);
      return newProperty;
    }
  }

  Option<InstancePropertyEntity> propertyEntity(FlowInstanceEntity instanceEntity, String propertyKey) {
    for (InstancePropertyEntity propertyEntity : instanceEntity.getPropertiesEntities()) {
      if (propertyEntity.getKey().equals(propertyKey)) {
        return Option.of(propertyEntity);
      }
    }
    return Option.empty();
  }

  protected InstancePropertyEntity newInstancePropertyEntity(Identifier<?> instanceId, FlowInstanceProperty property) {
    Identifier newId = idGenerator.generateId();
    Long created = new Date().getTime();

    return new InstancePropertyEntity()
      .withId(newId.stringValue())
      .withCreated(created)
      .withInstanceId(instanceId.stringValue())
      .withPropertyKey(property.getKey());
  }

  @Override
  public FlowInstanceProperties<?, FlowInstanceProperty<?>> getProperties(Identifier<?> instanceId) {
    return new ExecutionProperties().from(
            database.query().from(QInstancePropertyEntity.instancePropertyEntity)
                    .where(QInstancePropertyEntity.instancePropertyEntity.instanceId.eq(instanceId.stringValue()))
                    .list(QInstancePropertyEntity.instancePropertyEntity)
    );
  }

  protected InstancePropertyEntity withPropertyValue(InstancePropertyEntity entity, FlowInstanceProperty<?> property) {
    if (property instanceof StringProperty) {
      return entity.withStringValue(((StringProperty) property).getValue())
        .withValueType(STRING.typeName());
    } else if (property instanceof LongProperty) {
      return entity.withLongValue(((LongProperty) property).getValue())
        .withValueType(LONG.typeName());
    } else if (property instanceof BooleanProperty) {
      return entity.withLongValue(boolToLong((BooleanProperty) property))
        .withValueType(BOOLEAN.typeName());
    } else if (property instanceof IntProperty) {
      return entity.withLongValue(((IntProperty) property).getValue().longValue())
        .withValueType(INT.typeName());
    } else if (property instanceof FloatProperty) {
      return entity.withDoubleValue(Double.valueOf(((FloatProperty) property).getValue()))
        .withValueType(FLOAT.typeName());
    } else if (property instanceof DoubleProperty) {
      return entity.withDoubleValue(((DoubleProperty) property).getValue())
        .withValueType(DOUBLE.typeName());
    } else if (property instanceof DateProperty) {
      return entity.withLongValue(((DateProperty) property).getValue().getTime())
        .withValueType(DATE.typeName());
    } else {
      return entity.withBytesValue(getSerializer().serialize(property.getValue()))
        .withValueType(SERIALIZABLE.typeName());
    }
  }

  private long boolToLong(BooleanProperty property) {
    return property.getValue() ? 1l : 0l;
  }

  public ObjectSerializer getSerializer() {
    return serializer;
  }
}
