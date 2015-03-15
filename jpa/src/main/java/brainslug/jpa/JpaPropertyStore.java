package brainslug.jpa;

import brainslug.flow.Identifier;
import brainslug.flow.context.FlowProperties;
import brainslug.flow.context.ExecutionProperty;
import brainslug.flow.execution.ExecutionProperties;
import brainslug.flow.execution.BrainslugProperty;
import brainslug.flow.execution.PropertyStore;
import brainslug.jpa.entity.InstancePropertyEntity;
import brainslug.jpa.entity.query.QInstancePropertyEntity;
import brainslug.util.IdGenerator;
import com.mysema.query.types.ConstructorExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JpaPropertyStore implements PropertyStore {

  private Logger log = LoggerFactory.getLogger(JpaPropertyStore.class);

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
      database.insertOrUpdate(setValueField(instanceProperty, property.getObjectValue()));
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
        .list(ConstructorExpression.create(BrainslugProperty.class,
            QInstancePropertyEntity.instancePropertyEntity.propertyKey,
            QInstancePropertyEntity.instancePropertyEntity.valueType,
            QInstancePropertyEntity.instancePropertyEntity.stringValue,
            QInstancePropertyEntity.instancePropertyEntity.longValue,
            QInstancePropertyEntity.instancePropertyEntity.doubleValue,
            QInstancePropertyEntity.instancePropertyEntity.byteArrayValue
          )
        )
    );
  }

  public InstancePropertyEntity setValueField(InstancePropertyEntity entity, Object value) {
    if (value instanceof String) {
      entity.withStringValue((String) value);
    } else if (value instanceof Long) {
      entity.withLongValue((Long) value);
    } else if (value instanceof Double) {
      entity.withDoubleValue((Double) value);
    } else if (value instanceof Float) {
      entity.withDoubleValue(Double.valueOf((Float) value));
    } else if (value instanceof Date) {
      entity.withLongValue(((Date) value).getTime());
    } else if (value instanceof byte[]) {
      entity.withByteArrayValue((byte[]) value);
    }

    return entity.withValueType(value.getClass().getName());
  }
}
