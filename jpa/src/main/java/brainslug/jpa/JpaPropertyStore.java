package brainslug.jpa;

import brainslug.flow.context.ExecutionProperty;
import brainslug.flow.context.FlowProperties;
import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.property.ExecutionProperties;
import brainslug.flow.execution.property.PropertyStore;
import brainslug.flow.execution.property.basic.*;
import brainslug.jpa.entity.InstancePropertyEntity;
import brainslug.jpa.entity.query.QInstancePropertyEntity;
import brainslug.util.IdGenerator;
import com.mysema.query.types.Expression;
import com.mysema.query.types.FactoryExpressionBase;
import com.mysema.query.types.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    return new PropertyFactoryExpression()
      .withArgs(QInstancePropertyEntity.instancePropertyEntity.propertyKey,
        QInstancePropertyEntity.instancePropertyEntity.valueType,
        QInstancePropertyEntity.instancePropertyEntity.stringValue,
        QInstancePropertyEntity.instancePropertyEntity.longValue,
        QInstancePropertyEntity.instancePropertyEntity.doubleValue);
  }

  class PropertyFactoryExpression extends FactoryExpressionBase<ExecutionProperty> {
    private List<Expression<?>> args;

    public PropertyFactoryExpression() {
      super(ExecutionProperty.class);
    }

    public PropertyFactoryExpression withArgs(Expression<?>... args) {
      this.args = Arrays.asList(args);
      return this;
    }

    @Nullable
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, @Nullable C c) {
      return visitor.visit(this, c);
    }

    @Override
    public List<Expression<?>> getArgs() {
      return args;
    }

    @Nullable
    @Override
    public ExecutionProperty newInstance(Object... objects) {
      String key = (String) objects[0];
      String type = (String) objects[1];
      String stringValue = (String) objects[2];
      Long longValue = (Long) objects[3];
      Double doubleValue = (Double) objects[4];

      return createProperty(key, type, stringValue, longValue, doubleValue);
    }

    private ExecutionProperty createProperty(String key, String type, String stringValue, Long longValue, Double doubleValue) {
      if (type.equals("string")) {
        return new StringProperty(key, stringValue);
      } else if (type.equals("date")) {
        return new DateProperty(key, new Date(longValue));
      } else if (type.equals("long")) {
        return new LongProperty(key, longValue);
      } else if (type.equals("int")) {
        return new IntProperty(key, longValue.intValue());
      } else if (type.equals("double")) {
        return new DoubleProperty(key, doubleValue);
      } else if (type.equals("float")) {
        return new FloatProperty(key, doubleValue.floatValue());
      } else if (type.equals("boolean")) {
        return new BooleanProperty(key, longValue == 1);
      } else {
        return new ObjectProperty(key, deserialize(stringValue));
      }
    }
  }

  private Object deserialize(String stringValue) {
    try {
      ObjectInputStream s = new ObjectInputStream(new ByteArrayInputStream(stringValue.getBytes()));
      return s.readObject();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException();
    }
  }

  protected InstancePropertyEntity withPropertyValue(InstancePropertyEntity entity, ExecutionProperty<?> property) {
    if (property instanceof StringProperty) {
      return entity.withStringValue(((StringProperty) property).getValue())
        .withValueType("string");
    } else if (property instanceof LongProperty) {
      return entity.withLongValue(((LongProperty) property).getValue())
        .withValueType("long");
    } else if (property instanceof BooleanProperty) {
      return entity.withLongValue(boolToLong((BooleanProperty) property))
        .withValueType("boolean");
    } else if (property instanceof IntProperty) {
      return entity.withLongValue(((IntProperty) property).getValue().longValue())
        .withValueType("int");
    } else if (property instanceof FloatProperty) {
      return entity.withDoubleValue(Double.valueOf(((FloatProperty) property).getValue()))
        .withValueType("float");
    } else if (property instanceof DoubleProperty) {
      return entity.withDoubleValue(((DoubleProperty) property).getValue())
        .withValueType("double");
    } else if (property instanceof DateProperty) {
      return entity.withLongValue(((DateProperty) property).getValue().getTime())
        .withValueType("date");
    } else {
      return entity.withStringValue(serializeObject(property.getValue()))
        .withValueType(property.getClass().getName());
    }
  }

  private String serializeObject(Object value) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
      objectOutputStream.writeObject(value);
      objectOutputStream.flush();
      objectOutputStream.close();
      return new String(outputStream.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private long boolToLong(BooleanProperty property) {
    return property.getValue() ? 1l : 0l;
  }
}
