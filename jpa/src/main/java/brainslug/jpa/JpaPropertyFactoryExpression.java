package brainslug.jpa;

import brainslug.flow.context.ExecutionProperty;
import brainslug.flow.execution.property.*;
import brainslug.jpa.entity.InstancePropertyEntity;
import brainslug.jpa.util.ObjectSerializer;
import com.mysema.query.types.Expression;
import com.mysema.query.types.FactoryExpressionBase;
import com.mysema.query.types.Visitor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static brainslug.jpa.entity.InstancePropertyEntity.ValueType.*;

class JpaPropertyFactoryExpression extends FactoryExpressionBase<ExecutionProperty<?>> {
  private final ObjectSerializer serializer;
  private List<Expression<?>> args;

  public JpaPropertyFactoryExpression(ObjectSerializer serializer, Class<? extends ExecutionProperty<?>> type) {
    super(type);
    this.serializer = serializer;
  }

  public JpaPropertyFactoryExpression withArgs(Expression<?>... args) {
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
    InstancePropertyEntity.ValueType valueType = new InstancePropertyEntity.ValueType(type);

    if (valueType.equals(STRING)) {
      return new StringProperty(key, stringValue);
    } else if(valueType.equals(LONG)) {
      return new LongProperty(key, longValue);
    } else if(valueType.equals(DATE)) {
      return new DateProperty(key, new Date(longValue));
    } else if(valueType.equals(INT)) {
      return new IntProperty(key, longValue.intValue());
    } else if(valueType.equals(DOUBLE)) {
      return new DoubleProperty(key, doubleValue);
    } else if(valueType.equals(FLOAT)) {
      return new FloatProperty(key, doubleValue.floatValue());
    } else if(valueType.equals(BOOLEAN)) {
      return new BooleanProperty(key, longValue == 1);
    } else if(valueType.equals(SERIALIZABLE)) {
      return new ObjectProperty(key, serializer.deserialize(stringValue));
    } else {
      throw new IllegalArgumentException("unhandled value type:" + valueType);
    }
  }

}
