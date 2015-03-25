package brainslug.jpa.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "BS_INSTANCE_PROPERTY")
public class InstancePropertyEntity {
  @Id
  @Column(name = "_ID")
  protected String id;

  @NotNull
  @Column(name = "_CREATED")
  protected Long created;

  @Version
  @Column(name = "_VERSION")
  protected Long version;

  @Column(name = "_INSTANCE_ID")
  protected String instanceId;

  @Column(name = "_VALUE_TYPE")
  protected String valueType;

  @Column(name = "_PROPERTY_KEY")
  protected String propertyKey;

  @Column(name = "_STRING_VALUE")
  protected String stringValue;

  @Column(name = "_LONG_VALUE")
  protected Long longValue;

  @Column(name = "_DOUBLE_VALUE")
  protected Double doubleValue;

  public String getId() {
    return id;
  }

  public InstancePropertyEntity withId(String id) {
    this.id = id;
    return this;
  }

  public Long getCreated() {
    return created;
  }

  public InstancePropertyEntity withCreated(Long created) {
    this.created = created;
    return this;
  }

  public Long getVersion() {
    return version;
  }

  public InstancePropertyEntity withVersion(Long version) {
    this.version = version;
    return this;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public InstancePropertyEntity withInstanceId(String instanceId) {
    this.instanceId = instanceId;
    return this;
  }

  public String getValueType() {
    return valueType;
  }

  public InstancePropertyEntity withValueType(String valueType) {
    this.valueType = valueType;
    return this;
  }

  public String getPropertyKey() {
    return propertyKey;
  }

  public InstancePropertyEntity withPropertyKey(String propertyKey) {
    this.propertyKey = propertyKey;
    return this;
  }

  public String getStringValue() {
    return stringValue;
  }

  public InstancePropertyEntity withStringValue(String stringValue) {
    this.stringValue = stringValue;
    return this;
  }

  public Long getLongValue() {
    return longValue;
  }

  public InstancePropertyEntity withLongValue(Long longValue) {
    this.longValue = longValue;
    return this;
  }

  public Double getDoubleValue() {
    return doubleValue;
  }

  public InstancePropertyEntity withDoubleValue(Double doubleValue) {
    this.doubleValue = doubleValue;
    return this;
  }

  public static class ValueType {
    public static final ValueType STRING = new ValueType("string");
    public static final ValueType DATE = new ValueType("date");
    public static final ValueType INT = new ValueType("int");
    public static final ValueType DOUBLE = new ValueType("double");
    public static final ValueType FLOAT = new ValueType("float");
    public static final ValueType BOOLEAN = new ValueType("boolean");
    public static final ValueType SERIALIZABLE = new ValueType("serializable");
    public static final ValueType LONG = new ValueType("long");

    String typeName;

    public ValueType(String typeName) {
      this.typeName = typeName;
    }

    public String typeName() {
      return typeName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ValueType valueType = (ValueType) o;

      if (typeName != null ? !typeName.equals(valueType.typeName) : valueType.typeName != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return typeName != null ? typeName.hashCode() : 0;
    }
  }

}
