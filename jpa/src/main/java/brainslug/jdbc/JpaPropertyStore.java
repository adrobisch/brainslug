package brainslug.jdbc;

import brainslug.flow.Identifier;
import brainslug.flow.execution.ExecutionProperties;
import brainslug.flow.execution.ExecutionProperty;
import brainslug.flow.execution.PropertyStore;
import brainslug.jdbc.entity.InstancePropertyEntity;
import brainslug.jdbc.entity.query.QInstancePropertyEntity;
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
  public void storeProperties(Identifier<?> instanceId, ExecutionProperties executionProperties) {
    log.debug("storing properties {} for instance {}", executionProperties, instanceId);

    for (ExecutionProperty property : executionProperties.getValues()) {
      Identifier newId = idGenerator.generateId();
      Long created = new Date().getTime();

      InstancePropertyEntity instanceProperty = new InstancePropertyEntity()
        .withId(newId.stringValue())
        .withCreated(created)
        .withInstanceId(instanceId.stringValue())
        .withPropertyKey(property.getKey());

      database.insertOrUpdate(setValueField(instanceProperty, property.getObjectValue()));
    }
  }

  @Override
  public ExecutionProperties loadProperties(Identifier<?> instanceId) {
    return new ExecutionProperties().fromList(
      database.query().from(QInstancePropertyEntity.instancePropertyEntity)
        .where(QInstancePropertyEntity.instancePropertyEntity.instanceId.eq(instanceId.stringValue()))
        .list(ConstructorExpression.create(ExecutionProperty.class,
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
