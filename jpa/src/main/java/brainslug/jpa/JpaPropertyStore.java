package brainslug.jpa;

import brainslug.flow.context.ExecutionProperty;
import brainslug.flow.context.FlowProperties;
import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.property.*;
import brainslug.flow.execution.property.store.PropertyStore;
import brainslug.jpa.entity.InstancePropertyEntity;
import brainslug.jpa.entity.query.QInstancePropertyEntity;
import brainslug.jpa.util.ObjectSerializer;
import brainslug.util.IdGenerator;
import com.mysema.query.types.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static brainslug.jpa.entity.InstancePropertyEntity.ValueType.*;

public class JpaPropertyStore implements PropertyStore {

  private Logger log = LoggerFactory.getLogger(JpaPropertyStore.class);
  private ObjectSerializer serializer = new ObjectSerializer();

  protected final Database database;
  protected final IdGenerator idGenerator;

  public JpaPropertyStore(Database database, IdGenerator idGenerator) {
    this.database = database;
    this.idGenerator = idGenerator;
  }

  @Override
  public void storeProperties(Identifier<?> instanceId, FlowProperties<ExecutionProperty> executionProperties) {
    log.debug("storing properties {} for instance {}", executionProperties, instanceId);

    for (ExecutionProperty property : executionProperties.getValues()) {
      InstancePropertyEntity instanceProperty = getOrCreatePropertyEntity(instanceId, property);
      database.insertOrUpdate(withPropertyValue(instanceProperty, property));
    }
  }

  protected InstancePropertyEntity getOrCreatePropertyEntity(Identifier<?> instanceId, ExecutionProperty property) {
    InstancePropertyEntity instanceProperty = getProperty(instanceId, property.getKey());

    if (instanceProperty != null) {
      return instanceProperty;
    } else {
      return newInstancePropertyEntity(instanceId, property);
    }
  }

  InstancePropertyEntity getProperty(Identifier<?> instanceId, String propertyKey) {
    return database.query()
        .from(QInstancePropertyEntity.instancePropertyEntity)
        .where(
          QInstancePropertyEntity.instancePropertyEntity.propertyKey.eq(propertyKey),
          QInstancePropertyEntity.instancePropertyEntity.instanceId.eq(instanceId.stringValue())
        ).singleResult(QInstancePropertyEntity.instancePropertyEntity);
  }

  protected InstancePropertyEntity newInstancePropertyEntity(Identifier<?> instanceId, ExecutionProperty property) {
    Identifier newId = idGenerator.generateId();
    Long created = new Date().getTime();

    return new InstancePropertyEntity()
      .withId(newId.stringValue())
      .withCreated(created)
      .withInstanceId(instanceId.stringValue())
      .withPropertyKey(property.getKey());
  }

  @Override
  public FlowProperties loadProperties(Identifier<?> instanceId) {
    return new ExecutionProperties().fromList(
      database.query().from(QInstancePropertyEntity.instancePropertyEntity)
        .where(QInstancePropertyEntity.instancePropertyEntity.instanceId.eq(instanceId.stringValue()))
        .list(typedPropertyFactoryExpression())
    );
  }

  protected Expression<ExecutionProperty> typedPropertyFactoryExpression() {
    return new JpaPropertyFactoryExpression(serializer)
      .withArgs(QInstancePropertyEntity.instancePropertyEntity.propertyKey,
        QInstancePropertyEntity.instancePropertyEntity.valueType,
        QInstancePropertyEntity.instancePropertyEntity.stringValue,
        QInstancePropertyEntity.instancePropertyEntity.longValue,
        QInstancePropertyEntity.instancePropertyEntity.doubleValue);
  }

  protected InstancePropertyEntity withPropertyValue(InstancePropertyEntity entity, ExecutionProperty<?> property) {
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
      return entity.withStringValue(getSerializer().serialize(property.getValue()))
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
